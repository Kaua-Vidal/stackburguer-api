package com.stackburguer.api.controllers;

import com.stackburguer.api.DTO.order.OrderRequestDTO;
import com.stackburguer.api.DTO.order.OrderResponseDTO;
import com.stackburguer.api.service.PaymentService;
import com.stripe.exception.StripeException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/create-payment-intent")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService){
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createPaymentIntent(@RequestBody OrderRequestDTO orderRequest) throws StripeException{
        String clientSecret = paymentService.createPaymentIntent(orderRequest);

        Map<String, String> response = new HashMap<>();
        response.put("clientSecret", clientSecret);

        return ResponseEntity.ok(Map.of("clientSecret", clientSecret));
    }
}
