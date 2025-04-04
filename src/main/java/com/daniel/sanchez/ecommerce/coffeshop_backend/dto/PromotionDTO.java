package com.daniel.sanchez.ecommerce.coffeshop_backend.dto;

import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Audit;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class PromotionDTO {

    private UUID id;

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    private String description;

    @NotBlank(message = "El tipo de descuento es obligatorio")
    private String discountType; // "PORCENTAJE", "DESCUENTO_DIRECTO", "PRECIO_FIJO"

    @Positive(message = "El valor de descuento debe ser positivo")
    private Double discountValue;

    @NotBlank(message = "La fecha de inicio es obligatoria")
    private String startDate;

    @NotBlank(message = "La fecha de fin es obligatoria")
    private String endDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double priceOriginal;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double priceFinal;

    @Valid
    @NotEmpty(message = "La promoción debe incluir productos")
    private List<PromotionProductDTO> products;

    private Integer usesMax;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer usesQuantity;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String state;

    private Audit audit;

}