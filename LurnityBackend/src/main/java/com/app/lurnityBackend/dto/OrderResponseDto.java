package com.app.lurnityBackend.dto;

import com.app.lurnityBackend.model.Course;
import com.app.lurnityBackend.model.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class OrderResponseDto {
    private String id;
    private String userId;         // userId
    private Course course;         // full course object
    private PaymentStatus paymentStatus;
    private String paymentId;      // Stripe PaymentIntent ID
    private String clientSecret;   // Stripe client secret
    private Date createdAt;
}
