package com.daniel.sanchez.ecommerce.coffeshop_backend.services;

import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.ProductDTO;
import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductService {

    ProductDTO create(ProductDTO productDTO, MultipartFile imageFile);

    List<ProductDTO> findAll();

    Page<ProductDTO> findAll(Pageable pageable);

    ProductDTO findById(UUID id);

    Long getTotal();

    ProductDTO update(UUID id, ProductDTO updatedProductDTO, MultipartFile imageFile);

    void delete(UUID id);

    // Productos filtrados
    Page<ProductDTO> searchProducts(
            String name,
            Long idCategory,
            Boolean available,
            Double minPrice,
            Double maxPrice,
            Pageable pageable
    );

    // Obtener los productos mas pedidos
    Page<ProductDTO> findMostOrdered(Pageable pageable);

    // Obtener los productos menos pedidos
    Page<ProductDTO> findLessOrdered(Pageable pageable);

    // Obtener el producto favorito de un usuario
    ProductDTO findFavoriteProductByUser(UUID idUser);

}
