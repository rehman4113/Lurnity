package com.app.lurnityBackend.controller;

import com.app.lurnityBackend.service.PaymentService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Confirm payment for an existing order using Stripe test token.
     * Frontend sends JSON: { "token": "tok_visa" }
     */
    @PostMapping("/confirm/{orderId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> confirmPayment(
            @PathVariable String orderId,
            @RequestBody Map<String, String> request
    ) throws StripeException {
        String token = request.get("token");  // e.g., "tok_visa"
        paymentService.confirmPaymentWithToken(orderId, token);
        return ResponseEntity.ok("Payment confirmed and order updated");
    }
}
