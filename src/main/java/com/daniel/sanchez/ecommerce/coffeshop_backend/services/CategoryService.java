package com.daniel.sanchez.ecommerce.coffeshop_backend.services;

import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.CategoryDTO;
import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface CategoryService {

    CategoryDTO create(CategoryDTO categoryDTO, MultipartFile imageFile) throws IOException;

    List<CategoryDTO> findAll();

    Page<CategoryDTO> findAll(Pageable pageable);

    CategoryDTO findById(Long id);

    // Buscar categorías por caracteres
    Page<CategoryDTO> findByName(String name, Pageable pageable);

    // Obtener las categorías mas populares (Mas productos vendidos)
    Page<CategoryDTO> findMostPopular(Pageable pageable);

    // Obtener las categorías menos populares (Menos productos vendidos)
    Page<CategoryDTO> findLessPopular(Pageable pageable);

    Long getTotal();

    CategoryDTO update(Long id, CategoryDTO updatedCategoryDTO, MultipartFile image) throws IOException;

    void delete(Long id);

}
