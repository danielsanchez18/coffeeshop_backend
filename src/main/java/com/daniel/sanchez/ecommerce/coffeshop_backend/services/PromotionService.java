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

    void delete(UUID id);

    PromotionDTO findById(UUID id);

    List<PromotionDTO> findByName(String name);

    List<PromotionDTO> findByProduct(UUID productId);

    List<PromotionDTO> findByState(boolean active);

}