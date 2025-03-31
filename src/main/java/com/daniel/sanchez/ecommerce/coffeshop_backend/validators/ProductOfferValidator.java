package com.daniel.sanchez.ecommerce.coffeshop_backend.validators;

import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.ProductOfferDTO;
import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Product;
import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.ProductOffer;
import com.daniel.sanchez.ecommerce.coffeshop_backend.repositories.ProductOfferRepository;
import com.daniel.sanchez.ecommerce.coffeshop_backend.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Component
public class ProductOfferValidator {

    @Autowired
    private ProductOfferRepository productOfferRepository;

    @Autowired
    private ProductRepository productRepository;

    // Valida la creación de una oferta de producto
    public void validateCreateOffer(ProductOfferDTO offerDTO) {
        // Busca el producto por ID, lanza excepción si no se encuentra
        Product product = productRepository.findById(offerDTO.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        // Valida las fechas de la oferta
        validateOfferDates(offerDTO);
        // Valida que no haya ofertas activas para el producto
        validateNoActiveOffers(product);
        // Valida que el precio de descuento sea menor al precio original
        validateDiscountPrice(product.getPrice(), offerDTO.getDiscountPrice());
    }


    // Valida la actualización de una oferta de producto
    public void validateUpdateOffer(UUID offerId, ProductOfferDTO offerDTO) {
        // Busca la oferta existente por ID, lanza excepción si no se encuentra
        ProductOffer existingOffer = productOfferRepository.findById(offerId)
                .orElseThrow(() -> new IllegalArgumentException("Oferta no encontrada"));

        // Valida las fechas de la oferta
        validateOfferDates(offerDTO);
        // Valida que el precio de descuento sea menor al precio original
        validateDiscountPrice(existingOffer.getProduct().getPrice(), offerDTO.getDiscountPrice());
    }


    // Valida las fechas de la oferta
    private void validateOfferDates(ProductOfferDTO offerDTO) {
        LocalDateTime startDate = LocalDateTime.parse(offerDTO.getStartDate());
        LocalDateTime endDate = LocalDateTime.parse(offerDTO.getEndDate());
        LocalDateTime now = LocalDateTime.now();

        // La fecha de inicio no puede ser en el pasado
        if (startDate.isBefore(now)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser en el pasado");
        }

        // La fecha de fin debe ser posterior a la de inicio
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("La fecha de fin debe ser posterior a la de inicio");
        }
    }


    // Valida que no haya ofertas activas para el producto
    private void validateNoActiveOffers(Product product) {
        List<ProductOffer> activeOffers = productOfferRepository.findByProductAndEndDateAfter(product, LocalDateTime.now());

        // Si hay una oferta activa, lanza excepción
        activeOffers.stream()
                .filter(offer -> offer.getStartDate().isBefore(LocalDateTime.now())) // Solo ofertas en curso
                .findFirst()
                .ifPresent(offer -> {
                    String message = String.format(
                            "Este producto ya tiene una oferta activa del %s al %s",
                            formatDate(offer.getStartDate()),
                            formatDate(offer.getEndDate())
                    );
                    throw new IllegalArgumentException(message);
                });

        // Validación adicional: No permitir nuevas ofertas si hay una programada
        activeOffers.stream()
                .filter(offer -> offer.getStartDate().isAfter(LocalDateTime.now()))
                .findFirst()
                .ifPresent(offer -> {
                    String message = String.format(
                            "Este producto ya tiene una oferta programada del %s al %s",
                            formatDate(offer.getStartDate()),
                            formatDate(offer.getEndDate())
                    );
                    throw new IllegalArgumentException(message);
                });
    }


    // Valida que el precio de descuento sea menor al precio original
    private void validateDiscountPrice(Double originalPrice, Double discountPrice) {
        if (discountPrice >= originalPrice) {
            throw new IllegalArgumentException("La oferta no puede ser mayor o igual al precio original");
        }
    }


    // Valida que se pueda finalizar una oferta
    public void validateCanEndOffer(ProductOffer offer) {
        if (offer.getEndDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La oferta ya está finalizada");
        }
    }


    // Formatea la fecha en un formato legible
    private String formatDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM hh:mm a");
        return date.format(formatter);
    }
}