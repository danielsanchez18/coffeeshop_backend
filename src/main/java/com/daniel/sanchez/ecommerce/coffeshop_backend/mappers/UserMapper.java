package com.daniel.sanchez.ecommerce.coffeshop_backend.mappers;

import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.UserDTO;
import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Role;
import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.User;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword());
        dto.setPhone(user.getPhone());
        dto.setProfilePicture(user.getProfilePicture());
        dto.setProvider(user.getProvider());
        dto.setEnabled(user.getEnabled());

        if (user.getRoles() != null) {
            Set<String> roleNames = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet());
            dto.setRoles(roleNames);
        }

        dto.setAudit(user.getAudit());
        return dto;
    }

    public User toEntity(UserDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setPhone(dto.getPhone());
        user.setProfilePicture(dto.getProfilePicture());
        user.setEnabled(dto.getEnabled());
        return user;
    }

}
