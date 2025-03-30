package com.daniel.sanchez.ecommerce.coffeshop_backend.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tables")
public class Tables {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer tableNumber;

    @Column(length = 10, nullable = false)
    private String status; // e.g., "DISPONIBLE", "OCUPADA", "RESERVADA", "CERRADA"

    @Embedded
    private Audit audit = new Audit();

}
