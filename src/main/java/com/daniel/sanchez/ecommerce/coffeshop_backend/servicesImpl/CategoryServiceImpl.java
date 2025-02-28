package com.daniel.sanchez.ecommerce.coffeshop_backend.servicesImpl;

import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Category;
import com.daniel.sanchez.ecommerce.coffeshop_backend.repositories.CategoryRepository;
import com.daniel.sanchez.ecommerce.coffeshop_backend.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Category create(Category category) {
        validateCategoryName(category.getName());
        return categoryRepository.save(category);
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Page<Category> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Override
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Page<Category> findByName(String name, Pageable pageable) {
        return categoryRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    @Override
    public Page<Category> findMostPopular(Pageable pageable) {
        return categoryRepository.findMostPopular(pageable);
    }

    @Override
    public Page<Category> findLessPopular(Pageable pageable) {
        return categoryRepository.findUnPopular(pageable);
    }

    @Override
    public Long getTotal() {
        return categoryRepository.count();
    }

    @Override
    public Category update(Long id, Category category) {
        validateCategoryName(category.getName());
        validateCategoryExists(id);
        category.setId(id);
        return categoryRepository.save(category);
    }

    @Override
    public void delete(Long id) {
        validateCategoryCanBeDeleted(id);
        categoryRepository.deleteById(id);
    }



    // MÉTODOS DE VALIDACIÓN

    private void validateCategoryName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría no puede estar vacío");
        }
        if (categoryRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("El nombre de la categoría ya existe");
        }
    }

    private void validateCategoryExists(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("La categoría con el ID especificado no existe");
        }
    }

    // Validar que la categoría elimina no contenga productos
    private void validateCategoryCanBeDeleted(Long id) {
        boolean hasProducts = categoryRepository.existsByProductsCategoryId(id);
        if (hasProducts) {
            throw new IllegalArgumentException("La categoría no puede ser eliminada porque tiene productos asociados");
        }
    }

}
