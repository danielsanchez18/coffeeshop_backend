package com.daniel.sanchez.ecommerce.coffeshop_backend.utils;

import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.PromotionProductDTO;
import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Product;
import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Promotion;
import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.PromotionProduct;
import com.daniel.sanchez.ecommerce.coffeshop_backend.repositories.ProductRepository;

import java.util.List;

public class CalculatePricePromotionUtil {

    public static double calculateOriginalPrice(List<PromotionProductDTO> products, ProductRepository productRepository) {
        return products.stream()
                .mapToDouble(pp -> {
                    Product product = productRepository.findById(pp.getProductId())
                            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
                    return product.getPrice() * pp.getQuantityRequired();
                })
                .sum();
    }

    public static double calculateOriginalPriceFromEntities(List<PromotionProduct> promotionProducts, ProductRepository productRepository) {
        return promotionProducts.stream()
                .mapToDouble(pp -> {
                    Product product = productRepository.findById(pp.getProduct().getId())
                            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
                    return product.getPrice() * pp.getQuantityRequired();
                })
                .sum();
    }

    public static double calculateFinalPrice(Promotion promotion, double originalPrice) {
        return switch (promotion.getDiscountType()) {
            case "PORCENTAJE" -> originalPrice * (1 - promotion.getDiscountValue() / 100);
            case "DESCUENTO_DIRECTO" -> originalPrice - promotion.getDiscountValue();
            case "PRECIO_FIJO" -> promotion.getPriceFinal();
            default -> originalPrice;
        };
    }

}
