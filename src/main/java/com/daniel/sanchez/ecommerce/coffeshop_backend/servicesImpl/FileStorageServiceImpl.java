package com.daniel.sanchez.ecommerce.coffeshop_backend.servicesImpl;

import com.daniel.sanchez.ecommerce.coffeshop_backend.config.FileStorageConfig;
import com.daniel.sanchez.ecommerce.coffeshop_backend.services.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Autowired
    private FileStorageConfig fileStorageConfig;

    @Override
    public String storeImage(MultipartFile file, String folderName) {
        try {
            // Validar que el archivo no esté vacío
            if (file.isEmpty()) {
                throw new IllegalArgumentException("El archivo no puede estar vacío");
            }

            // Crear la carpeta específica si no existe
            String folderPath = fileStorageConfig.getUploadDir() + folderName + "/";
            Path folder = Paths.get(folderPath);
            if (!Files.exists(folder)) {
                Files.createDirectories(folder);
            }

            // Generar un nombre único para el archivo
            String originalFileName = file.getOriginalFilename();
            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String uniqueFileName = UUID.randomUUID().toString() + extension;

            // Guardar el archivo
            Path destination = Paths.get(folderPath + uniqueFileName);
            Files.copy(file.getInputStream(), destination);

            return folderName + "/" + uniqueFileName; // Retorna la ruta relativa
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar la imagen", e);
        }
    }

    @Override
    public void deleteImage(String fileName, String folderName) {
        try {
            Path filePath = Paths.get(fileStorageConfig.getUploadDir() + folderName + "/" + fileName);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al eliminar la imagen", e);
        }
    }

}
