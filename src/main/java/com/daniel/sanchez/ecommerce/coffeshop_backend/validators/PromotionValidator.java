package com.daniel.sanchez.ecommerce.coffeshop_backend.validators;

import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.PromotionDTO;
import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.PromotionProductDTO;
import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Product;
import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Promotion;
import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.PromotionProduct;
import com.daniel.sanchez.ecommerce.coffeshop_backend.mappers.PromotionMapper;
import com.daniel.sanchez.ecommerce.coffeshop_backend.repositories.ProductOfferRepository;
import com.daniel.sanchez.ecommerce.coffeshop_backend.repositories.ProductRepository;
import com.daniel.sanchez.ecommerce.coffeshop_backend.repositories.PromotionProductRepository;
import com.daniel.sanchez.ecommerce.coffeshop_backend.repositories.PromotionRepository;
import com.daniel.sanchez.ecommerce.coffeshop_backend.utils.CalculatePricePromotionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Component
public class PromotionValidator {

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOfferRepository productOfferRepository;

    @Autowired
    private PromotionProductRepository promotionProductRepository;

    @Autowired
    private PromotionMapper promotionMapper;

    public void validateCreatePromotion(PromotionDTO promotionDTO) {
        validatePromotionDates(promotionDTO);
        validateDiscountTypeAndValue(promotionDTO);
        validateProducts(promotionDTO.getProducts());
        validateNoOverlappingOffersOrPromotions(promotionDTO);
        validateDiscountLogic(promotionDTO);
        validateUniquePromotionName(promotionDTO.getName(), null);
    }

    public void validateUpdatePromotion(UUID promotionId, PromotionDTO promotionDTO) {
        Promotion existingPromotion = getPromotionOrThrow(promotionId);

        // Solo se pueden actualizar promociones en estado PROXIMA
        if (!existingPromotion.calculateStatus().equals("PROXIMA")) {
            throw new IllegalStateException("Solo se pueden actualizar promociones en estado PROXIMA");
        }

        // Validar que el nuevo usesMax no sea menor a los usos actuales
        if (promotionDTO.getUsesMax() != null && promotionDTO.getUsesMax() < existingPromotion.getUsesQuantity()) {
            throw new IllegalArgumentException("El nuevo límite de usos no puede ser menor al uso actual");
        }

        validatePromotionDates(promotionDTO);
        validateDiscountTypeAndValue(promotionDTO);
        validateUniquePromotionName(promotionDTO.getName(), promotionId);

        if (haveDatesChanged(existingPromotion, promotionDTO)) {
            validateProducts(promotionDTO.getProducts());
            validateDiscountLogic(promotionDTO);
            validateNoOverlappingOffersOrPromotions(promotionDTO);
        }
    }

    // Valida que se pueda extender una promoción
    public void validateExtendPromotion(Promotion existingPromotion, PromotionDTO promotionDTO) {
        LocalDateTime newEndDate = LocalDateTime.parse(promotionDTO.getEndDate());
        LocalDateTime existingEndDate = existingPromotion.getEndDate();
        String status = existingPromotion.calculateStatus();

        // --- Validaciones de Estado ---
        if (status.equals("CANCELADA")) {
            throw new IllegalStateException("No se puede extender una promoción cancelada");
        }
        if (!status.equals("ACTIVA") && !status.equals("TERMINADA")) {
            throw new IllegalStateException("Solo se pueden extender promociones ACTIVAS o TERMINADAS");
        }

        // --- Validaciones de Fecha ---
        if (!newEndDate.isAfter(existingEndDate)) {
            throw new IllegalArgumentException("La nueva fecha de fin debe ser posterior a la actual");
        }
        if (LocalDateTime.now().isAfter(existingEndDate.plusHours(24))) {
            throw new IllegalStateException("No se puede extender pasadas 24 horas de la fecha de fin");
        }

        // --- Validaciones de Solapamiento ---
        List<PromotionProduct> promotionProducts = promotionProductRepository.findByPromotionId(existingPromotion.getId());

        promotionProducts.forEach(pp -> {
            UUID productId = pp.getProduct().getId();
            if (productOfferRepository.existsByProductAndDateRange(productId, existingEndDate, newEndDate) ||
                    promotionProductRepository.existsByProductAndDateRange(productId, existingEndDate, newEndDate)) {
                throw new IllegalStateException("El producto % tiene ofertas/promociones posteriores a la fecha de fin actual.");
            }
        });
    }

