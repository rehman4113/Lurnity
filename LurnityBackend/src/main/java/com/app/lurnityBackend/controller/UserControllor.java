package com.app.lurnityBackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserControllor {

    @GetMapping("/users")
    public String getAllUsers() {
        return "Successfully checked";
    }
}

