package com.daniel.sanchez.ecommerce.coffeshop_backend.mappers;

import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.TablesDTO;
import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Tables;
import org.springframework.stereotype.Component;

@Component
public class TablesMapper {

    public TablesDTO toDTO(Tables tables) {
        TablesDTO dto = new TablesDTO();
        dto.setId(tables.getId());
        dto.setTableNumber(tables.getTableNumber());
        dto.setStatus(tables.getStatus());
        dto.setAudit(tables.getAudit());
        return dto;
    }

    public Tables toEntity(TablesDTO dto) {
        Tables tables = new Tables();
        tables.setId(dto.getId());
        tables.setTableNumber(dto.getTableNumber());
        tables.setStatus(dto.getStatus());
        return tables;
    }

}
