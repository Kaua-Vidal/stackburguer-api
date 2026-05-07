package com.stackburguer.api.controllers;

import com.stackburguer.api.DTO.order.OrderRequestDTO;
import com.stackburguer.api.DTO.order.OrderResponseDTO;
import com.stackburguer.api.DTO.orderStatus.OrderStatusRequestDTO;
import com.stackburguer.api.models.User;
import com.stackburguer.api.service.OrderService;
import com.stackburguer.api.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> create(@RequestBody @Valid OrderRequestDTO dto) throws StripeException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        OrderResponseDTO response = orderService.createOrder(dto, user);

        String paymentUrl = paymentService.createPaymentIntent(response);

        OrderResponseDTO responseWithPayment = new OrderResponseDTO(
                response.id(),
                response.user(),
                response.products(),
                response.status(),
                response.createdAt(),
                paymentUrl
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(responseWithPayment);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> index(){
        List<OrderResponseDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    //Utilizamos Patch em vez de PUT pois estamos fazendo apenas uma alteração parcial
    //e não, mudando tudo
    @PatchMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> updateStatus(
            @PathVariable String id,
            @RequestBody OrderStatusRequestDTO statusDTO
            ){
        OrderResponseDTO response = orderService.updateStatus(id, statusDTO.status());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ){

        orderService.processStripeWebhook(payload, sigHeader);

        return ResponseEntity.ok().build();
    }


}
