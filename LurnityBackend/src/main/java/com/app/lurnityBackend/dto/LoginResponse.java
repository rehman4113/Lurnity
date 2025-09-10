package com.app.lurnityBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String message;
    String accessToken; // optional, if using JWT
    String refreshToken;
    private String email; // optional
}
