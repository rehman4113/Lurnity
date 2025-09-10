package com.app.lurnityBackend.controller;

import com.app.lurnityBackend.dto.OrderRequestDto;
import com.app.lurnityBackend.dto.OrderResponseDto;
import com.app.lurnityBackend.service.OrderService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // ✅ User creates an order (buy course)
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<OrderResponseDto> createOrder(
            Authentication authentication,
            @RequestBody OrderRequestDto request) throws StripeException {
        String userEmail = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        OrderResponseDto orderResponse = orderService.createOrder(userEmail, request,isAdmin);
        // clientSecret is returned in orderResponse for frontend payment confirmation
        return ResponseEntity.ok(orderResponse);
    }

    // ✅ Retry payment
    @PostMapping("/{id}/retry")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<OrderResponseDto> retryPayment(
            @PathVariable String id,
            Authentication authentication) throws StripeException {
        String userEmail = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        OrderResponseDto response = orderService.retryPayment(id, userEmail,isAdmin);
        return ResponseEntity.ok(response);
    }

    // ✅ User fetches only their orders
    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<OrderResponseDto>> getMyOrders(Authentication authentication) {
        String userEmail = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        return ResponseEntity.ok(orderService.getMyOrders(userEmail,isAdmin));
    }

    // ✅ Admin can view all orders
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponseDto>> getAllOrders(Authentication authentication) {
        String adminEmail = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        return ResponseEntity.ok(orderService.getAllOrders(adminEmail, isAdmin));
    }

    // ✅ User cancels their order / Admin cancels any order
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<String> cancelOrder(
            @PathVariable String id,
            Authentication authentication) {
        String userEmail = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        orderService.cancelOrder(id, userEmail, isAdmin);
        return ResponseEntity.ok("Order cancelled successfully");
    }

}



/*
package com.app.lurnityBackend.controller;

import com.app.lurnityBackend.dto.OrderRequestDto;
import com.app.lurnityBackend.dto.OrderResponseDto;
import com.app.lurnityBackend.service.OrderService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // ✅ User creates an order (buy course)
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<OrderResponseDto> createOrder(
            Authentication authentication,
            @RequestBody OrderRequestDto request) throws StripeException {
        String userEmail = authentication.getName();
        OrderResponseDto orderResponse = orderService.createOrder(userEmail, request);
        // clientSecret is returned in orderResponse for frontend payment confirmation
        return ResponseEntity.ok(orderResponse);
    }

    // ✅ Retry payment
    @PostMapping("/{id}/retry")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<OrderResponseDto> retryPayment(
            @PathVariable String id,
            Authentication authentication) throws StripeException {
        String userEmail = authentication.getName();
        OrderResponseDto response = orderService.retryPayment(id, userEmail);
        return ResponseEntity.ok(response);
    }

    // ✅ User fetches only their orders
    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<OrderResponseDto>> getMyOrders(Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(orderService.getMyOrders(userEmail));
    }

    // ✅ Admin can view all orders
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // ✅ User cancels their order / Admin cancels any order
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<String> cancelOrder(
            @PathVariable String id,
            Authentication authentication) {
        String userEmail = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        orderService.cancelOrder(id, userEmail, isAdmin);
        return ResponseEntity.ok("Order cancelled successfully");
    }

}
*/
