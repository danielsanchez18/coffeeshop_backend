package com.daniel.sanchez.ecommerce.coffeshop_backend.repositories;

import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

    Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT c, COUNT(o.id) AS popularity " +
            "FROM Category c " +
            "JOIN Product p ON c.id = p.category.id " +
            "JOIN OrderItem oi ON p.id = oi.id " +
            "JOIN Order o ON oi.id = o.id " +
            "GROUP BY c.id " +
            "ORDER BY popularity DESC")
    Page<Category> findMostPopular(Pageable pageable);

    @Query("SELECT c, COUNT(o.id) AS popularity " +
            "FROM Category c " +
            "JOIN Product p ON c.id = p.category.id " +
            "JOIN OrderItem oi ON p.id = oi.id " +
            "JOIN Order o ON oi.id = o.id " +
            "GROUP BY c.id " +
            "ORDER BY popularity ASC")
    Page<Category> findUnPopular(Pageable pageable);

}
