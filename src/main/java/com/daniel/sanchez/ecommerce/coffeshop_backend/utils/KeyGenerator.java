package com.daniel.sanchez.ecommerce.coffeshop_backend.utils;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

public class KeyGenerator {
    public static void main(String[] args) {
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        String base64Key = java.util.Base64.getEncoder().encodeToString(key.getEncoded());
        System.out.println(base64Key);
    }
}
