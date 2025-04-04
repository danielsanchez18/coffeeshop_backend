package com.daniel.sanchez.ecommerce.coffeshop_backend.services;

import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.PromotionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PromotionService {

    PromotionDTO create(PromotionDTO promotionDTO);

    PromotionDTO update(UUID id, PromotionDTO promotionDTO);

    List<PromotionDTO> findAll();

    Page<PromotionDTO> findAll(Pageable pageable);

    PromotionDTO changeState(UUID id);

    PromotionDTO extendPromotion(UUID id, PromotionDTO promotionDTO);

    PromotionDTO incrementUsage(UUID id);

    void delete(UUID id);

    PromotionDTO findById(UUID id);

    Page<PromotionDTO> findByName(String name, Pageable pageable);

    Page<PromotionDTO> findByProductName(String productName, Pageable pageable);

    Page<PromotionDTO> findByStatePageable(boolean active, Pageable pageable);

    List<PromotionDTO> findByState(boolean active);

}