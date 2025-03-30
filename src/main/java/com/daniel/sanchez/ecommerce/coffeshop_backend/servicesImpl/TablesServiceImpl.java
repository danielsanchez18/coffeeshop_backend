package com.daniel.sanchez.ecommerce.coffeshop_backend.servicesImpl;

import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.TablesDTO;
import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Tables;
import com.daniel.sanchez.ecommerce.coffeshop_backend.mappers.TablesMapper;
import com.daniel.sanchez.ecommerce.coffeshop_backend.repositories.TablesRepository;
import com.daniel.sanchez.ecommerce.coffeshop_backend.services.TablesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TablesServiceImpl implements TablesService {

    @Autowired
    private TablesRepository tablesRepository;

    @Autowired
    private TablesMapper tablesMapper;

    @Override
    public TablesDTO create(TablesDTO tablesDTO) {
        validateTableNumberUnique(tablesDTO.getTableNumber());

        tablesDTO.setStatus("DISPONIBLE");

        Tables table = tablesMapper.toEntity(tablesDTO);
        return tablesMapper.toDTO(tablesRepository.save(table));
    }

    @Override
    public List<TablesDTO> findAll() {
        return tablesRepository.findAll()
                .stream()
                .map(tablesMapper::toDTO).toList();
    }

    @Override
    public Page<TablesDTO> findAll(Pageable pageable) {
        return tablesRepository.findAll(pageable)
                .map(tablesMapper::toDTO);
    }

    @Override
    public TablesDTO findById(Long id) {
        return tablesRepository.findById(id)
                .map(tablesMapper::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("La mesa con el id " + id + " no existe."));
    }

    @Override
    public Page<TablesDTO> findAllByTableNumber(Integer tableNumber, Pageable pageable) {
        return tablesRepository.findByTableNumber(tableNumber, pageable)
                .map(tablesMapper::toDTO);
    }

    @Override
    public Page<TablesDTO> findAllByStatus(String status, Pageable pageable) {
        return tablesRepository.findByStatus(status, pageable)
                .map(tablesMapper::toDTO);
    }

    @Override
    public TablesDTO update(TablesDTO tablesDTO) {
        Tables exitingTable = tablesRepository.findById(tablesDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("La mesa con el id " + tablesDTO.getId() + " no existe."));

        if (!exitingTable.getTableNumber().equals(tablesDTO.getTableNumber())) {
            validateTableNumberUnique(tablesDTO.getTableNumber());
        }

        exitingTable.setTableNumber(tablesDTO.getTableNumber());
        exitingTable.setStatus(tablesDTO.getStatus());

        return tablesMapper.toDTO(tablesRepository.save(exitingTable));
    }

    @Override
    public void delete(Long id) {
        Tables table = tablesRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La mesa con el id " + id + " no existe."));

        tablesRepository.delete(table);
    }



    // METODOS DE VALIDACION

    private void validateTableNumberUnique(Integer tableNumber) {
        if (tablesRepository.existsByTableNumber(tableNumber)) {
            throw new IllegalArgumentException("Una mesa con el número " + tableNumber + " ya existe.");
        }
    }

}
