package com.daniel.sanchez.ecommerce.coffeshop_backend.dto;

import lombok.Data;

@Data
public class AuthRequest {

    private String email;
    private String password;
    private boolean rememberMe; // true si el usuario marca "Recuerdame"

}
