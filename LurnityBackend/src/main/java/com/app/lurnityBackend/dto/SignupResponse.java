package com.app.lurnityBackend.dto;

import com.app.lurnityBackend.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    String accessToken; //
    String refreshToken;
    private String message; // e.g. "Signup successful"
}
