package com.daniel.sanchez.ecommerce.coffeshop_backend.repositories;

import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    // Buscar productos por nombre
    Optional<Product> findByName(String name);

    // Consulta genérica para filtrar productos
    @Query("SELECT p FROM Product p " +
            "WHERE (:name IS NULL OR LOWER(p.name) LIKE CONCAT('%', LOWER(:name), '%')) " +
            "AND (:idCategory IS NULL OR p.category.id = :idCategory) " +
            "AND (:available IS NULL OR p.available = :available) " +
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Product> searchProducts(
            @Param("name") String name,
            @Param("idCategory") Long idCategory,
            @Param("available") Boolean available,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            Pageable pageable
    );

    @Query("SELECT p FROM Product p LEFT JOIN p.orderItems oi GROUP BY p ORDER BY COUNT(oi) DESC")
    Page<Product> findMostOrdered(Pageable pageable);

    @Query("SELECT p FROM Product p LEFT JOIN p.orderItems oi GROUP BY p ORDER BY COUNT(oi) ASC")
    Page<Product> findLessOrdered(Pageable pageable);

    @Query("SELECT oi.product FROM OrderItem oi " +
            "WHERE oi.order.user.id = :idUser " +
            "GROUP BY oi.product.id " +
            "ORDER BY SUM(oi.quantity) DESC")
    Optional<Product> findFavoriteProductByUser(@Param("idUser") UUID idUser);


}