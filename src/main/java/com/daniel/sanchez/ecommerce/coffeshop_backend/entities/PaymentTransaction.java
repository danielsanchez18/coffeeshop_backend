package com.daniel.sanchez.ecommerce.coffeshop_backend.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "payment_transaction")
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_order", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_payment_method")
    private PaymentMethod paymentMethod;

    @Column(nullable = false)
    private Double amount;

    @Column(length = 10, nullable = false)
    private String currency; // e.g., "USD", "EUR"

    @Column(length = 25, nullable = false)
    private String status; // e.g., "PENDING", "SUCCESS", "FAILED"

    private String transactionId; // ID proporcionado por la pasarela de pago

    @Embedded
    private Audit audit = new Audit();

}