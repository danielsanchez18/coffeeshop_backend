package com.daniel.sanchez.ecommerce.coffeshop_backend.servicesImpl;

import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.AddressDTO;
import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Address;
import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.User;
import com.daniel.sanchez.ecommerce.coffeshop_backend.mappers.AddressMapper;
import com.daniel.sanchez.ecommerce.coffeshop_backend.repositories.AddressRepository;
import com.daniel.sanchez.ecommerce.coffeshop_backend.repositories.UserRepository;
import com.daniel.sanchez.ecommerce.coffeshop_backend.services.AddressService;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public AddressDTO create(AddressDTO addressDTO) {
        User user = validateUser(addressDTO.getIdUser());
        validateUniqueStreet(user, addressDTO.getStreet());
        validateMaxAddresses(user);

        Address address = addressMapper.toEntity(addressDTO, user);
        setPrimaryAddressIfNecessary(user, address);

        return addressMapper.toDTO(addressRepository.save(address));
    }

    @Override
    public AddressDTO update(Long id, AddressDTO addressDTO) {
        Address address = validateAddress(id);
        validateUniqueStreet(address.getUser(), addressDTO.getStreet(), id);

        address.setStreet(addressDTO.getStreet());
        address.setReference(addressDTO.getReference());

        return addressMapper.toDTO(addressRepository.save(address));
    }

    @Override
    public void delete(Long id) {
        Address address = validateAddress(id);
        boolean wasPrimary = address.getIsPrimary();
        addressRepository.delete(address);

        if (wasPrimary) {
            List<Address> addresses = addressRepository.findByUser(address.getUser());
            if (!addresses.isEmpty()) {
                Address lastAddress = addresses.get(addresses.size() - 1);
                lastAddress.setIsPrimary(true);
                addressRepository.save(lastAddress);
            }
        }
    }

    @Override
    public List<AddressDTO> findAll() {
        return addressRepository.findAll()
                .stream()
                .map(addressMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<AddressDTO> findAll(Pageable pageable) {
        return addressRepository.findAll(pageable).map(addressMapper::toDTO);
    }

    @Override
    public AddressDTO findById(Long id) {
        return addressMapper.toDTO(validateAddress(id));
    }

    @Override
    public List<AddressDTO> findByUserId(UUID userId) {
        return addressRepository.findByUserId(userId)
                .stream()
                .map(addressMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AddressDTO setPrimary(Long id) {
        Address address = validateAddress(id);
        resetPrimaryAddress(address.getUser());

        address.setIsPrimary(true);
        return addressMapper.toDTO(addressRepository.save(address));
    }


    // METODOS DE VALIDACION

    private User validateUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private Address validateAddress(Long id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada"));
    }

    private void validateUniqueStreet(User user, String street) {
        boolean exists = addressRepository.existsByUserAndStreet(user, street);
        if (exists) {
            throw new ValidationException("El usuario ya tiene registrada esta calle.");
        }
    }

    private void validateUniqueStreet(User user, String street, Long id) {
        boolean exists = addressRepository.existsByUserAndStreetAndIdNot(user, street, id);
        if (exists) {
            throw new ValidationException("El usuario ya tiene registrada esta calle.");
        }
    }

    private void validateMaxAddresses(User user) {
        long count = addressRepository.countByUser(user);
        if (count >= 3) {
            throw new ValidationException("El usuario solo puede tener hasta 3 direcciones.");
        }
    }

    private void resetPrimaryAddress(User user) {
        List<Address> addresses = addressRepository.findByUser(user);
        addresses.forEach(addr -> addr.setIsPrimary(false));
        addressRepository.saveAll(addresses);
    }

    private void setPrimaryAddressIfNecessary(User user, Address address) {
        if (address.getIsPrimary() == null || address.getIsPrimary()) {
            resetPrimaryAddress(user);
            address.setIsPrimary(true);
        }
    }
}
