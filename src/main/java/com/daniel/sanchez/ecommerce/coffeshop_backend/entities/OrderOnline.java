package com.daniel.sanchez.ecommerce.coffeshop_backend.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "order_online")
public class OrderOnline {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_order", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_address")
    private Address address;

    @Column(nullable = false)
    private Double deliveryFee;

    @Column(nullable = false)
    private Integer estimatedTime; // Tiempo estimado en minutos

    @Column(nullable = false)
    private String typeDelivery; // e.g., "HOME_DELIVERY", "PICKUP"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_delivery_person")
    private User deliveryPerson;

    @Embedded
    private Audit audit = new Audit();

}
