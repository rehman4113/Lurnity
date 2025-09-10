package com.app.lurnityBackend.service;

import com.app.lurnityBackend.model.Order;
import com.app.lurnityBackend.model.PaymentStatus;
import com.app.lurnityBackend.repository.OrderRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StripeWebhookService {

    private final OrderRepository orderRepository;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    /**
     * Handle incoming webhook payload from Stripe
     */
    public void handleWebhook(String payload, String sigHeader) {
        Event event;
        try {
            // ✅ Verify event with signature
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            throw new RuntimeException("⚠️ Invalid Stripe signature: " + e.getMessage(), e);
        }

        switch (event.getType()) {
            case "payment_intent.succeeded" -> {
                PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                        .getObject().orElseThrow();
                updateOrderStatus(paymentIntent, PaymentStatus.SUCCESS);
            }
            case "payment_intent.payment_failed" -> {
                PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                        .getObject().orElseThrow();
                updateOrderStatus(paymentIntent, PaymentStatus.FAILED);
            }
            default -> System.out.println("Unhandled event type: " + event.getType());
        }
    }

    private void updateOrderStatus(PaymentIntent paymentIntent, PaymentStatus status) {
        // retrieve your metadata (you set it when creating PaymentIntent)
        String orderId = paymentIntent.getMetadata().get("orderId");

        if (orderId == null) {
            System.err.println("⚠️ Webhook received PaymentIntent without orderId metadata");
            return;
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        order.setPaymentStatus(status);
        order.setPaymentId(paymentIntent.getId());
        orderRepository.save(order);

        System.out.println("✅ Order " + orderId + " updated to " + status);
    }
}
