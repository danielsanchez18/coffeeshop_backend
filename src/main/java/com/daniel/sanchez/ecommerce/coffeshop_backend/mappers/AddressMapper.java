package com.daniel.sanchez.ecommerce.coffeshop_backend.mappers;

import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.AddressDTO;
import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Address;
import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.User;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public AddressDTO toDTO(Address address) {
        AddressDTO dto = new AddressDTO();
        dto.setId(address.getId());
        dto.setIdUser(address.getUser().getId());
        dto.setStreet(address.getStreet());
        dto.setReference(address.getReference());
        dto.setIsPrimary(address.getIsPrimary());
        dto.setAudit(address.getAudit());

        return dto;
    }

    public Address toEntity(AddressDTO dto, User user) {
        Address address = new Address();
        address.setId(dto.getId());
        address.setUser(user);
        address.setStreet(dto.getStreet());
        address.setReference(dto.getReference());
        address.setIsPrimary(dto.getIsPrimary());

        return address;
    }

}
