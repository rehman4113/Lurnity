package com.app.lurnityBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String message;
    String token; // optional, if using JWT
    private String email; // optional
}
