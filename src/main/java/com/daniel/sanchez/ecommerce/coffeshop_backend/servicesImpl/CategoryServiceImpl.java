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

    // MÉTODOS DE VALIDACIÓN:

    // Validar un único nombre

    // Validar que la categoría elimina no contenga productos

    @Override
    public Category create(Category category) {
        return null;
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
        return null;
    }

    @Override
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }

}
