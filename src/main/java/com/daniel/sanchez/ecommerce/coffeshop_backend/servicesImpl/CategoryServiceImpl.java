package com.daniel.sanchez.ecommerce.coffeshop_backend.servicesImpl;

import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.CategoryDTO;
import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Category;
import com.daniel.sanchez.ecommerce.coffeshop_backend.mappers.CategoryMapper;
import com.daniel.sanchez.ecommerce.coffeshop_backend.repositories.CategoryRepository;
import com.daniel.sanchez.ecommerce.coffeshop_backend.services.CategoryService;
import com.daniel.sanchez.ecommerce.coffeshop_backend.services.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public CategoryDTO create(CategoryDTO categoryDTO, MultipartFile image) throws IOException {
        validateCategoryName(categoryDTO.getName());

        Category category = categoryMapper.toEntity(categoryDTO);

        if (image != null && !image.isEmpty()) {
            String imagePath = fileStorageService.storeImage(image, "IMG_CATEGORIES");
            categoryDTO.setImageUrl(imagePath);
        }

        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDTO(savedCategory);
    }

    @Override
    public List<CategoryDTO> findAll() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toDTO)
                .toList();
    }

    @Override
    public Page<CategoryDTO> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(categoryMapper::toDTO);
    }

    @Override
    public CategoryDTO findById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
        return categoryMapper.toDTO(category);
    }

    @Override
    public Page<CategoryDTO> findByName(String name, Pageable pageable) {
        return categoryRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(categoryMapper::toDTO);
    }

    @Override
    public Page<CategoryDTO> findMostPopular(Pageable pageable) {
        return categoryRepository.findMostPopular(pageable)
                .map(categoryMapper::toDTO);
    }

    @Override
    public Page<CategoryDTO> findLessPopular(Pageable pageable) {
        return categoryRepository.findUnPopular(pageable)
                .map(categoryMapper::toDTO);
    }

    @Override
    public Long getTotal() {
        return categoryRepository.count();
    }

    @Override
    public CategoryDTO update(Long id, CategoryDTO categoryDTO, MultipartFile image) throws IOException {
        validateCategoryExists(id);
        categoryDTO.setId(id);
        validateCategoryName(categoryDTO.getName());

        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        // Actualiza los campos de la categoría
        existingCategory.setName(categoryDTO.getName());
        existingCategory.setDescription(categoryDTO.getDescription());

        if (image != null && !image.isEmpty()) {
            //Eliminar la imagen anterior si existe
            if (existingCategory.getImageUrl() != null) {
                String oldFileName = existingCategory.getImageUrl().replace("IMG_CATEGORIES/", "");
                fileStorageService.deleteImage(oldFileName, "IMG_CATEGORIES");
            }

            // Guardar la nueva imagen
            String imagePath = fileStorageService.storeImage(image, "IMG_CATEGORIES");
            existingCategory.setImageUrl(imagePath);
        }

        Category category = categoryRepository.save(existingCategory);
        return categoryMapper.toDTO(category);
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
