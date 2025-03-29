package com.daniel.sanchez.ecommerce.coffeshop_backend.servicesImpl;

import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.UserDTO;
import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Role;
import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.User;
import com.daniel.sanchez.ecommerce.coffeshop_backend.mappers.UserMapper;
import com.daniel.sanchez.ecommerce.coffeshop_backend.repositories.RoleRepository;
import com.daniel.sanchez.ecommerce.coffeshop_backend.repositories.UserRepository;
import com.daniel.sanchez.ecommerce.coffeshop_backend.services.FileStorageService;
import com.daniel.sanchez.ecommerce.coffeshop_backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FileStorageService fileStorageService;

    @Override
    public UserDTO create(UserDTO userDTO) {

        validateUserEmail(userDTO.getEmail());
        User user = userMapper.toEntity(userDTO);

        // 1. Asignar el rol 5L (Cliente)
        Optional<Role> role = roleRepository.findById(5L);
        if (!role.isPresent()) {
            throw new RuntimeException("Rol 'Cliente' no encontrado");
        }

        // 2. Obtener el rol
        Role clienteRole = role.get();

        // 3. Asignar el rol al usuario
        user.setRoles(new HashSet<>(Collections.singleton(clienteRole)));

        user.setEnabled(true);
        user.setProvider("LOCAL");

        // Imprimir el estado del usuario
        System.out.println("User before saving: " + user);

        User createdUser = userRepository.save(user);

        System.out.println("CreatedUser" + createdUser);

        return userMapper.toDTO(createdUser);
    }

    @Override
    public List<UserDTO> findAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDTO)
                .toList();
    }

    @Override
    public Page<UserDTO> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toDTO);
    }

    @Override
    public UserDTO findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El usuario con el ID especificado no existe"));
        return userMapper.toDTO(user);
    }

    @Override
    public Long getTotal() {
        return userRepository.count();
    }

    @Override
    public UserDTO update(UUID id, UserDTO updatedUserDTO, MultipartFile imageFile) {

        // 1. Validar si el usuario existe
        validateUserExists(id);
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // 2. Validar si el email cambió (si se cambió el email, debe ser único)
        if (!existingUser.getEmail().equalsIgnoreCase(updatedUserDTO.getEmail())) {
            validateUserEmail(updatedUserDTO.getEmail());
        }

        // 3. Actualizar los campos básicos
        existingUser.setName(updatedUserDTO.getName());
        existingUser.setEmail(updatedUserDTO.getEmail());
        existingUser.setPhone(updatedUserDTO.getPhone());
        existingUser.setEnabled(updatedUserDTO.getEnabled());

        // 4. Actualizar el rol
        // Aseguramos que el rol "Cliente" siempre esté asignado
        Optional<Role> roleOptional = roleRepository.findById(5L); // Rol Cliente
        if (!roleOptional.isPresent()) {
            throw new RuntimeException("Rol 'Cliente' no encontrado");
        }
        Role clienteRole = roleOptional.get();
        existingUser.setRoles(new HashSet<>(Collections.singleton(clienteRole)));

        // 5. Manejo de imagen de perfil (si se proporciona una nueva)
        if (imageFile != null && !imageFile.isEmpty()) {
            if (existingUser.getProfilePicture() != null) {
                String oldFileName = existingUser.getProfilePicture().replace("IMG_PROFILE/", "");
                fileStorageService.deleteImage(oldFileName, "IMG_PROFILE");
            }
            String newProfilePictureUrl = fileStorageService.storeImage(imageFile, "IMG_PROFILE");
            existingUser.setProfilePicture(newProfilePictureUrl);
        }

        User updatedUser = userRepository.save(existingUser);
        return userMapper.toDTO(updatedUser);
    }

    @Override
    public void delete(UUID id) {
        validateUserExists(id);
        userRepository.deleteById(id);
    }

    @Override
    public Page<UserDTO> searchUsers(
            String name,
            String email,
            String role,
            String provider,
            Boolean status,
            Pageable pageable) {
        return userRepository.searchUsers(name, email, role, provider, status, pageable)
                .map(userMapper::toDTO);
    }

    @Override
    public Page<UserDTO> findByOrders(Pageable pageable) {
        return userRepository.findByOrders(pageable)
                .map(userMapper::toDTO);
    }

    @Override
    public Page<UserDTO> findBySales(Pageable pageable) {
        return userRepository.findBySales(pageable)
                .map(userMapper::toDTO);
    }

    @Override
    public UserDTO updateRole(UUID id, String roleName) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));

        user.setRoles(new HashSet<>(Collections.singleton(role)));

        User updatedUser = userRepository.save(user);
        return userMapper.toDTO(updatedUser);
    }

    @Override
    public UserDTO updatePassword(UUID id, String password) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Usuario no encontrado"));

        // user.setPassword(this.passwordEncoder.encode(password));
        user.setPassword(password);

        User updatedUser = userRepository.save(user);
        return userMapper.toDTO(updatedUser);
    }

    @Override
    public UserDTO changeStatus(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        user.setEnabled(!user.getEnabled());

        User updatedUser = userRepository.save(user);
        return userMapper.toDTO(updatedUser);
    }



    // METODOS DE VALIDACION

    // Validación: Correo electrónico único
    private void validateUserEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El correo electrónico no puede estar vacío");
        }
        if (userRepository.findByEmail(email) != null) {
            throw new IllegalArgumentException("El correo electrónico ya está en uso");
        }
    }

    // Validación: Existencia del usuario
    private void validateUserExists(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("El usuario con el ID especificado no existe");
        }
    }

}
