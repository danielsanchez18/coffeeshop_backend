package com.daniel.sanchez.ecommerce.coffeshop_backend.repositories;

import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, UUID> {

    List<Promotion> findByNameContainingIgnoreCase(String name);

    @Query("SELECT p FROM Promotion p WHERE p.startDate <= :now AND p.endDate >= :now")
    List<Promotion> findActivePromotions(LocalDateTime now);

    @Query("SELECT p FROM Promotion p WHERE p.endDate < :now OR p.startDate > :now")
    List<Promotion> findInactivePromotions(LocalDateTime now);

    boolean existsByName(String name);

}
