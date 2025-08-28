package com.app.lurnityBackend.service;

import com.app.lurnityBackend.model.Order;
import com.app.lurnityBackend.model.PaymentStatus;
import com.app.lurnityBackend.repository.OrderRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentMethodCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.util.Map;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final OrderRepository orderRepository;

    @Value("${stripe.api.key}")
    private String stripeApiKey;



    public void confirmPaymentWithToken(String orderId, String token) {
        Stripe.apiKey = stripeApiKey;

        try {
            // 1. Retrieve the order
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            // 2. Confirm PaymentIntent using the test token
            PaymentIntent paymentIntent = PaymentIntent.retrieve(order.getPaymentId());

            PaymentIntent confirmedIntent = paymentIntent.confirm(
                    PaymentIntentConfirmParams.builder()
                            .setPaymentMethod(token)  // Use the test token here
                            .build()
            );

            // 3. Update order status
            if ("succeeded".equals(confirmedIntent.getStatus())) {
                order.setPaymentStatus(PaymentStatus.SUCCESS);
            } else {
                order.setPaymentStatus(PaymentStatus.FAILED);
            }

            orderRepository.save(order);

        } catch (StripeException e) {
            throw new RuntimeException("Stripe payment failed: " + e.getMessage(), e);
        }
    }

    /**
     * Confirm a payment using card details (server-side)
     */
    public void confirmPaymentWithCard(String orderId, Map<String, String> cardDetails) {
        String cardNumber = cardDetails.get("number");
        long expMonth = Long.parseLong(cardDetails.get("expMonth"));
        long expYear = Long.parseLong(cardDetails.get("expYear"));
        String cvc = cardDetails.get("cvc");    Stripe.apiKey = stripeApiKey;
        System.out.println("🔑 Using Stripe Key: " + stripeApiKey);
        System.out.println("📌 Confirming payment for Order: " + orderId);

        try {
            // 1️⃣ Fetch order from DB
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));

            // 2️⃣ Create a PaymentMethod with card details
            PaymentMethodCreateParams cardParams = PaymentMethodCreateParams.builder()
                    .setType(PaymentMethodCreateParams.Type.CARD)
                    .setCard(
                            PaymentMethodCreateParams.CardDetails.builder()
                                    .setNumber(cardNumber)
                                    .setExpMonth(expMonth)
                                    .setExpYear(expYear)
                                    .setCvc(cvc)
                                    .build()
                    )
                    .build();

            PaymentMethod paymentMethod = PaymentMethod.create(cardParams);

            // 3️⃣ Retrieve the PaymentIntent from Stripe
            PaymentIntent paymentIntent = PaymentIntent.retrieve(order.getPaymentId());

            // 4️⃣ Confirm PaymentIntent with the PaymentMethod
            PaymentIntentConfirmParams confirmParams = PaymentIntentConfirmParams.builder()
                    .setPaymentMethod(paymentMethod.getId())
                    .build();

            PaymentIntent confirmedIntent = paymentIntent.confirm(confirmParams);

            // 5️⃣ Update order status based on Stripe result
            if ("succeeded".equals(confirmedIntent.getStatus())) {
                order.setPaymentStatus(PaymentStatus.SUCCESS);
            } else {
                order.setPaymentStatus(PaymentStatus.FAILED);
            }
            order.setPaymentId(confirmedIntent.getId());

            orderRepository.save(order);
            System.out.println("💾 Order updated with payment status: " + order.getPaymentStatus());

        } catch (StripeException e) {
            System.err.println("❌ StripeException: " + e.getMessage());
            throw new RuntimeException("Stripe payment failed: " + e.getMessage(), e);

        } catch (Exception e) {
            System.err.println("❌ General Exception: " + e.getMessage());
            throw new RuntimeException("Payment confirmation failed: " + e.getMessage(), e);
        }
    }
}
