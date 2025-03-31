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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductOfferServiceImpl implements ProductOfferService {

    @Autowired
    private ProductOfferRepository productOfferRepository;

    @Autowired
    private ProductOfferValidator validator;

    @Autowired
    private ProductOfferMapper productOfferMapper;

    @Override
    @Transactional
    public ProductOfferDTO create(ProductOfferDTO offerDTO) {
        validator.validateCreateOffer(offerDTO);

        ProductOffer offer = productOfferMapper.toEntity(offerDTO);
        ProductOffer savedOffer = productOfferRepository.save(offer);

        return productOfferMapper.toDTO(savedOffer);
    }

    @Override
    @Transactional
    public ProductOfferDTO update(UUID id, ProductOfferDTO offerDTO) {
        validator.validateUpdateOffer(id, offerDTO);

        ProductOffer existingOffer = productOfferRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Oferta no encontrada"));

        productOfferMapper.updateFromDTO(offerDTO, existingOffer);
        ProductOffer updatedOffer = productOfferRepository.save(existingOffer);

        return productOfferMapper.toDTO(updatedOffer);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        productOfferRepository.deleteById(id);
    }

    @Override
    public ProductOfferDTO findById(UUID id) {
        return productOfferRepository.findById(id)
                .map(productOfferMapper::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Oferta no encontrada"));
    }

    @Override
    public List<ProductOfferDTO> findAll() {
        return productOfferRepository.findAll().stream()
                .map(productOfferMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductOfferDTO> findAll(Pageable pageable) {
        return productOfferRepository.findAll(pageable)
                .map(productOfferMapper::toDTO);
    }

    @Override
    public Page<ProductOfferDTO> findByProductName(String productName, Pageable pageable) {
        return productOfferRepository.findByProductNameContainingIgnoreCase(productName, pageable)
                .map(productOfferMapper::toDTO);
    }

    @Override
    @Transactional
    public ProductOfferDTO changeState(UUID id) {
        ProductOffer offer = productOfferRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Oferta no encontrada"));

        validator.validateCanEndOffer(offer);
        offer.endNow();

        return productOfferMapper.toDTO(productOfferRepository.save(offer));
    }
}
