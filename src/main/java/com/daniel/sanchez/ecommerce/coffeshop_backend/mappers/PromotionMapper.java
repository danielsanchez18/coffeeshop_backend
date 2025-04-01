package com.daniel.sanchez.ecommerce.coffeshop_backend.mappers;

import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.PromotionDTO;
import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.PromotionProductDTO;
import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Promotion;
import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.PromotionProduct;
import com.daniel.sanchez.ecommerce.coffeshop_backend.repositories.ProductRepository;
import com.daniel.sanchez.ecommerce.coffeshop_backend.repositories.PromotionProductRepository;
import com.daniel.sanchez.ecommerce.coffeshop_backend.utils.CalculatePricePromotionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PromotionMapper {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PromotionProductRepository promotionProductRepository;

    public PromotionDTO toDTO(Promotion promotion) {
        PromotionDTO dto = new PromotionDTO();
        dto.setId(promotion.getId());
        dto.setName(promotion.getName());
        dto.setDescription(promotion.getDescription());
        dto.setDiscountType(promotion.getDiscountType());
        dto.setDiscountValue(promotion.getDiscountValue());
        dto.setStartDate(promotion.getStartDate().toString());
        dto.setEndDate(promotion.getEndDate().toString());
        dto.setActive(promotion.isActive());
        dto.setAudit(promotion.getAudit());

        // Mapeo de productos
        List<PromotionProduct> promotionProducts = promotionProductRepository.findByPromotionId(promotion.getId());
        dto.setProducts(promotionProducts.stream()
                .map(pp -> {
                    PromotionProductDTO ppDto = new PromotionProductDTO();
                    ppDto.setProductId(pp.getProduct().getId());
                    ppDto.setQuantityRequired(pp.getQuantityRequired());
                    return ppDto;
                })
                .collect(Collectors.toList()));

        // Campos calculados
        dto.setPriceOriginal(calculateOriginalPrice(promotionProducts));
        dto.setPriceFinal(calculateFinalPrice(promotion, dto.getPriceOriginal()));

        return dto;
    }

    public Promotion toEntity(PromotionDTO dto) {
        Promotion promotion = new Promotion();
        promotion.setName(dto.getName());
        promotion.setDescription(dto.getDescription());
        promotion.setDiscountType(dto.getDiscountType());
        promotion.setDiscountValue(dto.getDiscountValue());
        promotion.setStartDate(LocalDateTime.parse(dto.getStartDate()));
        promotion.setEndDate(LocalDateTime.parse(dto.getEndDate()));
        return promotion;
    }

    public void updateEntityFromDTO(PromotionDTO promotionDTO, Promotion entity) {
        if (promotionDTO.getName() != null) {
            entity.setName(promotionDTO.getName());
        }

        if (promotionDTO.getDescription() != null) {
            entity.setDescription(promotionDTO.getDescription());
        }

        if (promotionDTO.getStartDate() != null) {
            entity.setStartDate(LocalDateTime.parse(promotionDTO.getStartDate()));
        }

        if (promotionDTO.getEndDate() != null) {
            entity.setEndDate(LocalDateTime.parse(promotionDTO.getEndDate()));
        }

        if (promotionDTO.getDiscountType() != null) {
            entity.setDiscountType(promotionDTO.getDiscountType());
        }

        if (promotionDTO.getDiscountValue() != null) {
            entity.setDiscountValue(promotionDTO.getDiscountValue());
        }

        // Actualización condicional del estado (si el DTO lo incluye)
        if (promotionDTO.isActive() != entity.isActive()) {
            if (!promotionDTO.isActive()) {
                entity.endNow(); // Desactiva la promoción
            } else {
                // Reactivación (solo si las fechas son válidas)
                LocalDateTime now = LocalDateTime.now();
                if (entity.getEndDate().isAfter(now)) {
                    entity.setStartDate(now);
                }
            }
        }
    }

    private Double calculateOriginalPrice(List<PromotionProduct> promotionProducts) {
        return CalculatePricePromotionUtil.calculateOriginalPriceFromEntities(promotionProducts, productRepository);
    }

    private Double calculateFinalPrice(Promotion promotion, Double originalPrice) {
        return CalculatePricePromotionUtil.calculateFinalPrice(promotion, originalPrice);
    }

}