package com.daniel.sanchez.ecommerce.coffeshop_backend.controllers;

import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.AddressDTO;
import com.daniel.sanchez.ecommerce.coffeshop_backend.services.AddressService;
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
@RequestMapping("/address")
@CrossOrigin("*")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody AddressDTO addressDTO) {
        try {
            AddressDTO createdAddress = addressService.create(addressDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseUtil.successResponse("Dirección creada exitosamente", createdAddress));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseUtil.errorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.errorResponse("Error al crear la dirección"));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody AddressDTO addressDTO) {
        try {
            AddressDTO updatedAddress = addressService.update(id, addressDTO);
            return ResponseEntity.ok(ResponseUtil.successResponse("Dirección actualizada exitosamente", updatedAddress));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseUtil.errorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.errorResponse("Error al actualizar la dirección"));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            addressService.delete(id);
            return ResponseEntity.ok(ResponseUtil.successResponse("Dirección eliminada exitosamente", null));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseUtil.errorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.errorResponse("Error al eliminar la dirección"));
        }
    }

    @GetMapping
    public ResponseEntity<?> findAll() {
        try {
            List<AddressDTO> addresses = addressService.findAll();
            return ResponseEntity.ok(ResponseUtil.successResponse("Direcciones encontradas exitosamente", addresses));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.errorResponse("Error al buscar las direcciones"));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAllPage(@PageableDefault(size = 10, page = 0) Pageable pageable) {
        try {
            Page<AddressDTO> addresses = addressService.findAll(pageable);
            return ResponseEntity.ok(ResponseUtil.successResponse("Direcciones encontradas exitosamente", addresses));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.errorResponse("Error al buscar las direcciones"));
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            AddressDTO address = addressService.findById(id);
            return ResponseEntity.ok(ResponseUtil.successResponse("Dirección encontrada", address));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.errorResponse(ex.getMessage()));
        }
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<?> findByUserId(@PathVariable UUID userId) {
        try {
            List<AddressDTO> addresses = addressService.findByUserId(userId);
            return ResponseEntity.ok(ResponseUtil.successResponse("Direcciones encontradas exitosamente", addresses));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.errorResponse(ex.getMessage()));
        }
    }

    @PutMapping("/set-primary/{id}")
    public ResponseEntity<?> setPrimary(@PathVariable Long id) {
        try {
            AddressDTO address = addressService.setPrimary(id);
            return ResponseEntity.ok(ResponseUtil.successResponse("Dirección principal establecida exitosamente", address));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtil.errorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseUtil.errorResponse("Error al establecer la dirección principal"));
        }
    }
}