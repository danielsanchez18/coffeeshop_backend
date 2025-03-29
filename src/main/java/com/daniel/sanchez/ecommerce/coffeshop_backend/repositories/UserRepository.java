package com.daniel.sanchez.ecommerce.coffeshop_backend.repositories;

import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    User findByEmail(String email);

    @Query("SELECT u FROM User u " +
            "JOIN u.roles r " +
            "WHERE (:name IS NULL OR u.name LIKE %:name%) " +
            "AND (:email IS NULL OR u.email LIKE %:email%) " +
            "AND (:role IS NULL OR r.name LIKE %:role%) " +
            "AND (:provider IS NULL OR u.provider = :provider) " +
            "AND (:status IS NULL OR u.enabled = :status)")
    Page<User> searchUsers(
            String name,
            String email,
            String role,
            String provider,
            Boolean status,
            Pageable pageable);

    // Obtener usuarios con más pedidos
    @Query("SELECT u FROM User u " +
            "LEFT JOIN Order o ON o.user.id = u.id " +
            "GROUP BY u.id " +
            "ORDER BY COUNT(o.id) DESC")
    Page<User> findByOrders(Pageable pageable);


    // Obtener usuarios con más ventas
    @Query("SELECT u FROM User u " +
            "LEFT JOIN Order o ON o.user.id = u.id " +
            "WHERE o.status = 'COMPLETO' " +
            "GROUP BY u.id " +
            "ORDER BY COUNT(o.id) DESC")
    Page<User> findBySales(Pageable pageable);

}
