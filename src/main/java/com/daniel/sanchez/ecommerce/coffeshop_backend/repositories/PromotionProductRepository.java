package com.daniel.sanchez.ecommerce.coffeshop_backend.repositories;

import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.PromotionProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PromotionProductRepository extends JpaRepository<PromotionProduct, Long> {

    // Verifica si un producto tiene promociones activas ahora
    @Query("SELECT CASE WHEN COUNT(pp) > 0 THEN true ELSE false END " +
            "FROM PromotionProduct pp " +
            "JOIN pp.promotion p " +
            "WHERE pp.product.id = :productId " +
            "AND p.startDate <= CURRENT_TIMESTAMP " +
            "AND p.endDate >= CURRENT_TIMESTAMP")
    boolean existsActivePromotionForProduct(@Param("productId") UUID productId);

    // Verifica solapamiento de fechas con promociones
    @Query("SELECT CASE WHEN COUNT(pp) > 0 THEN true ELSE false END " +
            "FROM PromotionProduct pp " +
            "JOIN pp.promotion p " +
            "WHERE pp.product.id = :productId " +
            "AND ((p.startDate BETWEEN :start AND :end) OR " +
            "(p.endDate BETWEEN :start AND :end) OR " +
            "(p.startDate <= :start AND p.endDate >= :end))")
    boolean existsByProductAndDateRange(
            @Param("productId") UUID productId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    List<PromotionProduct> findByPromotionId(UUID promotionId);

    List<PromotionProduct> findByProductId(UUID productId);

    void deleteByPromotionId(UUID promotionId);

    // Opcional: Para obtener las promociones solapadas (usado en mensajes de error)
    @Query("SELECT pp FROM PromotionProduct pp " +
            "JOIN pp.promotion p " +
            "WHERE pp.product.id = :productId " +
            "AND ((p.startDate BETWEEN :start AND :end) OR " +
            "(p.endDate BETWEEN :start AND :end) OR " +
            "(p.startDate <= :start AND p.endDate >= :end))")
    List<PromotionProduct> findByProductIdAndDateRange(
            @Param("productId") UUID productId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}