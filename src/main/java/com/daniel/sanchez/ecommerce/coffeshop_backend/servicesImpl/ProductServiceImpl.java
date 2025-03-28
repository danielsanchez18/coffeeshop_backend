package com.daniel.sanchez.ecommerce.coffeshop_backend.servicesImpl;

import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.ProductDTO;
import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Category;
import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Product;
import com.daniel.sanchez.ecommerce.coffeshop_backend.mappers.ProductMapper;
import com.daniel.sanchez.ecommerce.coffeshop_backend.repositories.CategoryRepository;
import com.daniel.sanchez.ecommerce.coffeshop_backend.repositories.ProductRepository;
import com.daniel.sanchez.ecommerce.coffeshop_backend.services.FileStorageService;
import com.daniel.sanchez.ecommerce.coffeshop_backend.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public ProductDTO create(ProductDTO productDTO, MultipartFile imageFile) {
        validateProductName(productDTO.getName());

        Product product = productMapper.toEntity(productDTO);

        // 1. Asignar categoría (buscándola por nombre)
        Category category = categoryRepository.findByName(productDTO.getCategory())
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada: " + productDTO.getCategory()));
        product.setCategory(category);

        // 2. Guardar imagen
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = fileStorageService.storeImage(imageFile, "IMG_PRODUCTS");
            product.setImageUrl(imageUrl);
        }

        Product savedProduct = productRepository.save(product);
        return productMapper.toDTO(savedProduct);
    }

    @Override
    public List<ProductDTO> findAll() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toDTO)
                .toList();
    }

    @Override
    public Page<ProductDTO> findAll(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toDTO);
    }

    @Override
    public ProductDTO findById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        return productMapper.toDTO(product);
    }

    @Override
    public Long getTotal() {
        return productRepository.count();
    }

    @Override
    public ProductDTO update(UUID id, ProductDTO updatedProductDTO, MultipartFile imageFile) {
        validateProductExists(id);
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        // Validar nombre único (si cambió)
        if (!existingProduct.getName().equalsIgnoreCase(updatedProductDTO.getName())) {
            validateProductName(updatedProductDTO.getName());
        }

        // Actualizar campos básicos
        existingProduct.setName(updatedProductDTO.getName());
        existingProduct.setDescription(updatedProductDTO.getDescription());
        existingProduct.setPrice(updatedProductDTO.getPrice());
        existingProduct.setAvailable(updatedProductDTO.getAvailable());

        // Actualizar categoría (si es necesario)
        if (!existingProduct.getCategory().getName().equals(updatedProductDTO.getCategory())) {
            Category newCategory = categoryRepository.findByName(updatedProductDTO.getCategory())
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
            existingProduct.setCategory(newCategory);
        }

        // Manejo de imagen
        if (imageFile != null && !imageFile.isEmpty()) {
            if (existingProduct.getImageUrl() != null) {
                String oldFileName = existingProduct.getImageUrl().replace("IMG_PRODUCTS/", "");
                fileStorageService.deleteImage(oldFileName, "IMG_PRODUCTS");
            }
            String newImageUrl = fileStorageService.storeImage(imageFile, "IMG_PRODUCTS");
            existingProduct.setImageUrl(newImageUrl);
        }

        Product updatedProduct = productRepository.save(existingProduct);
        return productMapper.toDTO(updatedProduct);
    }

    @Override
    public void delete(UUID id) {
        validateProductExists(id);
        productRepository.deleteById(id);
    }

    @Override
    public Page<ProductDTO> searchProducts(
            String name,
            Long idCategory,
            Boolean available,
            Double minPrice,
            Double maxPrice,
            Pageable pageable
    ) {
        return productRepository.searchProducts(name, idCategory, available, minPrice, maxPrice, pageable)
                .map(productMapper::toDTO);
    }

    @Override
    public Page<ProductDTO> findMostOrdered(Pageable pageable) {
        return productRepository.findMostOrdered(pageable)
                .map(productMapper::toDTO);
    }

    @Override
    public Page<ProductDTO> findLessOrdered(Pageable pageable) {
        return productRepository.findLessOrdered(pageable)
                .map(productMapper::toDTO);
    }

    @Override
    public ProductDTO findFavoriteProductByUser(UUID idUser) {
        Product savedProduct = productRepository.findFavoriteProductByUser(idUser)
                .orElseThrow(() -> new IllegalArgumentException("No favorite product found for user with ID: " + idUser));

        return productMapper.toDTO(savedProduct);
    }



    // METODOS DE VALIDACION

    // Validación: Nombre del producto
    private void validateProductName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío");
        }
        if (productRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("El nombre del producto ya existe");
        }
    }

    // Validación: Existencia del producto
    private void validateProductExists(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("El producto con el ID especificado no existe");
        }
    }

}
