package com.daniel.sanchez.ecommerce.coffeshop_backend.controllers;

import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.ProductOfferDTO;
import com.daniel.sanchez.ecommerce.coffeshop_backend.services.ProductOfferService;
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
@RequestMapping("/product-offer")
@CrossOrigin("*")
public class ProductOfferController {

    @Autowired
    private ProductOfferService productOfferService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ProductOfferDTO offerDTO) {
        try {
            ProductOfferDTO createdOffer = productOfferService.create(offerDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseUtil.successResponse("Oferta de producto creada exitosamente", createdOffer));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseUtil.errorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.errorResponse("Error al crear la oferta de producto"));
        }
    }

    @GetMapping
    public ResponseEntity<?> findAll() {
        try {
            List<ProductOfferDTO> offers = productOfferService.findAll();
            return ResponseEntity.ok(ResponseUtil.successResponse("Ofertas de producto encontradas exitosamente", offers));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.errorResponse("Error al buscar las ofertas de producto"));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAllPage(@PageableDefault(size = 10, page = 0) Pageable pageable) {
        try {
            Page<ProductOfferDTO> offers = productOfferService.findAll(pageable);
            return ResponseEntity.ok(ResponseUtil.successResponse("Ofertas de producto encontradas exitosamente", offers));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.errorResponse("Error al buscar las ofertas de producto"));
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> findById(@PathVariable UUID id) {
        try {
            ProductOfferDTO offer = productOfferService.findById(id);
            return ResponseEntity.ok(ResponseUtil.successResponse("Oferta de producto encontrada", offer));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.errorResponse(ex.getMessage()));
        }
    }

    @GetMapping("/product-name/{productName}")
    public ResponseEntity<?> findByProductName(@PathVariable String productName, @PageableDefault(size = 10, page = 0) Pageable pageable) {
        try {
            Page<ProductOfferDTO> offers = productOfferService.findByProductName(productName, pageable);
            return ResponseEntity.ok(ResponseUtil.successResponse("Ofertas de producto encontradas exitosamente", offers));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.errorResponse(ex.getMessage()));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody ProductOfferDTO offerDTO) {
        try {
            ProductOfferDTO updatedOffer = productOfferService.update(id, offerDTO);
            return ResponseEntity.ok(ResponseUtil.successResponse("Oferta de producto actualizada exitosamente", updatedOffer));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseUtil.errorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.errorResponse("Error al actualizar la oferta de producto"));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            productOfferService.delete(id);
            return ResponseEntity.ok(ResponseUtil.successResponse("Oferta de producto eliminada exitosamente", null));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseUtil.errorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.errorResponse("Error al eliminar la oferta de producto"));
        }
    }

    @PutMapping("/change-state/{id}")
    public ResponseEntity<?> changeState(@PathVariable UUID id) {
        try {
            ProductOfferDTO offer = productOfferService.changeState(id);
            return ResponseEntity.ok(ResponseUtil.successResponse("Estado de la oferta de producto cambiado exitosamente", offer));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseUtil.errorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.errorResponse("Error al cambiar el estado de la oferta de producto"));
        }
    }

    @PutMapping("/extend/{id}")
    public ResponseEntity<?> extendOffer(@PathVariable UUID id, @RequestBody ProductOfferDTO offerDTO) {
        try {
            ProductOfferDTO extendedOffer = productOfferService.extendOffer(id, offerDTO);
            return ResponseEntity.ok(ResponseUtil.successResponse("Oferta de producto extendida exitosamente", extendedOffer));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseUtil.errorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.errorResponse("Error al extender la oferta de producto"));
        }
    }

    @PutMapping("/increment-usage/{offerId}")
    public ResponseEntity<?> incrementUsage(@PathVariable UUID offerId) {
        try {
            ProductOfferDTO updatedOffer = productOfferService.incrementUsage(offerId);
            return ResponseEntity.ok(ResponseUtil.successResponse("Uso de la oferta de producto incrementado exitosamente", updatedOffer));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseUtil.errorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.errorResponse("Error al incrementar el uso de la oferta de producto"));
        }
    }

}