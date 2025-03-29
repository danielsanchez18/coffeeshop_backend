package com.daniel.sanchez.ecommerce.coffeshop_backend.dto;

import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Audit;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class UserDTO {

    private UUID id;
    private String name;
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String phone;
    private String profilePicture;
    private Boolean enabled;
    private Set<String> roles;
    private String provider;
    private Audit audit;

}
