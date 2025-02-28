package com.daniel.sanchez.ecommerce.coffeshop_backend.entities;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_category", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    //@JsonBackReference
    private Category category;

    @Column(length = 100, nullable = false, unique = true)
    private String name;

    private String description;

    @Column(nullable = false)
    private Double price;

    private String imageUrl;

    @Column(nullable = false)
    private Boolean available;

    @Embedded
    private Audit audit = new Audit();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

}
