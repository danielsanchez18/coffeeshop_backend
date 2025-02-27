package com.daniel.sanchez.ecommerce.coffeshop_backend.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 25, nullable = false, unique = true)
    private String name;

    private String description;

    @Embedded
    private Audit audit = new Audit();

}