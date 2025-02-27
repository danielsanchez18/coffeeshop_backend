package com.daniel.sanchez.ecommerce.coffeshop_backend.services;

import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductService {

    Product create(Product product);

    List<Product> findAll();

    Page<Product> findAll(Pageable pageable);

    Optional<Product> findById(UUID id);

    Page<Product> findByName(String name);

    // FILTROS

    // Obtener los productos mas pedidos
    // Obtener los productos menos pedidos
    // Obtener los pedidos favoritos de un usuario

    Product update(UUID id, Product product);

    void delete(UUID id);

}
