package com.daniel.sanchez.ecommerce.coffeshop_backend.mappers;

import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.ProductDTO;
import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductDTO toDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setCategory(product.getCategory() != null ? product.getCategory().getName() : null);
        dto.setPrice(product.getPrice());
        dto.setImageUrl(product.getImageUrl());
        dto.setAvailable(product.getAvailable());
        dto.setAudit(product.getAudit());
        return dto;
    }

    public Product toEntity(ProductDTO dto) {
        Product product = new Product();
        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setImageUrl(dto.getImageUrl());
        product.setAvailable(dto.getAvailable());
        return product;
    }

}
