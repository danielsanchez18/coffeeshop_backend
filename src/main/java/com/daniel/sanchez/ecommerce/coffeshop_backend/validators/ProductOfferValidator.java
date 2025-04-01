package com.daniel.sanchez.ecommerce.coffeshop_backend.validators;

import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.ProductOfferDTO;
import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Product;
import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.ProductOffer;
import com.daniel.sanchez.ecommerce.coffeshop_backend.repositories.ProductOfferRepository;
import com.daniel.sanchez.ecommerce.coffeshop_backend.repositories.ProductRepository;
import com.daniel.sanchez.ecommerce.coffeshop_backend.repositories.PromotionProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class ProductOfferValidator {

    @Autowired
    private ProductOfferRepository productOfferRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PromotionProductRepository promotionProductRepository;

    // Valida la creación de una oferta de producto
    public void validateCreateOffer(ProductOfferDTO offerDTO) {
        Product product = getProductOrThrow(offerDTO.getProductId());

        LocalDateTime startDate = LocalDateTime.parse(offerDTO.getStartDate());
        LocalDateTime endDate = LocalDateTime.parse(offerDTO.getEndDate());

        validateOfferDates(offerDTO); // Valida las fechas de la oferta
        validateNoActiveOffersOrPromotions(product, startDate, endDate); // Valida que no tenga ofertas activas ni promociones activas
        validateDiscountPrice(product.getPrice(), offerDTO.getDiscountPrice()); // Valida el precio de la oferta
    }

    // Valida la actualización de una oferta de producto
    public void validateUpdateOffer(UUID offerId, ProductOfferDTO offerDTO) {
        ProductOffer existingOffer = getOfferOrThrow(offerId); // Obtiene la oferta existente
        Product product = existingOffer.getProduct(); // Obtiene el producto de la oferta

        LocalDateTime newStart = LocalDateTime.parse(offerDTO.getStartDate());
        LocalDateTime newEnd = LocalDateTime.parse(offerDTO.getEndDate());

        validateOfferDates(offerDTO); // Valida las fechas de la oferta
        validateDiscountPrice(product.getPrice(), offerDTO.getDiscountPrice()); // Valida el precio de la oferta

        if (haveDatesChanged(existingOffer, offerDTO)) {
            validateNoOverlappingPromotions(product, newStart, newEnd); // Valida que no haya promociones activas
        }
    }

    // Valida que se pueda finalizar una oferta
    public void validateCanEndOffer(ProductOffer offer) {
        LocalDateTime now = LocalDateTime.now();

        if (offer.getStartDate().isAfter(now)) {
            throw new IllegalStateException("No se puede finalizar una oferta que no ha comenzado");
        }
        if (offer.getEndDate().isBefore(now)) {
            throw new IllegalStateException("La oferta ya ha terminado");
        }
    }

    // --- Métodos privados de validación ---

    // Obtiene un producto o lanza una excepción si no existe
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

    // Obtiene una oferta o lanza una excepción si no existe
    private ProductOffer getOfferOrThrow(UUID offerId) {
        return productOfferRepository.findById(offerId)
                .orElseThrow(() -> new IllegalArgumentException("Oferta no encontrada"));
    }

    // Valida que las fechas de la oferta sean correctas
    private void validateOfferDates(ProductOfferDTO offerDTO) {
        LocalDateTime startDate = LocalDateTime.parse(offerDTO.getStartDate());
        LocalDateTime endDate = LocalDateTime.parse(offerDTO.getEndDate());
        LocalDateTime now = LocalDateTime.now();

        if (startDate.isBefore(now)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser en el pasado");
        }

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("La fecha de fin debe ser posterior a la de inicio");
        }
    }

    // Valida que no haya ofertas activas ni promociones activas
    private void validateNoActiveOffersOrPromotions(Product product, LocalDateTime start, LocalDateTime end) {
        validateNoActiveOffers(product);
        validateNoOverlappingPromotions(product, start, end);
    }

    // Valida que el producto no tenga ofertas activas
    private void validateNoActiveOffers(Product product) {
        productOfferRepository.findByProductAndEndDateAfter(product, LocalDateTime.now())
                .stream()
                .filter(offer -> offer.isActive() || offer.getStartDate().isAfter(LocalDateTime.now()))
                .findFirst()
                .ifPresent(offer -> {
                    throw new IllegalArgumentException(
                            String.format("El producto ya tiene una oferta activa del %s al %s",
                                    offer.isActive() ? "activa" : "programada",
                                    formatDate(offer.getStartDate()),
                                    formatDate(offer.getEndDate()))
                    );
                });
    }

    // Valida que no haya promociones activas
    private void validateNoOverlappingPromotions(Product product, LocalDateTime start, LocalDateTime end) {
        if(promotionProductRepository.existsByProductAndDateRange(product.getId(), start, end)) {
            throw new IllegalArgumentException("Este producto ya está en una promoción programada en ese rango de fechas");
        }
    }

    // Comprueba si las fechas de la oferta han cambiado
    private boolean haveDatesChanged(ProductOffer existingOffer, ProductOfferDTO newOffer) {
        return !existingOffer.getStartDate().equals(LocalDateTime.parse(newOffer.getStartDate())) ||
                !existingOffer.getEndDate().equals(LocalDateTime.parse(newOffer.getEndDate()));
    }

    // Valida que el precio de la oferta sea menor al precio original
    private void validateDiscountPrice(Double originalPrice, Double discountPrice) {
        if (discountPrice >= originalPrice) {
            throw new IllegalArgumentException("La oferta no puede ser mayor o igual al precio original");
        }
    }

    // Formatea una fecha a un string con el formato "dd/MM hh:mm a"
    private String formatDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM hh:mm a");
        return date.format(formatter);
    }
}