package com.daniel.sanchez.ecommerce.coffeshop_backend.dto;

import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Audit;
import lombok.Data;

import java.util.UUID;

@Data
public class AddressDTO {

    private Long id;
    private UUID idUser;
    private String street;
    private String reference;
    private Boolean isPrimary;
    private Audit audit;

}
