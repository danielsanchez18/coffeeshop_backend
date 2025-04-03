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

        validateOfferDates(startDate, endDate, false); // Valida las fechas de la oferta
        validateNoActiveOffersOrPromotions(product, startDate, endDate); // Valida que no tenga ofertas activas ni promociones activas
        validateDiscountPrice(product.getPrice(), offerDTO.getDiscountPrice()); // Valida el precio de la oferta
    }

    // Valida la actualización de una oferta de producto
    public void validateUpdateOffer(ProductOffer existingOffer, ProductOfferDTO offerDTO) {
        validateOfferStateForUpdate(existingOffer); // Solo PROXIMA
        validateOfferDates(
                LocalDateTime.parse(offerDTO.getStartDate()),
                LocalDateTime.parse(offerDTO.getEndDate()),
                false
        );
        validateDiscountPrice(existingOffer.getProduct().getPrice(), offerDTO.getDiscountPrice());

        if (haveDatesChanged(existingOffer, offerDTO)) {
            validateNoOverlappingPromotions(
                    existingOffer.getProduct(),
                    LocalDateTime.parse(offerDTO.getStartDate()),
                    LocalDateTime.parse(offerDTO.getEndDate())
            );
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

    // Valida que se pueda extender una oferta
    public void validateExtendOffer(ProductOffer existingOffer, ProductOfferDTO offerDTO) {
        LocalDateTime newEndDate = LocalDateTime.parse(offerDTO.getEndDate());
        LocalDateTime existingEndDate = existingOffer.getEndDate();
        String status = existingOffer.calculateStatus();
        UUID productId = existingOffer.getProduct().getId();

        // 1. Validar fecha de extensión posterior
        if (!newEndDate.isAfter(existingEndDate)) {
            throw new IllegalArgumentException(
                    String.format("La nueva fecha de fin (%s) debe ser posterior a la fecha actual (%s)",
                            newEndDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                            existingEndDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            );
        }

        // 2. Validar estado
        if (status.equals("CANCELADA")) {
            throw new IllegalStateException("No se puede extender una oferta cancelada");
        }
        if (LocalDateTime.now().isAfter(existingEndDate.plusHours(24))) {
            throw new IllegalStateException("No se puede extender pasadas 24 horas de la fecha de fin");
        }

        // 3. Validar por estado
        switch (status) {
            case "ACTIVA":
            case "TERMINADA":
                validateOfferDates(existingOffer.getStartDate(), newEndDate, true);
                break;
            case "AGOTADA":
                validateOfferDates(existingOffer.getStartDate(), newEndDate, true);
                if (offerDTO.getUsesMax() < existingOffer.getUsesQuantity()) {
                    throw new IllegalArgumentException(
                            String.format("El nuevo límite de usos (%d) no puede ser menor al uso actual (%d)",
                                    offerDTO.getUsesMax(), existingOffer.getUsesQuantity())
                    );
                }
                break;
            default:
                throw new IllegalStateException("Estado no válido para extensión: " + status);
        }

        // 4. Validar solapamientos con ofertas/promociones (usando existsByProductAndDateRange)
        if (productOfferRepository.existsByProductAndDateRange(productId, existingEndDate, newEndDate)) {
            throw new IllegalStateException("No se puede extender: Hay ofertas programadas después de la fecha original");
        }
        if (promotionProductRepository.existsByProductAndDateRange(productId, existingEndDate, newEndDate)) {
            throw new IllegalStateException("No se puede extender: Hay promociones programadas después de la fecha original");
        }
    }


    // Valida que se pueda incrementar el uso de una oferta
    public void validateCanIncrementUsage(ProductOffer offer) {
        String status = offer.calculateStatus();

        // Solo permitir incrementar en ofertas ACTIVAS o PRÓXIMAS (si startsDate <= now)
        if (!status.equals("ACTIVA") &&
                !(status.equals("PROXIMA") && LocalDateTime.now().isAfter(offer.getStartDate()))) {
            throw new IllegalStateException(
                    String.format("No se puede incrementar usos en una oferta con estado: %s", status)
            );
        }

        // Validar si ya está agotada (usesMax > 0 && usesQuantity >= usesMax)
        if (offer.getUsesMax() > 0 && offer.getUsesQuantity() >= offer.getUsesMax()) {
            throw new IllegalStateException("La oferta ya ha alcanzado su límite de usos");
        }
    }

    // === Métodos Privados de Validacion y Soporte ===

    // Valida que la oferta esté en estado PROXIMA para poder actualizarla
    private void validateOfferStateForUpdate(ProductOffer offer) {
        if (!offer.calculateStatus().equals("PROXIMA")) {
            throw new IllegalStateException("Solo se pueden actualizar ofertas en estado PROXIMA");
        }
    }

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

    // Valida que las fechas de la oferta sean correctas
    private void validateOfferDates(LocalDateTime startDate, LocalDateTime endDate, boolean isExtension) {
        LocalDateTime now = LocalDateTime.now();

        if (!isExtension && startDate.isBefore(now)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser en el pasado");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("La fecha de fin debe ser posterior a la de inicio");
        }
    }

    // Valida que no haya ofertas activas ni promociones activas
    private void validateNoActiveOffersOrPromotions(Product product, LocalDateTime start, LocalDateTime end) {
        validateNoOverlappingOffers(product, start, end);
        validateNoOverlappingPromotions(product, start, end);
    }

    private void validateNoOverlappingOffers(Product product, LocalDateTime start, LocalDateTime end) {
        productOfferRepository.findByProductAndOverlappingDates(product.getId(), start, end)
                .stream()
                .findFirst()
                .ifPresent(offer -> {
                    throw new IllegalArgumentException(
                            String.format("El producto ya tiene una oferta programada del %s al %s",
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