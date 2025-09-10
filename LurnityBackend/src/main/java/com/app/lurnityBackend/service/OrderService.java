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
import java.util.Map;
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
    public OrderResponseDto createOrder(String userEmail, OrderRequestDto request, boolean isAdmin) throws StripeException {
        Stripe.apiKey = stripeApiKey;

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // 🚫 Prevent duplicate purchases
        boolean alreadyPurchased = orderRepository.findAll().stream()
                .anyMatch(order ->
                        order.getUserId().equals(user.getId()) &&
                                order.getCourse().getId().equals(course.getId()) &&
                                order.getPaymentStatus() == PaymentStatus.SUCCESS
                );

        if (alreadyPurchased) {
            throw new RuntimeException("You have already purchased this course.");
        }

        // 1️⃣ Create Order entity first
        Order order = Order.builder()
                .userId(user.getId())
                .course(course)
                .paymentStatus(PaymentStatus.PENDING)
                .createdAt(new Date())
                .build();
        orderRepository.save(order);

        // 2️⃣ Create Stripe PaymentIntent
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount((long) (course.getPrice() * 100)) // amount in cents
                .setCurrency("usd")
                .putAllMetadata(Map.of(
                        "orderId", String.valueOf(order.getId()),
                        "userId", String.valueOf(user.getId()),
                        "courseId", String.valueOf(course.getId())
                ))
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        // 3️⃣ Update order with Stripe details
        order.setPaymentId(paymentIntent.getId());
        order.setClientSecret(paymentIntent.getClientSecret());
        orderRepository.save(order);

        // 4️⃣ Return clientSecret to frontend
        return mapToDto(order, isAdmin);
    }

    // Get all orders of a specific user
    public List<OrderResponseDto> getMyOrders(String userEmail, boolean isAdmin) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return orderRepository.findAll().stream()
                .filter(order -> order.getUserId().equals(user.getId())) // ✅ only paid
                .map(order -> mapToDto(order, isAdmin))
                .collect(Collectors.toList());
    }

    // Get all orders (admin only)
    public List<OrderResponseDto> getAllOrders(String adminEmail, boolean isAdmin) {
        userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return orderRepository.findAll().stream()
                .map(order -> mapToDto(order, isAdmin))
                .collect(Collectors.toList());
    }

    // Retry payment method
    public OrderResponseDto retryPayment(String orderId, String userEmail, boolean isAdmin) throws StripeException {
        Stripe.apiKey = stripeApiKey;

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        User user = userRepository.findById(order.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getEmail().equals(userEmail) && !isAdmin) {
            throw new RuntimeException("Access denied");
        }

        if (order.getPaymentStatus() == PaymentStatus.SUCCESS) {
            throw new RuntimeException("Payment already completed for this order.");
        }

        Course course = order.getCourse();

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount((long) (course.getPrice() * 100))
                .setCurrency("usd")
                .putAllMetadata(Map.of(
                        "orderId", order.getId(),
                        "userId", user.getId(),
                        "courseId", course.getId()
                ))
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setPaymentId(paymentIntent.getId());
        order.setClientSecret(paymentIntent.getClientSecret());
        order.setCreatedAt(new Date());
        orderRepository.save(order);

        return mapToDto(order, isAdmin);
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

        orderRepository.deleteById(orderId);
    }

    // ✅ Mapper utility with role & payment checks
    private OrderResponseDto mapToDto(Order order, boolean isAdmin) {
        boolean hasAccess = isAdmin || order.getPaymentStatus() == PaymentStatus.SUCCESS;

        Course course = order.getCourse();
        Course safeCourse = new Course(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getPrice(),
                hasAccess ? course.getContentUrl() : null,
                course.getImageUrl()
        );

        return OrderResponseDto.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .course(safeCourse)
                .paymentStatus(order.getPaymentStatus())
                .paymentId(order.getPaymentId())
                .clientSecret(order.getClientSecret())
                .createdAt(order.getCreatedAt())
                .build();
    }
}


