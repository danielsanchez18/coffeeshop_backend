package com.daniel.sanchez.ecommerce.coffeshop_backend.servicesImpl;

import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Role;
import com.daniel.sanchez.ecommerce.coffeshop_backend.repositories.RoleRepository;
import com.daniel.sanchez.ecommerce.coffeshop_backend.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Role create(Role role) {
        validateRoleName(role.getName());
        return roleRepository.save(role);
    }

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public Optional<Role> findById(Long id) {
        return roleRepository.findById(id);
    }

    @Override
    public Role update(Long id, Role role) {
        validateRoleName(role.getName());
        validateRoleExists(role.getId());
        role.setId(id);
        return roleRepository.save(role);
    }

    @Override
    public void delete(Long id) {
        roleRepository.deleteById(id);
    }



    // METODOS DE VALIDACION

    private void validateRoleName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del rol es requerido");
        }
        if (roleRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("El nombre del rol ya existe");
        }
    }

    private void validateRoleExists(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new IllegalArgumentException("El rol con el ID especificado no existe");

        }
    }

}
