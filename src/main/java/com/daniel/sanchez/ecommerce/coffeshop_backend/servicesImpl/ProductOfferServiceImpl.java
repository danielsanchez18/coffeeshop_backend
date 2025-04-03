package com.daniel.sanchez.ecommerce.coffeshop_backend.servicesImpl;

import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.ProductOfferDTO;
import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.ProductOffer;
import com.daniel.sanchez.ecommerce.coffeshop_backend.mappers.ProductOfferMapper;
import com.daniel.sanchez.ecommerce.coffeshop_backend.repositories.ProductOfferRepository;
import com.daniel.sanchez.ecommerce.coffeshop_backend.services.ProductOfferService;
import com.daniel.sanchez.ecommerce.coffeshop_backend.validators.ProductOfferValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductOfferServiceImpl implements ProductOfferService {

    @Autowired
    private ProductOfferRepository repository;

    @Autowired
    private ProductOfferValidator validator;

    @Autowired
    private ProductOfferMapper mapper;

    @Override
    @Transactional
    public ProductOfferDTO create(ProductOfferDTO offerDTO) {
        validator.validateCreateOffer(offerDTO);

        ProductOffer offer = mapper.toEntity(offerDTO);
        ProductOffer savedOffer = repository.save(offer);

        return mapper.toDTO(savedOffer);
    }

    @Override
    @Transactional
    public ProductOfferDTO update(UUID id, ProductOfferDTO offerDTO) {
        ProductOffer existingOffer = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Oferta no encontrada"));

        validator.validateUpdateOffer(existingOffer, offerDTO);

        // Solo permite actualizar campos específicos
        mapper.updateFromDTO(offerDTO, existingOffer);
        return mapper.toDTO(repository.save(existingOffer));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public ProductOfferDTO findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Oferta no encontrada"));
    }

    @Override
    public List<ProductOfferDTO> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductOfferDTO> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toDTO);
    }

    @Override
    public Page<ProductOfferDTO> findByProductName(String productName, Pageable pageable) {
        return repository.findByProductNameContainingIgnoreCase(productName, pageable)
                .map(mapper::toDTO);
    }

    @Override
    @Transactional
    public ProductOfferDTO changeState(UUID id) {
        ProductOffer offer = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Oferta no encontrada"));

        validator.validateCanEndOffer(offer);
        offer.cancel();

        return mapper.toDTO(repository.save(offer));
    }

    @Override
    @Transactional
    public ProductOfferDTO extendOffer(UUID id, ProductOfferDTO offerDTO) {
        ProductOffer existingOffer = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Oferta no encontrada"));

        validator.validateExtendOffer(existingOffer, offerDTO);

        // Aplica cambios según estado
        String status = existingOffer.calculateStatus();
        existingOffer.setEndDate(LocalDateTime.parse(offerDTO.getEndDate()));

        if (status.equals("AGOTADA")) {
            existingOffer.setUsesMax(offerDTO.getUsesMax());
        }

        return mapper.toDTO(repository.save(existingOffer));
    }

    @Override
    @Transactional
    public ProductOfferDTO incrementUsage(UUID offerId) {
        ProductOffer offer = repository.findById(offerId)
                .orElseThrow(() -> new IllegalArgumentException("Oferta no encontrada"));

        // Validar si se puede incrementar
        validator.validateCanIncrementUsage(offer);

        // Incrementar usos
        offer.setUsesQuantity(offer.getUsesQuantity() + 1);

        // Si se agotó, actualizar estado (pero no modificar endDate)
        if (offer.getUsesMax() > 0 && offer.getUsesQuantity() >= offer.getUsesMax()) {
            offer.setEndDate(LocalDateTime.now()); // Opcional: Si quieres forzar el fin
        }

        return mapper.toDTO(repository.save(offer));
    }
}
