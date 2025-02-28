package com.daniel.sanchez.ecommerce.coffeshop_backend.controllers;

import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Product;
import com.daniel.sanchez.ecommerce.coffeshop_backend.services.ProductService;
import com.daniel.sanchez.ecommerce.coffeshop_backend.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/product")
@CrossOrigin("*")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Product product) {
        try {
            Product createdProduct = productService.create(product);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseUtil.successResponse("Producto creado exitosamente", createdProduct));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseUtil.errorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.errorResponse("Error al crear el producto"));
        }
    }

    @GetMapping
    public ResponseEntity<?> findAll() {
        try {
            List<Product> products = productService.findAll();
            return ResponseEntity.ok(ResponseUtil.successResponse("Productos encontrados exitosamente", products));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.errorResponse("Error al buscar los productos"));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAllPage(@PageableDefault(size = 10, page = 0) Pageable pageable) {
        try {
            Page<Product> products = productService.findAll(pageable);
            return ResponseEntity.ok(ResponseUtil.successResponse("Productos encontrados exitosamente", products));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.errorResponse("Error al buscar los productos"));
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> findById(@PathVariable UUID id) {
        try {
            Optional<Product> product = productService.findById(id);
            return ResponseEntity.ok(ResponseUtil.successResponse("Producto encontrado", product));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.errorResponse(ex.getMessage()));
        }
    }

    @GetMapping("/total")
    public ResponseEntity<?> getTotal() {
        try {
            Long total = productService.getTotal();
            return ResponseEntity.ok(ResponseUtil.successResponse("Total de productos", total));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseUtil.errorResponse("Error al obtener el total de productos"));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody Product product) {
        try {
            Product updatedProduct = productService.update(id, product);
            return ResponseEntity.ok(ResponseUtil.successResponse("Producto actualizado exitosamente", updatedProduct));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseUtil.errorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.errorResponse("Error al actualizar el producto"));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            productService.delete(id);
            return ResponseEntity.ok(ResponseUtil.successResponse("Producto eliminado exitosamente", null));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseUtil.errorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.errorResponse("Error al eliminar el producto"));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long idCategory,
            @RequestParam(required = false) Boolean available,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @PageableDefault(size = 10, page = 0) Pageable pageable
    ) {
        try {
            Page<Product> products = productService.searchProducts(name, idCategory, available, minPrice, maxPrice, pageable);
            return ResponseEntity.ok(ResponseUtil.successResponse("Productos encontrados exitosamente", products));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.errorResponse(ex.getMessage()));
        }
    }

    @GetMapping("/most-ordered")
    public ResponseEntity<?> findMostOrdered(@PageableDefault(size = 10, page = 0) Pageable pageable) {
        try {
            Page<Product> products = productService.findMostOrdered(pageable);
            return ResponseEntity.ok(ResponseUtil.successResponse("Productos encontrados exitosamente", products));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.errorResponse(ex.getMessage()));
        }
    }

    @GetMapping("/less-ordered")
    public ResponseEntity<?> findLessOrdered(@PageableDefault(size = 10, page = 0) Pageable pageable) {
        try {
            Page<Product> products = productService.findLessOrdered(pageable);
            return ResponseEntity.ok(ResponseUtil.successResponse("Productos encontrados exitosamente", products));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.errorResponse(ex.getMessage()));
        }
    }

    @GetMapping("/favorite/{idUser}")
    public ResponseEntity<?> findFavoriteProductByUser(@PathVariable UUID idUser) {
        try {
            Product product = productService.findFavoriteProductByUser(idUser);
            return ResponseEntity.ok(ResponseUtil.successResponse("Producto encontrado", product));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.errorResponse(ex.getMessage()));
        }
    }

}
