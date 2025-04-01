package com.daniel.sanchez.ecommerce.coffeshop_backend.mappers;

import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.ProductDTO;
import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Product;
import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.ProductOffer;
import com.daniel.sanchez.ecommerce.coffeshop_backend.repositories.PromotionProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ProductMapper {

    @Autowired
    private ProductOfferMapper productOfferMapper;

    @Autowired
    private PromotionProductRepository promotionProductRepository;

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

        // Calcula si tiene al menos una oferta activa
        dto.setOnOffer(
                product.getProductOffers() != null &&
                product.getProductOffers().stream()
                        .anyMatch(ProductOffer::isActive) // Usa el método isActive() de la entidad
        );

        // Calcula si tiene al menos una promoción activa
        dto.setOnPromotion(
                promotionProductRepository.existsActivePromotionForProduct(product.getId())
        );

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
