package com.daniel.sanchez.ecommerce.coffeshop_backend.services;

import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    Category create(Category category);

    List<Category> findAll();

    Page<Category> findAll(Pageable pageable);

    Optional<Category> findById(Long id);

    // Buscar categorías por caracteres
    Page<Category> findByName(String name, Pageable pageable);

    // Obtener las categorías mas populares (Mas productos vendidos)
    Page<Category> findMostPopular(Pageable pageable);

    // Obtener las categorías menos populares (Menos productos vendidos)
    Page<Category> findLessPopular(Pageable pageable);

    Long getTotal();

    Category update(Long id, Category category);

    void delete(Long id);

}
