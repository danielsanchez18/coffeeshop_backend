package com.daniel.sanchez.ecommerce.coffeshop_backend.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;

public interface FileStorageService {

    // public Resource load(String filename, String folder) throws MalformedURLException;
    // public String copy(MultipartFile filename, String folder) throws IOException;
    // public boolean delete(String filename, String folder);

    String storeImage(MultipartFile file, String folderName);

    void deleteImage(String fileName, String folderName);

}
