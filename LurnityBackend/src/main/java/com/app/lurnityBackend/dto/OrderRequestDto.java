package com.app.lurnityBackend.dto;

import lombok.Data;

@Data
public class OrderRequestDto {
    private String courseId;   // user ID comes from JWT, so no need to send from client
}
