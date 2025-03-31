package com.daniel.sanchez.ecommerce.coffeshop_backend.mappers;

import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.ProductOfferDTO;
import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Product;
import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.ProductOffer;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ProductOfferMapper {

    public ProductOfferDTO toDTO(ProductOffer offer) {
        ProductOfferDTO dto = new ProductOfferDTO();
        dto.setId(offer.getId());

        // Campos de solo lectura (output)
        if(offer.getProduct() != null) {
            dto.setProductId(offer.getProduct().getId());
            dto.setProductName(offer.getProduct().getName());
            dto.setOriginalPrice(offer.getProduct().getPrice());
        }

        dto.setDiscountPrice(offer.getDiscountPrice());
        dto.setStartDate(offer.getStartDate().toString());
        dto.setEndDate(offer.getEndDate().toString());

        // Estado calculado (usa isActive() de la entidad)
        dto.setActive(offer.isActive());

        return dto;
    }

    public ProductOffer toEntity(ProductOfferDTO dto) {
        ProductOffer offer = new ProductOffer();

        if(dto.getId() != null) {
            offer.setId(dto.getId());
        }

        // Solo necesitamos asignar el producto por ID
        Product product = new Product();
        product.setId(dto.getProductId());
        offer.setProduct(product);

        offer.setDiscountPrice(dto.getDiscountPrice());
        offer.setStartDate(LocalDateTime.parse(dto.getStartDate()));
        offer.setEndDate(LocalDateTime.parse(dto.getEndDate()));

        return offer;
    }

    public void updateFromDTO(ProductOfferDTO dto, ProductOffer entity) {
        if (dto.getDiscountPrice() != null) {
            entity.setDiscountPrice(dto.getDiscountPrice());
        }
        if (dto.getStartDate() != null) {
            entity.setStartDate(LocalDateTime.parse(dto.getStartDate()));
        }
        if (dto.getEndDate() != null) {
            entity.setEndDate(LocalDateTime.parse(dto.getEndDate()));
        }
    }

}
