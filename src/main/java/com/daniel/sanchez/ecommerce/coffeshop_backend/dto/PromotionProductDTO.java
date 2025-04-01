package com.daniel.sanchez.ecommerce.coffeshop_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class PromotionProductDTO {

    @NotNull(message = "El ID del producto es obligatorio")
    private UUID productId;

    @Min(value = 1, message = "La cantidad requerida debe ser al menos 1")
    private Integer quantityRequired;

}