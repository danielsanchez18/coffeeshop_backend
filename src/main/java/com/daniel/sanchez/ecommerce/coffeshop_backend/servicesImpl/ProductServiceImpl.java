package com.daniel.sanchez.ecommerce.coffeshop_backend.servicesImpl;

import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Product;
import com.daniel.sanchez.ecommerce.coffeshop_backend.repositories.ProductRepository;
import com.daniel.sanchez.ecommerce.coffeshop_backend.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Product create(Product product) {
        validateProductName(product.getName());
        return productRepository.save(product);
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return productRepository.findById(id);
    }

    @Override
    public Long getTotal() {
        return productRepository.count();
    }

    @Override
    public Product update(UUID id, Product updatedProduct) {
        validateProductExists(id);
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        if (!existingProduct.getName().equalsIgnoreCase(updatedProduct.getName())) {
            validateProductName(updatedProduct.getName());
        }

        existingProduct.setCategory(updatedProduct.getCategory());
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setImageUrl(updatedProduct.getImageUrl());
        existingProduct.setAvailable(updatedProduct.getAvailable());

        return productRepository.save(existingProduct);
    }

    @Override
    public void delete(UUID id) {
        validateProductExists(id);
        productRepository.deleteById(id);
    }

    @Override
    public Page<Product> searchProducts(
            String name,
            Long idCategory,
            Boolean available,
            Double minPrice,
            Double maxPrice,
            Pageable pageable
    ) {
        return productRepository.searchProducts(name, idCategory, available, minPrice, maxPrice, pageable);
    }

    @Override
    public Page<Product> findMostOrdered(Pageable pageable) {
        return productRepository.findMostOrdered(pageable);
    }

    @Override
    public Page<Product> findLessOrdered(Pageable pageable) {
        return productRepository.findLessOrdered(pageable);
    }

    @Override
    public Product findFavoriteProductByUser(UUID idUser) {
        return productRepository.findFavoriteProductByUser(idUser)
                .orElseThrow(() -> new IllegalArgumentException("No favorite product found for user with ID: " + idUser));
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
