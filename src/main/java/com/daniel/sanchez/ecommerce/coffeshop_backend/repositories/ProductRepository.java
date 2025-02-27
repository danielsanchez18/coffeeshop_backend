package com.daniel.sanchez.ecommerce.coffeshop_backend.repositories;

import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {



}
