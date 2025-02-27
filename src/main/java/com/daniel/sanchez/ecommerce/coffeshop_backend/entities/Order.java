package com.daniel.sanchez.ecommerce.coffeshop_backend.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    private User user;

    @Column(length = 25, nullable = false)
    private String status; // e.g., "PENDING", "IN_PREPARATION", "READY", "DELIVERED"

    @Column(nullable = false)
    private Double totalAmount;

    private Double discountApplied;

    @Column(length = 25, nullable = false)
    private String paymentMethod; // e.g., "CASH", "CARD", "ONLINE"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Embedded
    private Audit audit = new Audit();

}