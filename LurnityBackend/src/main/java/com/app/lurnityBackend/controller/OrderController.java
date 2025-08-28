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

    // ✅ User/Admin fetch specific order
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<OrderResponseDto> getOrderById(
            @PathVariable String id,
            Authentication authentication) {
        String userEmail = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        return ResponseEntity.ok(orderService.getOrderById(id, userEmail, isAdmin));
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



//package com.app.login.controller;
//
//import com.app.login.dto.OrderRequestDto;
//import com.app.login.dto.OrderResponseDto;
//import com.app.login.service.OrderService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/orders")
//@RequiredArgsConstructor
//public class OrderController {
//
//    private final OrderService orderService;
//
//    // ✅ User creates an order (buy course)
//    @PostMapping
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity<OrderResponseDto> createOrder(
//            Authentication authentication,
//            @RequestBody OrderRequestDto request) {
//        String userEmail = authentication.getName();
//        return ResponseEntity.ok(orderService.createOrder(userEmail, request));
//    }
//
//    // ✅ User fetches only their orders
//    @GetMapping("/my")
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity<List<OrderResponseDto>> getMyOrders(Authentication authentication) {
//        String userEmail = authentication.getName();
//        return ResponseEntity.ok(orderService.getMyOrders(userEmail));
//    }
//
//    // ✅ Admin can view all orders
//    @GetMapping
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
//        return ResponseEntity.ok(orderService.getAllOrders());
//    }
//
//    // ✅ User/Admin fetch specific order
//    @GetMapping("/{id}")
//    @PreAuthorize("hasAnyRole('USER','ADMIN')")
//    public ResponseEntity<OrderResponseDto> getOrderById(
//            @PathVariable String id,
//            Authentication authentication) {
//        String userEmail = authentication.getName();
//        boolean isAdmin = authentication.getAuthorities().stream()
//                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
//
//        return ResponseEntity.ok(orderService.getOrderById(id, userEmail, isAdmin));
//    }
//
//    // ✅ User cancels their order / Admin cancels any order
//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAnyRole('USER','ADMIN')")
//    public ResponseEntity<String> cancelOrder(
//            @PathVariable String id,
//            Authentication authentication) {
//        String userEmail = authentication.getName();
//        boolean isAdmin = authentication.getAuthorities().stream()
//                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
//
//        orderService.cancelOrder(id, userEmail, isAdmin);
//        return ResponseEntity.ok("Order cancelled successfully");
//    }
//}
