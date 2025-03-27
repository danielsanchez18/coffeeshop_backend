package com.daniel.sanchez.ecommerce.coffeshop_backend.controllers;

import com.daniel.sanchez.ecommerce.coffeshop_backend.services.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
@CrossOrigin("*")
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/upload/product")
    public ResponseEntity<?> uploadProductImage(@RequestParam("file") MultipartFile file) {
        try {
            String imagePath = fileStorageService.storeImage(file, "IMG_PRODUCTS");
            return ResponseEntity.ok(imagePath); // Retorna la ruta relativa de la imagen
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al cargar la imagen: " + e.getMessage());
        }
    }

    @PostMapping("/upload/category")
    public ResponseEntity<?> uploadCategoryImage(@RequestParam("file") MultipartFile file) {
        try {
            String imagePath = fileStorageService.storeImage(file, "IMG_CATEGORIES");
            return ResponseEntity.ok(imagePath); // Retorna la ruta relativa de la imagen
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al cargar la imagen: " + e.getMessage());
        }
    }

    @PostMapping("/upload/user")
    public ResponseEntity<?> uploadUserImage(@RequestParam("file") MultipartFile file) {
        try {
            String imagePath = fileStorageService.storeImage(file, "IMG_USERS");
            return ResponseEntity.ok(imagePath); // Retorna la ruta relativa de la imagen
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al cargar la imagen: " + e.getMessage());
        }
    }

}
