package com.spring.boot.demo.controller;

import com.spring.boot.demo.dto.RegisterRequest;
import com.spring.boot.demo.dto.UserResponse;
import com.spring.boot.demo.service.AppUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AppUserController {
    private final AppUserService appUserService;

    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request){
        UserResponse createdUser = appUserService.registerCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
}
