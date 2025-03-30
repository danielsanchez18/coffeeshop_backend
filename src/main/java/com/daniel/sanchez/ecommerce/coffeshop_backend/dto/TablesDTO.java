package com.daniel.sanchez.ecommerce.coffeshop_backend.dto;

import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Audit;
import lombok.Data;

@Data
public class TablesDTO {

    private Long id;
    private Integer tableNumber;
    private String status;
    private Audit audit;

}
