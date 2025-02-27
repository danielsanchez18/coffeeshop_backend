package com.daniel.sanchez.ecommerce.coffeshop_backend.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "order_in_person")
public class OrderInPerson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_order", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_table", nullable = false)
    private Tables tables;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_waiter", nullable = false)
    private User waiter;

    @Embedded
    private Audit audit = new Audit();

}