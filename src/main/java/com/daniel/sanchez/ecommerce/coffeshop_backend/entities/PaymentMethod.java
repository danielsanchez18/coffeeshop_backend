package com.daniel.sanchez.ecommerce.coffeshop_backend.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "payment_method")
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @Column(nullable = false)
    private String cardholderName;

    @Column(nullable = false)
    private String cardNumber; // Enmascarado (e.g., "**** **** **** 1234")

    @Column(length = 10, nullable = false)
    private String expirationDate; // Formato MM/YY

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_billing_address")
    private Address billingAddress;

    @Column(nullable = false)
    private Boolean isDefault;

    @Embedded
    private Audit audit = new Audit();

}