/*
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
import java.util.Map;
import java.util.stream.Collectors;

import static com.stripe.param.PaymentIntentCreateParams.*;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepo userRepository;
    private final CourseRepository courseRepository;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    // ✅ Create a new order with Stripe payment
    // ✅ Create order and PaymentIntent
    public OrderResponseDto createOrder(String userEmail, OrderRequestDto request) throws StripeException {
        Stripe.apiKey = stripeApiKey;

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // 🚫 Prevent duplicate purchases
        boolean alreadyPurchased = orderRepository.findAll().stream()
                .anyMatch(order ->
                        order.getUserId().equals(user.getId()) &&
                                order.getCourse().getId().equals(course.getId()) &&
                                order.getPaymentStatus() == PaymentStatus.SUCCESS
                );

        if (alreadyPurchased) {
            throw new RuntimeException("You have already purchased this course.");
        }

        // 1️⃣ Create Order entity first (so we get an orderId for metadata)
        Order order = Order.builder()
                .userId(user.getId())
                .course(course)
                .paymentStatus(PaymentStatus.PENDING)
                .createdAt(new Date())
                .build();
        orderRepository.save(order);

        // 2️⃣ Create Stripe PaymentIntent with metadata linking back to Order
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount((long) (course.getPrice() * 100)) // amount in cents
                .setCurrency("usd")
                .putAllMetadata(Map.of(
                        "orderId", String.valueOf(order.getId()), // 👈 crucial for webhook
                        "userId", String.valueOf(user.getId()),
                        "courseId", String.valueOf(course.getId())
                ))
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        // 3️⃣ Update order with Stripe details
        order.setPaymentId(paymentIntent.getId());
        order.setClientSecret(paymentIntent.getClientSecret());
        orderRepository.save(order);

        // 4️⃣ Return clientSecret to frontend
        return mapToDto(order, user.getId());
    }

    // Get all orders of a specific user
    public List<OrderResponseDto> getMyOrders(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return orderRepository.findAll().stream()
                .filter(order -> order.getUserId().equals(user.getId())
                        && order.getPaymentStatus() == PaymentStatus.SUCCESS) // ✅ only paid
                .map(order -> mapToDto(order, order.getUserId()))
                .collect(Collectors.toList());
    }


    // Get all orders (admin)
    public List<OrderResponseDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(order -> mapToDto(order, order.getUserId()))
                .collect(Collectors.toList());
    }

    // Retry payment method
    public OrderResponseDto retryPayment(String orderId, String userEmail) throws StripeException {
        Stripe.apiKey = stripeApiKey;

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        User user = userRepository.findById(order.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Ensure same user is retrying (or allow admin check if needed)
        if (!user.getEmail().equals(userEmail)) {
            throw new RuntimeException("Access denied");
        }

        // 🚫 If already successful, no retry allowed
        if (order.getPaymentStatus() == PaymentStatus.SUCCESS) {
            throw new RuntimeException("Payment already completed for this order.");
        }

        Course course = order.getCourse();

        // 1️⃣ Create a new PaymentIntent (new attempt)
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount((long) (course.getPrice() * 100)) // amount in cents
                .setCurrency("usd")
                .putAllMetadata(Map.of(
                        "orderId", order.getId(), // 👈 still linked to the same order
                        "userId", user.getId(),
                        "courseId", course.getId()
                ))
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        // 2️⃣ Update order: set back to PENDING with new PaymentIntent
        order.setPaymentStatus(PaymentStatus.PENDING); // ✅ let webhook decide SUCCESS/FAILED
        order.setPaymentId(paymentIntent.getId());
        order.setClientSecret(paymentIntent.getClientSecret());
        order.setCreatedAt(new Date());
        orderRepository.save(order);

        // 3️⃣ Return response for frontend to confirm again with Stripe
        return mapToDto(order, user.getId());
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

        orderRepository.deleteById(orderId);
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
*/