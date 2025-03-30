package com.daniel.sanchez.ecommerce.coffeshop_backend.services;

import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.AddressDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface AddressService {

    AddressDTO create(AddressDTO addressDTO);

    AddressDTO update(Long id, AddressDTO addressDTO);

    void delete(Long id);

    List<AddressDTO> findAll();

    Page<AddressDTO> findAll(Pageable pageable);

    AddressDTO findById(Long id);

    List<AddressDTO> findByUserId(UUID userId);

    AddressDTO setPrimary(Long id);

}
