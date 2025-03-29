package com.daniel.sanchez.ecommerce.coffeshop_backend.services;

import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserDTO create(UserDTO userDTO);

    List<UserDTO> findAll();

    Page<UserDTO> findAll(Pageable pageable);

    UserDTO findById(UUID id);

    Long getTotal();

    UserDTO update(UUID id, UserDTO updatedUserDTO, MultipartFile imageFile);

    void delete(UUID id);

    // Obtener los usuarios filtrados
    Page<UserDTO> searchUsers(
            String name,
            String email,
            String role,
            String provider,
            Boolean status,
            Pageable pageable
    );

    // Obtener los usuarios por cantidad de pedidos
    Page<UserDTO> findByOrders(Pageable pageable);

    // Obtener los usuarios por cantidad de ventas
    Page<UserDTO> findBySales(Pageable pageable);

    UserDTO updateRole(UUID id, String role);

    UserDTO updatePassword(UUID id, String password);

    UserDTO changeStatus(UUID id);

}
