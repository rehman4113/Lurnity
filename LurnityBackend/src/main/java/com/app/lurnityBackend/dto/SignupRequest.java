package com.app.lurnityBackend.dto;

import com.app.lurnityBackend.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data                   // generates getters, setters, toString, equals, hashCode
@AllArgsConstructor     // default constructor
@NoArgsConstructor     // all-args constructor
public class SignupRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Role role;
}
