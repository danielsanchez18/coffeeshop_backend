package com.daniel.sanchez.ecommerce.coffeshop_backend.controllers;

import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.CategoryDTO;
import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Category;
import com.daniel.sanchez.ecommerce.coffeshop_backend.services.CategoryService;
import com.daniel.sanchez.ecommerce.coffeshop_backend.utils.ResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/category")
@CrossOrigin("*")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ResponseEntity<?> create(
            @RequestPart("category") String categoryJson,
            @RequestPart("image") MultipartFile image
    ) throws IOException {

        ObjectMapper om = new ObjectMapper();
        CategoryDTO category = om.readValue(categoryJson, CategoryDTO.class);

        try {
            CategoryDTO createdCategory = categoryService.create(category, image);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseUtil.successResponse("Categoría creada exitosamente", createdCategory));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseUtil.errorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.errorResponse("Error al crear la categoría"));
        }
    }

    @GetMapping
    public ResponseEntity<?> findAll() {
        try {
            List<CategoryDTO> categories = categoryService.findAll();
            return ResponseEntity.ok(ResponseUtil.successResponse("Categorías encontradas exitosamente", categories));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.errorResponse("Error al buscar las categorías"));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAllPage(@PageableDefault(size = 10, page = 0) Pageable pageable) {
        try {
            Page<CategoryDTO> categories = categoryService.findAll(pageable);
            return ResponseEntity.ok(ResponseUtil.successResponse("Categorías encontradas exitosamente", categories));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.errorResponse("Error al buscar las categorías"));
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            CategoryDTO category = categoryService.findById(id);
            return ResponseEntity.ok(ResponseUtil.successResponse("Categoría encontrada", category));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.errorResponse(ex.getMessage()));
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<?> findByName(@PathVariable String name, @PageableDefault(size = 10, page = 0) Pageable pageable) {
        try {
            Page<CategoryDTO> categories = categoryService.findByName(name, pageable);
            return ResponseEntity.ok(ResponseUtil.successResponse("Categorías encontradas exitosamente" , categories));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.errorResponse(ex.getMessage()));
        }
    }

    @GetMapping("/most-popular")
    public ResponseEntity<?> findMostPopular(@PageableDefault(size = 10, page = 0) Pageable pageable) {
        try {
            Page<CategoryDTO> categories = categoryService.findMostPopular(pageable);
            return ResponseEntity.ok(ResponseUtil.successResponse("Categorías encontradas exitosamente" , categories));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.errorResponse(ex.getMessage()));
        }
    }

    @GetMapping("/less-popular")
    public ResponseEntity<?> findLessPopular(@PageableDefault(size = 10, page = 0) Pageable pageable) {
        try {
            Page<CategoryDTO> categories = categoryService.findLessPopular(pageable);
            return ResponseEntity.ok(ResponseUtil.successResponse("Categorías encontradas exitosamente" , categories));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.errorResponse(ex.getMessage()));
        }
    }

    @GetMapping("/total")
    public ResponseEntity<?> getTotal() {
        try {
            Long total = categoryService.getTotal();
            return ResponseEntity.ok(ResponseUtil.successResponse("Total de categorías", total));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseUtil.errorResponse("Error al obtener el total de categorías"));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestPart("category") String categoryJson,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {

        ObjectMapper om = new ObjectMapper();
        CategoryDTO category = om.readValue(categoryJson, CategoryDTO.class);

        try {
            CategoryDTO updatedCategory = categoryService.update(id, category, image);
            return ResponseEntity.ok(ResponseUtil.successResponse("Categoría actualizada exitosamente", updatedCategory));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseUtil.errorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.errorResponse("Error al actualizar la categoría"));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            categoryService.delete(id);
            return ResponseEntity.ok(ResponseUtil.successResponse("Categoría eliminada exitosamente", null));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseUtil.errorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.errorResponse("Error al eliminar la categoría"));
        }
    }

}
