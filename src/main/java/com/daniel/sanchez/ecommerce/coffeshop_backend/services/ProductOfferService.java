package com.daniel.sanchez.ecommerce.coffeshop_backend.services;

import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.ProductOfferDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ProductOfferService {

    ProductOfferDTO create(ProductOfferDTO offerDTO);

    ProductOfferDTO update(UUID id, ProductOfferDTO offerDTO);

    void delete(UUID id);

    ProductOfferDTO findById(UUID id);

    List<ProductOfferDTO> findAll();

    Page<ProductOfferDTO> findAll(Pageable pageable);

    Page<ProductOfferDTO> findByProductName(String productName, Pageable pageable);

    ProductOfferDTO changeState(UUID id);

    ProductOfferDTO extendOffer(UUID id, ProductOfferDTO offerDTO);

    ProductOfferDTO incrementUsage(UUID offerId);

}
