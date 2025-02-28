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

    Long getTotal();

    Product update(UUID id, Product product);

    void delete(UUID id);

    // Filtrados
    Page<Product> searchProducts(
            String name,
            Long idCategory,
            Boolean available,
            Double minPrice,
            Double maxPrice,
            Pageable pageable
    );

    // Obtener los productos mas pedidos
    Page<Product> findMostOrdered(Pageable pageable);

    // Obtener los productos menos pedidos
    Page<Product> findLessOrdered(Pageable pageable);

    // Obtener el producto favorito de un usuario
    Product findFavoriteProductByUser(UUID idUser);

}
