package com.daniel.sanchez.ecommerce.coffeshop_backend.services;

import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {

    Role create(Role role);

    List<Role> findAll();

    Optional<Role> findById(Long id);

    Role update(Long id, Role role);

    void delete(Long id);

}
