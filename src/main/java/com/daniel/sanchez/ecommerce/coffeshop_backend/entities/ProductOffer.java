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

    @Column(nullable = false)
    private Integer usesMax; // 0 para ilimitado

    @Column(nullable = false)
    private Integer usesQuantity = 0;

    @Column(nullable = false)
    private boolean manualCancel = false; // Indica si fue cancelada manualmente

    @Embedded
    private Audit audit = new Audit();

    public boolean isActive() {
        return calculateStatus().equals("ACTIVA");
    }

    // Método para cancelar manualmente
    public void cancel() {
        this.manualCancel = true;
        this.endDate = LocalDateTime.now(); // Finaliza inmediatamente
    }

    // Nuevo método para incrementar usos (solo se llama al aplicar la oferta en un pedido)
    public void incrementUsage() {
        if (usesMax > 0 && usesQuantity >= usesMax) {
            throw new IllegalStateException("La oferta ya ha alcanzado su límite de usos");
        }
        usesQuantity++;

        // Si se agotó, finaliza automáticamente
        if (usesQuantity.equals(usesMax)) {
            this.endDate = LocalDateTime.now();
        }
    }

    // Método para calcular el estado (no se persiste, se usa en el DTO)
    public String calculateStatus() {
        LocalDateTime now = LocalDateTime.now();

        if (manualCancel) {
            return "CANCELADA"; // Prioridad máxima: override cualquier otro estado
        }
        if (usesMax > 0 && usesQuantity >= usesMax) {
            return "AGOTADA"; // Segundo en prioridad
        }
        if (now.isBefore(startDate)) {
            return "PROXIMA";
        }
        if (!now.isAfter(endDate)) { // Si está en rango o se agotó (endDate modificado)
            return "ACTIVA";
        }
        return "TERMINADA"; // Si pasó la fecha original de fin
    }

}