package com.daniel.sanchez.ecommerce.coffeshop_backend.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "product_offer")
public class ProductOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_product", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Double discountPrice;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Embedded
    private Audit audit = new Audit();

    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return !now.isBefore(startDate) && !now.isAfter(endDate);
    }

    // Método para finalizar la oferta
    public void endNow() {
        this.endDate = LocalDateTime.now(); // Fuerza el fin inmediato
    }

}