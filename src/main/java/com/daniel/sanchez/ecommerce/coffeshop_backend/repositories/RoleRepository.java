package com.daniel.sanchez.ecommerce.coffeshop_backend.repositories;

import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

}
