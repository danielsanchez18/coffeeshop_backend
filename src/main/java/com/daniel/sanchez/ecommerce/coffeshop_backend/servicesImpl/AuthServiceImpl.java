package com.daniel.sanchez.ecommerce.coffeshop_backend.servicesImpl;

import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.AuthRequest;
import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.AuthResponse;
import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.UserDTO;
import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.User;
import com.daniel.sanchez.ecommerce.coffeshop_backend.mappers.UserMapper;
import com.daniel.sanchez.ecommerce.coffeshop_backend.repositories.UserRepository;
import com.daniel.sanchez.ecommerce.coffeshop_backend.services.AuthService;
import com.daniel.sanchez.ecommerce.coffeshop_backend.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Override
    public AuthResponse authenticateUser(AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication, authRequest.isRememberMe());

            return new AuthResponse(jwt);
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas");
        }
    }

    @Override
    public UserDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
            && !(authentication instanceof AnonymousAuthenticationToken)) {

            String email = authentication.getName();
            User user = userRepository.findByEmail(email);

            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
            }

            return userMapper.toDTO(user);

        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No hay usuario autenticado");
        }
    }

}
