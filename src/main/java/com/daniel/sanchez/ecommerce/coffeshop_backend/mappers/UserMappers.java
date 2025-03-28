package com.daniel.sanchez.ecommerce.coffeshop_backend.mappers;

import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.UserDTO;
import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.User;

public class UserMappers {

    public static UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setLastname(user.getLastname());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword());
        dto.setPhone(user.getPhone());
        dto.setProfilePicture(user.getProfilePicture());
        dto.setGoogleId(user.getGoogleId());
        dto.setFacebookId(user.getFacebookId());
        dto.setEnabled(user.getEnabled());
        dto.setAudit(user.getAudit());
        return dto;
    }

    public static User toEntity(UserDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setLastname(dto.getLastname());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setPhone(dto.getPhone());
        user.setProfilePicture(dto.getProfilePicture());
        user.setGoogleId(dto.getGoogleId());
        user.setFacebookId(dto.getFacebookId());
        user.setEnabled(dto.getEnabled());
        return user;
    }

}
