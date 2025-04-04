package com.daniel.sanchez.ecommerce.coffeshop_backend.repositories;

import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Promotion;
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
public interface PromotionRepository extends JpaRepository<Promotion, UUID> {

    Page<Promotion> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT p FROM Promotion p WHERE p.startDate <= :now AND p.endDate >= :now")
    List<Promotion> findActivePromotions(LocalDateTime now);

    @Query("SELECT p FROM Promotion p WHERE p.endDate < :now OR p.startDate > :now")
    List<Promotion> findInactivePromotions(LocalDateTime now);

    @Query("SELECT p FROM Promotion p WHERE :now BETWEEN p.startDate AND p.endDate")
    Page<Promotion> findActivePromotions(@Param("now") LocalDateTime now, Pageable pageable);

    @Query("SELECT p FROM Promotion p WHERE :now NOT BETWEEN p.startDate AND p.endDate")
    Page<Promotion> findInactivePromotions(@Param("now") LocalDateTime now, Pageable pageable);

    boolean existsByName(String name);

    Promotion findByName(String name);

}
