package com.app.lurnityBackend.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "orders")
public class Order {

    @Id
    private String id;

    private String userId;        // email or user _id — your choice
    private Course course;
    private PaymentStatus paymentStatus;

    private String paymentId;     // Stripe PaymentIntent ID
    private String clientSecret;  // Stripe client secret for frontend confirmation

    @Builder.Default
    private Date createdAt = new Date();
}
