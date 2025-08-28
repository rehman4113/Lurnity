package com.app.lurnityBackend.service;

import com.app.lurnityBackend.dto.OrderRequestDto;
import com.app.lurnityBackend.dto.OrderResponseDto;
import com.app.lurnityBackend.model.Order;
import com.app.lurnityBackend.model.PaymentStatus;
import com.app.lurnityBackend.model.User;
import com.app.lurnityBackend.model.Course;
import com.app.lurnityBackend.repository.CourseRepository;
import com.app.lurnityBackend.repository.OrderRepository;
import com.app.lurnityBackend.repository.UserRepo;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepo userRepository;
    private final CourseRepository courseRepository;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    // ✅ Create a new order with Stripe payment
    public OrderResponseDto createOrder(String userEmail, OrderRequestDto request) throws StripeException {
        Stripe.apiKey = stripeApiKey;

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Prevent duplicate purchases
        boolean alreadyPurchased = orderRepository.findAll().stream()
                .anyMatch(order ->
                        order.getUserId().equals(user.getId()) &&
                                order.getCourse().getId().equals(course.getId()) &&
                                order.getPaymentStatus() == PaymentStatus.SUCCESS
                );

        if (alreadyPurchased) {
            throw new RuntimeException("You have already purchased this course.");
        }

        // 1️⃣ Create Stripe PaymentIntent
        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount((long) (course.getPrice() * 100)) // amount in cents
                        .setCurrency("usd")
                        .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        // 2️⃣ Save order with PAYMENT_PENDING status
        Order order = Order.builder()
                .userId(user.getId())
                .course(course)
                .paymentStatus(PaymentStatus.PENDING)
                .paymentId(paymentIntent.getId())
                .clientSecret(paymentIntent.getClientSecret())
                .createdAt(new Date())
                .build();

        orderRepository.save(order);

        // 3️⃣ Return order info including clientSecret
        return mapToDto(order, user.getId());
    }

    // Get all orders of a specific user
    public List<OrderResponseDto> getMyOrders(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return orderRepository.findAll().stream()
                .filter(order -> order.getUserId().equals(user.getId()))
                .map(order -> mapToDto(order, order.getUserId()))
                .collect(Collectors.toList());
    }

    // Get all orders (admin)
    public List<OrderResponseDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(order -> mapToDto(order, order.getUserId()))
                .collect(Collectors.toList());
    }

    // Get a specific order by ID
    public OrderResponseDto getOrderById(String orderId, String userEmail, boolean isAdmin) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        User user = userRepository.findById(order.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!isAdmin && !user.getEmail().equals(userEmail)) {
            throw new RuntimeException("Access denied");
        }

        return mapToDto(order, order.getUserId());
    }

    // Cancel an order
    public void cancelOrder(String orderId, String userEmail, boolean isAdmin) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        User user = userRepository.findById(order.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!isAdmin && !user.getEmail().equals(userEmail)) {
            throw new RuntimeException("Access denied");
        }

        order.setPaymentStatus(PaymentStatus.CANCELLED);
        orderRepository.save(order);
    }

    // Mapper utility
    private OrderResponseDto mapToDto(Order order, String userId) {
        return OrderResponseDto.builder()
                .id(order.getId())
                .userId(userId)
                .course(order.getCourse())
                .paymentStatus(order.getPaymentStatus())
                .paymentId(order.getPaymentId())
                .clientSecret(order.getClientSecret())
                .createdAt(order.getCreatedAt())
                .build();
    }
}




//package com.app.login.service;
//
//import com.app.login.dto.OrderRequestDto;
//import com.app.login.dto.OrderResponseDto;
//import com.app.login.model.Order;
//import com.app.login.model.PaymentStatus;
//import com.app.login.model.User;
//import com.app.login.model.Course;
//import com.app.login.repository.CourseRepository;
//import com.app.login.repository.OrderRepository;
//import com.app.login.repository.UserRepo;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.Date;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@RequiredArgsConstructor
//@Service
//public class OrderService {
//
//    private final OrderRepository orderRepository;
//    private final UserRepo userRepository;
//    private final CourseRepository courseRepository;
//
//    // Create a new order
//    public OrderResponseDto createOrder(String userEmail, OrderRequestDto request) {
//        User user = userRepository.findByEmail(userEmail)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        Course course = courseRepository.findById(request.getCourseId())
//                .orElseThrow(() -> new RuntimeException("Course not found"));
//
//        // 🚫 Prevent duplicate purchases
//        boolean alreadyPurchased = orderRepository.findAll().stream()
//                .anyMatch(order ->
//                        order.getUserId().equals(user.getId()) &&
//                                order.getCourse().getId().equals(course.getId()) &&
//                                order.getPaymentStatus() == PaymentStatus.SUCCESS
//                );
//
//        if (alreadyPurchased) {
//            throw new RuntimeException("You have already purchased this course.");
//        }
//
//        Order order = Order.builder()
//                .userId(user.getId())
//                .course(course)   // ✅ store full course object
//                .paymentStatus(PaymentStatus.SUCCESS)
//                .createdAt(new Date())
//                .build();
//
//        orderRepository.save(order);
//
//        return mapToDto(order, user.getId());
//    }
//
//    // Get all orders of a specific user
//    public List<OrderResponseDto> getMyOrders(String userEmail) {
//        User user = userRepository.findByEmail(userEmail)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        return orderRepository.findAll().stream()
//                .filter(order -> order.getUserId().equals(user.getId()))
//                .map(order -> mapToDto(order, order.getUserId()))
//                .collect(Collectors.toList());
//    }
//
//    // Get all orders (admin)
//    public List<OrderResponseDto> getAllOrders() {
//        return orderRepository.findAll().stream()
//                .map(order -> mapToDto(order, order.getUserId()))
//                .collect(Collectors.toList());
//    }
//
//    // Get a specific order by ID
//    public OrderResponseDto getOrderById(String orderId, String userEmail, boolean isAdmin) {
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new RuntimeException("Order not found"));
//
//        User user = userRepository.findById(order.getUserId())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        if (!isAdmin && !user.getEmail().equals(userEmail)) {
//            throw new RuntimeException("Access denied");
//        }
//
//        return mapToDto(order, order.getUserId());
//    }
//
//    // Cancel an order
//    public void cancelOrder(String orderId, String userEmail, boolean isAdmin) {
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new RuntimeException("Order not found"));
//
//        User user = userRepository.findById(order.getUserId())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        if (!isAdmin && !user.getEmail().equals(userEmail)) {
//            throw new RuntimeException("Access denied");
//        }
//
//        order.setPaymentStatus(PaymentStatus.CANCELLED);
//        orderRepository.save(order);
//    }
//
//    // Mapper utility
//    private OrderResponseDto mapToDto(Order order, String userId) {
//        return OrderResponseDto.builder()
//                .id(order.getId())
//                .userId(userId)
//                .course(order.getCourse())   // ✅ return full course
//                .paymentStatus(order.getPaymentStatus())
//                .paymentId(order.getPaymentId())
//                .createdAt(order.getCreatedAt())
//                .build();
//    }
//}
