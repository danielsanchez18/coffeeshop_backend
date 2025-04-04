package com.daniel.sanchez.ecommerce.coffeshop_backend.controllers;

import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.PromotionDTO;
import com.daniel.sanchez.ecommerce.coffeshop_backend.services.PromotionService;
import com.daniel.sanchez.ecommerce.coffeshop_backend.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/promotion")
@CrossOrigin("*")
public class PromotionController {

    @Autowired
    private PromotionService promotionService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody PromotionDTO promotionDTO) {
        try {
            PromotionDTO createdPromotion = promotionService.create(promotionDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseUtil.successResponse("Promoción creada exitosamente", createdPromotion));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseUtil.errorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.errorResponse("Error al crear la promoción"));
        }
    }

    @GetMapping
    public ResponseEntity<?> findAll() {
        try {
            List<PromotionDTO> promotions = promotionService.findAll();
            return ResponseEntity.ok(ResponseUtil.successResponse("Promociones encontradas exitosamente", promotions));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.errorResponse("Error al buscar las promociones"));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAllPage(@PageableDefault(size = 10, page = 0) Pageable pageable) {
        try {
            Page<PromotionDTO> promotions = promotionService.findAll(pageable);
            return ResponseEntity.ok(ResponseUtil.successResponse("Promociones encontradas exitosamente", promotions));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.errorResponse("Error al buscar las promociones"));
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> findById(@PathVariable UUID id) {
        try {
            PromotionDTO promotion = promotionService.findById(id);
            return ResponseEntity.ok(ResponseUtil.successResponse("Promoción encontrada", promotion));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.errorResponse(ex.getMessage()));
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<?> findByName(@PathVariable String name, @PageableDefault(size = 10, page = 0) Pageable pageable) {
        try {
            Page<PromotionDTO> promotions = promotionService.findByName(name, pageable);
            return ResponseEntity.ok(ResponseUtil.successResponse("Promociones encontradas exitosamente", promotions));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.errorResponse(ex.getMessage()));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody PromotionDTO promotionDTO) {
        try {
            PromotionDTO updatedPromotion = promotionService.update(id, promotionDTO);
            return ResponseEntity.ok(ResponseUtil.successResponse("Promoción actualizada exitosamente", updatedPromotion));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseUtil.errorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.errorResponse("Error al actualizar la promoción"));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            promotionService.delete(id);
            return ResponseEntity.ok(ResponseUtil.successResponse("Promoción eliminada exitosamente", null));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseUtil.errorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.errorResponse("Error al eliminar la promoción"));
        }
    }

    @PutMapping("/change-state/{id}")
    public ResponseEntity<?> changeState(@PathVariable UUID id) {
        try {
            PromotionDTO promotion = promotionService.changeState(id);
            return ResponseEntity.ok(ResponseUtil.successResponse("Estado de la promoción cambiado exitosamente", promotion));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseUtil.errorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.errorResponse("Error al cambiar el estado de la promoción"));
        }
    }

    @GetMapping("/state/{active}")
    public ResponseEntity<?> findByState(@PathVariable boolean active) {
        try {
            List<PromotionDTO> promotions = promotionService.findByState(active);
            return ResponseEntity.ok(ResponseUtil.successResponse("Promociones encontradas exitosamente", promotions));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.errorResponse(ex.getMessage()));
        }
    }

    @GetMapping("/state-page/{active}")
    public ResponseEntity<?> findByStatePageable(@PathVariable boolean active, @PageableDefault(size = 10, page = 0) Pageable pageable) {
        try {
            Page<PromotionDTO> promotions = promotionService.findByStatePageable(active, pageable);
            return ResponseEntity.ok(ResponseUtil.successResponse("Promociones encontradas exitosamente", promotions));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.errorResponse(ex.getMessage()));
        }
    }

    @GetMapping("/product/{productName}")
    public ResponseEntity<?> findByProductName(@PathVariable String productName, @PageableDefault(size = 10, page = 0) Pageable pageable) {
        try {
            Page<PromotionDTO> promotions = promotionService.findByProductName(productName, pageable);
            return ResponseEntity.ok(ResponseUtil.successResponse("Promociones encontradas exitosamente", promotions));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.errorResponse(ex.getMessage()));
        }
    }

    @PutMapping("/extend/{id}")
    public ResponseEntity<?> extendPromotion(@PathVariable UUID id, @RequestBody PromotionDTO promotionDTO) {
        try {
            PromotionDTO extendedPromotion = promotionService.extendPromotion(id, promotionDTO);
            return ResponseEntity.ok(ResponseUtil.successResponse("Promoción extendida exitosamente", extendedPromotion));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseUtil.errorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.errorResponse("Error al extender la promoción"));
        }
    }

    @PutMapping("/increment-usage/{id}")
    public ResponseEntity<?> incrementUsage(@PathVariable UUID id) {
        try {
            PromotionDTO updatedPromotion = promotionService.incrementUsage(id);
            return ResponseEntity.ok(ResponseUtil.successResponse("Uso de la promoción incrementado exitosamente", updatedPromotion));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseUtil.errorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.errorResponse("Error al incrementar el uso de la promoción"));
        }
    }

}
