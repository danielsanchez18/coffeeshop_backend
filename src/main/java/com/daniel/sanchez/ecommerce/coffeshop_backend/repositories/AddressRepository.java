package com.daniel.sanchez.ecommerce.coffeshop_backend.repositories;

import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Address;
import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    boolean existsByUserAndStreet(User user, String street);

    boolean existsByUserAndStreetAndIdNot(User user, String street, Long id);

    long countByUser(User user);

    List<Address> findByUser(User user);

    List<Address> findByUserId(UUID userId);

}
