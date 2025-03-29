package com.daniel.sanchez.ecommerce.coffeshop_backend.services;

import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.AuthRequest;
import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.AuthResponse;
import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.UserDTO;

public interface AuthService {

    AuthResponse authenticateUser(AuthRequest authRequest);

    UserDTO getCurrentUser();

}