    // Valida que se pueda finalizar una promoción
    public void validateCanEndPromotion(Promotion promotion) {
        LocalDateTime now = LocalDateTime.now();

        if (promotion.getStartDate().isAfter(now)) {
            throw new IllegalStateException("No se puede finalizar una promoción que no ha comenzado");
        }
        if (promotion.getEndDate().isBefore(now)) {
            throw new IllegalStateException("La promoción ya ha terminado");
        }
    }

    // Valida que se pueda incrementar el uso de una promoción
    public void validateCanIncrementUsage(Promotion promotion) {
        String status = promotion.calculateStatus();

        if (!status.equals("ACTIVA") &&
                !(status.equals("PROXIMA") && LocalDateTime.now().isAfter(promotion.getStartDate()))) {
            throw new IllegalStateException("No se puede incrementar usos en este estado: " + status);
        }

        if (promotion.getUsesMax() > 0 && promotion.getUsesQuantity() >= promotion.getUsesMax()) {
            throw new IllegalStateException("Límite de usos alcanzado");
        }
    }

    // --- Métodos de Validación Modularizados ---

    private void validatePromotionDates(PromotionDTO promotionDTO) {
        LocalDateTime startDate = LocalDateTime.parse(promotionDTO.getStartDate());
        LocalDateTime endDate = LocalDateTime.parse(promotionDTO.getEndDate());
        LocalDateTime now = LocalDateTime.now();

        if (startDate.isBefore(now)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser en el pasado");
        }

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("La fecha de fin debe ser posterior a la de inicio");
        }
    }

    private void validateNoOverlappingOffersOrPromotions(PromotionDTO dto) {
        dto.getProducts().forEach(product -> {
            UUID productId = product.getProductId();
            LocalDateTime start = LocalDateTime.parse(dto.getStartDate());
            LocalDateTime end = LocalDateTime.parse(dto.getEndDate());

            // 1. Validar contra ofertas existentes
            if (productOfferRepository.existsByProductAndDateRange(productId, start, end)) {
                throw new IllegalArgumentException(
                        String.format("El producto %s tiene una oferta programada en ese rango de fechas",
                                getProductName(productId))
                );
            }

            // 2. Validar contra otras promociones
            List<PromotionProduct> existingPromotions =
                    promotionProductRepository.findByProductIdAndDateRange(productId, start, end);

            if (!existingPromotions.isEmpty()) {
                Promotion existing = existingPromotions.get(0).getPromotion();
                throw new IllegalArgumentException(
                        String.format("El producto %s ya tiene una promoción programada del %s al %s",
                                getProductName(productId),
                                formatDate(existing.getStartDate()),
                                formatDate(existing.getEndDate()))
                );
            }
        });
    }

    private void validateProducts(List<PromotionProductDTO> products) {
        if (products.isEmpty()) {
            throw new IllegalArgumentException("La promoción debe tener al menos un producto");
        }
        products.forEach(this::validateProduct);
    }

    private void validateProduct(PromotionProductDTO productDTO) {
        Product product = getProductOrThrow(productDTO.getProductId());

        if (productOfferRepository.existsActiveOfferForProduct(product.getId())) {
            throw new IllegalArgumentException(
                    String.format("El producto %s ya tiene una oferta activa", product.getName())
            );
        }

        if (promotionProductRepository.existsActivePromotionForProduct(product.getId())) {
            throw new IllegalArgumentException(
                    String.format("El producto %s ya está en una promoción activa", product.getName())
            );
        }
    }

    private void validateDiscountTypeAndValue(PromotionDTO promotionDTO) {
        switch (promotionDTO.getDiscountType()) {
            case "PORCENTAJE":
                if (promotionDTO.getDiscountValue() <= 0 || promotionDTO.getDiscountValue() > 100) {
                    throw new IllegalArgumentException("El descuento porcentual debe estar entre 0 y 100");
                }
                break;
            case "DESCUENTO_DIRECTO":
                if (promotionDTO.getDiscountValue() <= 0) {
                    throw new IllegalArgumentException("El descuento directo debe ser mayor que 0");
                }
                break;
            case "PRECIO_FIJO":
                double originalPrice = calculateOriginalPrice(promotionDTO.getProducts());
                if (promotionDTO.getDiscountValue() <= 0 || promotionDTO.getDiscountValue() >= originalPrice) {
                    throw new IllegalArgumentException("El precio fijo debe ser mayor que 0 y menor al precio original del combo");
                }
                break;
            default:
                throw new IllegalArgumentException("Tipo de descuento no válido. Use PORCENTAJE, DESCUENTO_DIRECTO o PRECIO_FIJO");
        }
    }


    private void validateDiscountLogic(PromotionDTO promotionDTO) {
        double originalPrice = calculateOriginalPrice(promotionDTO.getProducts());
        double finalPrice = calculateFinalPrice(promotionDTO, originalPrice);

        // Validación genérica para todos los tipos
        if (finalPrice <= 0) {
            throw new IllegalArgumentException("El precio final debe ser mayor a cero");
        }

        // Validación específica para PRECIO_FIJO
        if ("PRECIO_FIJO".equals(promotionDTO.getDiscountType())) {
            if (promotionDTO.getPriceFinal() >= originalPrice) {
                throw new IllegalArgumentException("El precio fijo debe ser menor al precio original del combo");
            }
        }

        // Validación: El descuento no puede superar el doble del producto más caro
        double maxProductPrice = getMaxProductPrice(promotionDTO.getProducts());
        if (originalPrice - finalPrice > maxProductPrice * 2) {
            throw new IllegalArgumentException("El descuento no puede superar el doble del valor del producto más caro del combo");
        }
    }

    // Validación: Nombre de promoción único
    private void validateUniquePromotionName(String name, UUID existingPromotionId) {
        Promotion existing = promotionRepository.findByName(name);
        if (existing != null && (existingPromotionId == null || !existing.getId().equals(existingPromotionId))) {
            throw new IllegalArgumentException("El nombre de la promoción ya existe");
        }
    }

    // --- Métodos de Cálculo (Reutilizables) ---

    private double calculateOriginalPrice(List<PromotionProductDTO> products) {
        return CalculatePricePromotionUtil.calculateOriginalPrice(products, productRepository);
    }

    private double calculateFinalPrice(PromotionDTO promotionDTO, double originalPrice) {
        return CalculatePricePromotionUtil.calculateFinalPrice(promotionMapper.toEntity(promotionDTO), originalPrice);
    }

    private double getMaxProductPrice(List<PromotionProductDTO> products) {
        return products.stream()
                .mapToDouble(pp -> getProductOrThrow(pp.getProductId()).getPrice())
                .max()
                .orElse(0);
    }

    // --- Métodos Auxiliares ---

    private boolean haveDatesChanged(Promotion existingPromotion, PromotionDTO newPromotion) {
        return !existingPromotion.getStartDate().equals(LocalDateTime.parse(newPromotion.getStartDate())) ||
                !existingPromotion.getEndDate().equals(LocalDateTime.parse(newPromotion.getEndDate()));
    }

    private Product getProductOrThrow(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        if (!product.getAvailable()) {
            throw new IllegalArgumentException(
                    String.format("El producto %s no está disponible", product.getName())
            );
        }

        return product;
    }

    private Promotion getPromotionOrThrow(UUID promotionId) {
        return promotionRepository.findById(promotionId)
                .orElseThrow(() -> new IllegalArgumentException("Promoción no encontrada"));
    }

    private String getProductName(UUID productId) {
        return productRepository.findById(productId)
                .map(Product::getName)
                .orElse("[producto desconocido]");
    }

    // Formatea una fecha a un string con el formato "dd/MM hh:mm a"
    private String formatDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM hh:mm a");
        return date.format(formatter);
    }
}