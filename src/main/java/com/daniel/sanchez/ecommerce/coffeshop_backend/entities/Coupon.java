package com.daniel.sanchez.ecommerce.coffeshop_backend.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "coupon")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(length = 50, nullable = false)
    private String discountType; // e.g., "FIXED", "PERCENTAGE"

    @Column(nullable = false)
    private Double discountValue;

    @Column(nullable = false)
    private Integer maxUses;

    @Column(nullable = false)
    private Integer usesCount;

    @Column(nullable = false)
    private LocalDateTime validFrom;

    @Column(nullable = false)
    private LocalDateTime validTo;

    @Embedded
    private Audit audit = new Audit();

}