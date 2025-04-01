package com.daniel.sanchez.ecommerce.coffeshop_backend.repositories;

import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Product;
import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.ProductOffer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProductOfferRepository extends JpaRepository<ProductOffer, UUID> {

    List<ProductOffer> findByProductAndEndDateAfter(Product product, LocalDateTime date);

    Page<ProductOffer> findByProductNameContainingIgnoreCase(String productName, Pageable pageable);

    // Consulta para saber si un producto tiene al menos una oferta activa
    @Query("SELECT COUNT(o) > 0 FROM ProductOffer o " +
                  "WHERE o.product.id = :productId " +
                  "AND o.startDate <= CURRENT_TIMESTAMP " +
                  "AND o.endDate >= CURRENT_TIMESTAMP")
    boolean existsActiveOfferForProduct(UUID productId);

    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END " +
            "FROM ProductOffer o " +
            "WHERE o.product.id = :productId " +
            "AND ((o.startDate BETWEEN :start AND :end) OR " +
            "(o.endDate BETWEEN :start AND :end) OR " +
            "(o.startDate <= :start AND o.endDate >= :end))")
    boolean existsByProductAndDateRange(
            @Param("productId") UUID productId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

}
