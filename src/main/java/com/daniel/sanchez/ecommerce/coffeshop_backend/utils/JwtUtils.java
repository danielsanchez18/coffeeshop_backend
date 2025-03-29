package com.daniel.sanchez.ecommerce.coffeshop_backend.utils;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    // Expiración en milisegundos para token "normal" (ej. 1 hora)
    @Value("${jwt.expirationMs}")
    private int jwtExpirationMs;

    // Expiración en milisegundos para token "remember me" (ej. 30 días)
    @Value("${jwt.expirationRememberMeMs}")
    private long jwtExpirationRememberMeMs;

    public String generateJwtToken(Authentication authentication, boolean rememberMe) {
        org.springframework.security.core.userdetails.User userPrincipal = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + (rememberMe ? jwtExpirationRememberMeMs : jwtExpirationMs));

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateJwtToken(String token) {

        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("Token expirado: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("Token no soportado: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("Token mal formado: " + e.getMessage());
        } catch (SignatureException e) {
            System.out.println("Fallo en la firma: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Claims vacío: " + e.getMessage());
        }

        return false;
    }

}
