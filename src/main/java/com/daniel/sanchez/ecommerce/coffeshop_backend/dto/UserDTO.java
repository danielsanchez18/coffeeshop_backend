package com.daniel.sanchez.ecommerce.coffeshop_backend.dto;

import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Audit;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class UserDTO {

    private UUID id;
    private String name;
    private String lastname;
    private String email;
    private String phone;
    private String profilePicture;
    private Boolean enabled;
    private Set<String> roles;
    private String provider;
    private String providerId;
    private Audit audit;

}
