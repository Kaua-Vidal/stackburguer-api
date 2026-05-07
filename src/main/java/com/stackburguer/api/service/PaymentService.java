package com.stackburguer.api.service;

import com.stackburguer.api.DTO.order.OrderRequestDTO;
import com.stackburguer.api.DTO.order.OrderResponseDTO;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class PaymentService {

    @Value("${stripe.api.key}")
    private String stripeSecretKey;

    public String createPaymentIntent(OrderResponseDTO order) throws StripeException {
        long totalCents = order.products().stream()
                .mapToLong(p -> (long) (p.getPrice() * p.getQuantity() * 100))
                .sum();
        return processPayment(totalCents, order.id().toString());
    }

    public String createPaymentIntent(OrderRequestDTO order) throws StripeException {
        long totalCents = order.products().stream()
                .mapToLong(p -> (long) (p.price() * p.quantity() * 100))
                .sum();
        return processPayment(totalCents, "NEW_ORDER");
    }

    public String processPayment(long totalAmountInCents, String orderId) throws StripeException{
        Stripe.apiKey = stripeSecretKey;


        //Criando o PaymentIntent em vez de uma Session
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(totalAmountInCents + 500)
                .setCurrency("brl")
                .putMetadata("order_id", orderId)
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )

                .build();

        PaymentIntent intent = PaymentIntent.create(params);

        return intent.getClientSecret();

    }
}
