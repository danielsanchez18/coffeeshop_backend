package com.daniel.sanchez.ecommerce.coffeshop_backend.dto;

import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Audit;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class ProductOfferDTO {

    private UUID id;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private UUID productId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String productName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double originalPrice;

    private Double discountPrice;
    private String startDate;
    private String endDate;
    private Integer usesMax;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer usesQuantity;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String state;

    private Audit audit;
}
