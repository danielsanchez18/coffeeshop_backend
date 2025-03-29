package com.daniel.sanchez.ecommerce.coffeshop_backend.controllers;

import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.UserDTO;
import com.daniel.sanchez.ecommerce.coffeshop_backend.services.UserService;
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
import java.util.UUID;

@RestController
@RequestMapping("/users")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody UserDTO userDTO) {
        try {
            UserDTO createdUser = userService.create(userDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseUtil.successResponse("Usuario creado exitosamente", createdUser));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseUtil.errorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.errorResponse("Error al crear el usuario"));
        }
    }

    @GetMapping
    public ResponseEntity<?> findAll() {
        try {
            List<UserDTO> users = userService.findAll();
            return ResponseEntity.ok(ResponseUtil.successResponse("Usuarios encontrados exitosamente", users));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.errorResponse("Error al buscar los usuarios"));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAllPage(@PageableDefault(size = 10, page = 0) Pageable pageable) {
        try {
            Page<UserDTO> users = userService.findAll(pageable);
            return ResponseEntity.ok(ResponseUtil.successResponse("Usuarios encontrados exitosamente", users));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.errorResponse("Error al buscar los usuarios"));
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> findById(@PathVariable UUID id) {
        try {
            UserDTO user = userService.findById(id);
            return ResponseEntity.ok(ResponseUtil.successResponse("Usuario encontrado", user));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.errorResponse(ex.getMessage()));
        }
    }

    @GetMapping("/total")
    public ResponseEntity<?> getTotal() {
        try {
            Long total = userService.getTotal();
            return ResponseEntity.ok(ResponseUtil.successResponse("Total de usuarios", total));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseUtil.errorResponse("Error al obtener el total de usuarios"));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(
            @PathVariable UUID id,
            @RequestPart("user") String userJson,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {

        ObjectMapper om = new ObjectMapper();
        UserDTO userDTO = om.readValue(userJson, UserDTO.class);

        try {
            UserDTO updatedUser = userService.update(id, userDTO, image);
            return ResponseEntity.ok(ResponseUtil.successResponse("Usuario actualizado exitosamente", updatedUser));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseUtil.errorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.errorResponse("Error al actualizar el usuario"));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            userService.delete(id);
            return ResponseEntity.ok(ResponseUtil.successResponse("Usuario eliminado exitosamente", null));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseUtil.errorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.errorResponse("Error al eliminar el usuario"));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String provider,
            @RequestParam(required = false) Boolean status,
            @PageableDefault(size = 10, page = 0) Pageable pageable
    ) {
        try {
            Page<UserDTO> users = userService.searchUsers(name, email, role, provider, status, pageable);
            return ResponseEntity.ok(ResponseUtil.successResponse("Usuarios encontrados exitosamente", users));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.errorResponse(ex.getMessage()));
        }
    }

    // TODO: PROBAR
    @GetMapping("/by-orders")
    public ResponseEntity<?> findByOrders(@PageableDefault(size = 10, page = 0) Pageable pageable) {
        try {
            Page<UserDTO> users = userService.findByOrders(pageable);
            return ResponseEntity.ok(ResponseUtil.successResponse("Usuarios encontrados exitosamente", users));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.errorResponse("Error al buscar los usuarios por pedidos"));
        }
    }

    // TODO: PROBAR
    @GetMapping("/by-sales")
    public ResponseEntity<?> findBySales(@PageableDefault(size = 10, page = 0) Pageable pageable) {
        try {
            Page<UserDTO> users = userService.findBySales(pageable);
            return ResponseEntity.ok(ResponseUtil.successResponse("Usuarios encontrados exitosamente", users));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.errorResponse("Error al buscar los usuarios por ventas"));
        }
    }

    @PutMapping("/update-role/{id}")
    public ResponseEntity<?> updateRole(@PathVariable UUID id, @RequestParam String role) {
        try {
            UserDTO updatedUser = userService.updateRole(id, role);
            return ResponseEntity.ok(ResponseUtil.successResponse("Rol del usuario actualizado exitosamente", updatedUser));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseUtil.errorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.errorResponse("Error al actualizar el rol del usuario"));
        }
    }

    @PutMapping("/update-password/{id}")
    public ResponseEntity<?> updatePassword(@PathVariable UUID id, @RequestParam String password) {
        try {
            UserDTO updatedUser = userService.updatePassword(id, password);
            return ResponseEntity.ok(ResponseUtil.successResponse("Contraseña del usuario actualizada exitosamente", updatedUser));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseUtil.errorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.errorResponse("Error al actualizar la contraseña del usuario"));
        }
    }

    @PutMapping("/change-status/{id}")
    public ResponseEntity<?> changeStatus(@PathVariable UUID id) {
        try {
            UserDTO updatedUser = userService.changeStatus(id);
            return ResponseEntity.ok(ResponseUtil.successResponse("Estado del usuario cambiado exitosamente", updatedUser));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseUtil.errorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.errorResponse("Error al cambiar el estado del usuario"));
        }
    }
}
