package com.daniel.sanchez.ecommerce.coffeshop_backend.controllers;

import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.AuthRequest;
import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.AuthResponse;
import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.UserDTO;
import com.daniel.sanchez.ecommerce.coffeshop_backend.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(authService.authenticateUser(authRequest));
    }

    @GetMapping("/current-user")
    public ResponseEntity<UserDTO> getCurrentUser() {
        return ResponseEntity.ok(authService.getCurrentUser());
    }

}
