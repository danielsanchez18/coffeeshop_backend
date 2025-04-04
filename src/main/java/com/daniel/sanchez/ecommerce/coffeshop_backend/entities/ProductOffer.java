package com.daniel.sanchez.ecommerce.coffeshop_backend.entities;

import com.daniel.sanchez.ecommerce.coffeshop_backend.enums.OfferState;
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
    private Integer usesMax = 0; // 0 = ilimitado

    @Column(nullable = false)
    private Integer usesQuantity = 0; // 0 = sin usar

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OfferState state = OfferState.PROXIMA;

    @Embedded
    private Audit audit = new Audit();

    // Método para calcular y actualizar el estado
    public void updateState() {
        LocalDateTime now = LocalDateTime.now();

        if (state == OfferState.CANCELADA) {
            return; // Si fue cancelada manualmente, no cambia
        }

        if (usesQuantity >= usesMax) {
            state = OfferState.AGOTADA;
            this.endDate = now; // Opcional: forzar fin inmediato
            return;
        }

        if (now.isBefore(startDate)) {
            state = OfferState.PROXIMA;
        } else if (!now.isAfter(endDate)) {
            state = OfferState.ACTIVA;
        } else {
            state = OfferState.TERMINADA;
        }
    }

    // Método para incrementar usos (se llamará desde el servicio de pedidos)
    public void incrementUsage() {
        if (usesQuantity < usesMax) {
            usesQuantity++;
            updateState(); // Recalcula el estado después de incrementar
        }
    }

    // Método para cancelar manualmente
    public void cancel() {
        this.state = OfferState.CANCELADA;
        this.endDate = LocalDateTime.now(); // Opcional: forzar fin inmediato
    }

    // Método para verificar si está activa (ahora considera estado y usos)
    public boolean isActive() {
        updateState(); // Asegura que el estado esté actualizado
        return state == OfferState.ACTIVA;
    }

}
