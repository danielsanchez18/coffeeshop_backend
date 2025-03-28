package com.daniel.sanchez.ecommerce.coffeshop_backend.dto;

import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Audit;
import lombok.Data;

import java.util.UUID;

@Data
public class ProductDTO {

    private UUID id;
    private String name;
    private String description;
    private String category;
    private Double price;
    private String imageUrl;
    private Boolean available;
    private Audit audit;

}
