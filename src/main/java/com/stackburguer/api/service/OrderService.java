package com.stackburguer.api.service;

import com.stackburguer.api.DTO.order.OrderRequestDTO;
import com.stackburguer.api.DTO.order.OrderResponseDTO;
import com.stackburguer.api.exceptions.OrderNotFoundException;
import com.stackburguer.api.exceptions.ProductNotFoundException;
import com.stackburguer.api.exceptions.WebHookFailException;
import com.stackburguer.api.models.User;
import com.stackburguer.api.models.order.Order;
import com.stackburguer.api.models.order.ProductItem;
import com.stackburguer.api.repositories.ProductRepository;
import com.stackburguer.api.repositories.UserRepository;
import com.stackburguer.api.repositories.OrderRepository;
import com.stackburguer.api.utils.EmailUtil;
import com.stackburguer.api.utils.WhatsappUtil;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;  //Interface que conversa com o Mongo

    @Autowired
    private ProductRepository productRepository;  //Interface que conversa com o Postgres

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private WhatsappUtil whatsappUtil;

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private UserRepository userRepository;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @Value("${url.api.product}")
    private String urlApiProduct;

    public OrderResponseDTO createOrder(OrderRequestDTO dto, User user) throws StripeException {
        List<ProductItem> items = dto.products().stream().map(itemRequest -> {
            var product = productRepository.findById(itemRequest.id())
                    .orElseThrow(() -> new ProductNotFoundException("Produto não encontrado: " + itemRequest.id()));

            return new ProductItem(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.getCategory().getName(),
                    urlApiProduct + product.getPath(),
                    itemRequest.quantity()
            );
        }).toList();


        Order order = new Order();
        order.setUser(user);
        order.setProducts(items);
        order.setStatus("Pedido realizado");

        Order savedOrder = orderRepository.save(order);

        OrderResponseDTO initialResponse = mapToResponseDTO(order, null);
        String url = paymentService.createPaymentIntent(initialResponse);

        return mapToResponseDTO(savedOrder, url);
    }

    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(order -> mapToResponseDTO(order, null))
                .toList();
    }

    private OrderResponseDTO mapToResponseDTO(Order order, String paymentUrl) {
        List<ProductItem> products = order.getProducts().stream()
                .map(product -> new ProductItem(
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        product.getCategory(), // <--- PEGUE APENAS O NOME AQUI!
                        product.getUrl(),
                        product.getQuantity()
                )).toList();

        return new OrderResponseDTO(
                order.getId(),
                order.getUser(),
                products,
                order.getStatus(),
                order.getCreatedAt(),
                paymentUrl
        );
    }

    public OrderResponseDTO updateStatus(String id, String status) {
        System.out.println("🚨 Status recebido do Front-end: ->" + status + "<-");

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Pedido não encontrado"));
        order.setStatus(status);

        mapToResponseDTO(orderRepository.save(order), null);

        String mensagem = String.format(
                "🍔 Olá, %s! O status do seu pedido no Stack Burguer mudou para: *%s*",
                order.getUser().getName(),
                status
        );

        if (order.getUser().getPhone() != null && order.getUser().getPhone().isEmpty()){
            String telefoneCliente = order.getUser().getPhone().trim();

            if (!telefoneCliente.startsWith("+")){
                telefoneCliente = "+55" + telefoneCliente;
            }

            try {
               whatsappUtil.enviarNotificacao(telefoneCliente, mensagem);
                System.out.println("🚀 Tentando enviar WhatsApp para: " + telefoneCliente);
            } catch (Exception e){
                System.err.println("Falha ao enviar WhatsApp: " + e.getMessage());
            }
        }


        enviarNotificacaoSeNecessario(status, order.getUser());

        return new OrderResponseDTO(order);
    }

    private void enviarNotificacaoSeNecessario(String status, User user){
        String mensagem = "";
        switch (status.toLowerCase()){

            case "pedido realizado":
                mensagem = "Fala, " + user.getName() + "! Seu pedido no Stack Burguer foi confirmado e já caiu no nosso sistema. Prepare a fome! \uD83C\uDF54\u2705";
                break;

            case "em preparação":
                mensagem = "Opa, " + user.getName() + "! A chapa tá quente! O chef já está preparando o seu lanche com muito capricho. \uD83D\uDC68\u200D\uD83C\uDF73\uD83D\uDD25";
                break;


            case "pedido pronto":
                mensagem = "Cheirinho de lanche pronto, " + user.getName() + "! \uD83E\uDD24 Seu pedido já está embalado e aguardando a coleta do entregador. Fique de olho!";
                break;

            case "pedido à caminho":
                mensagem = "Aqueça o estômago, " + user.getName() + "! O entregador acabou de sair do Stack Burguer com o seu pedido. Vai arrumando a mesa! \uD83D\uDEF5\uD83D\uDCA8";
                break;

            case "entregue":
                // \uD83C\uDF54 = 🍔 | \u2B50 = ⭐
                mensagem = "Missão cumprida, " + user.getName() + "! \uD83C\uDF54\u2728 Seu pedido foi entregue. Esperamos que seja uma experiência deliciosa. Depois conta pra gente o que achou! \u2B50";
                break;

            default:
                break;

        }

        if (!mensagem.isEmpty()) {
            whatsappUtil.enviarNotificacao(user.getPhone(), mensagem);
        }
    }


    public void processStripeWebhook(String payload, String sigHeader) {
        try {
            // 1. Usamos o Jackson (que o Spring já tem) para ler o texto bruto
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(payload);

            // 2. Pegamos o tipo do evento do JSON
            String eventType = root.path("type").asText();
            System.out.println("DEBUG: Evento recebido via Jackson: " + eventType);

            if ("checkout.session.completed".equals(eventType)) {
                // 3. Navegamos no JSON: data -> object -> client_reference_id
                JsonNode sessionNode = root.path("data").path("object");
                String orderId = sessionNode.path("client_reference_id").asText();

                System.out.println("DEBUG: ID extraído manualmente: " + orderId);

                // Verificamos se o ID não é nulo ou a string "null" (que o Jackson às vezes retorna)
                if (orderId != null && !orderId.isEmpty() && !orderId.equals("null")) {
                    this.updateStatus(orderId, "Pago");
                    System.out.println("SUCESSO: Pedido " + orderId + " atualizado para Pago.");
                } else {
                    System.err.println("ALERTA: O client_reference_id não foi encontrado no JSON!");
                }
            } else {
                System.out.println("Evento " + eventType + " ignorado.");
            }

        } catch (Exception e) {
            System.err.println("ERRO ao processar JSON manualmente: " + e.getMessage());
            throw new WebHookFailException("Falha no processamento do Webhook");
        }
    }
}
