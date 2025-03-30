package com.daniel.sanchez.ecommerce.coffeshop_backend.services;

import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.TablesDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TablesService {

    TablesDTO create(TablesDTO tablesDTO);

    List<TablesDTO> findAll();

    Page<TablesDTO> findAll(Pageable pageable);

    TablesDTO findById(Long id);

    Page<TablesDTO> findAllByTableNumber(Integer tableNumber, Pageable pageable);

    Page<TablesDTO> findAllByStatus(String status, Pageable pageable);

    TablesDTO update(TablesDTO tablesDTO);

    void delete(Long id);

}
