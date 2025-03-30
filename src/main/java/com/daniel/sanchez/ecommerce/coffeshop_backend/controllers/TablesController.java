package com.daniel.sanchez.ecommerce.coffeshop_backend.controllers;

import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.TablesDTO;
import com.daniel.sanchez.ecommerce.coffeshop_backend.services.TablesService;
import com.daniel.sanchez.ecommerce.coffeshop_backend.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tables")
@CrossOrigin("*")
public class TablesController {

    @Autowired
    private TablesService tablesService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody TablesDTO tablesDTO) {
        try {
            TablesDTO createdTable = tablesService.create(tablesDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseUtil.successResponse("Mesa creada exitosamente", createdTable));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseUtil.errorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.errorResponse("Error al crear la mesa"));
        }
    }

    @GetMapping
    public ResponseEntity<?> findAll() {
        try {
            List<TablesDTO> tables = tablesService.findAll();
            return ResponseEntity.ok(ResponseUtil.successResponse("Mesas encontradas exitosamente", tables));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.errorResponse("Error al buscar las mesas"));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAllPage(@PageableDefault(size = 10, page = 0) Pageable pageable) {
        try {
            Page<TablesDTO> tables = tablesService.findAll(pageable);
            return ResponseEntity.ok(ResponseUtil.successResponse("Mesas encontradas exitosamente", tables));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.errorResponse("Error al buscar las mesas"));
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            TablesDTO table = tablesService.findById(id);
            return ResponseEntity.ok(ResponseUtil.successResponse("Mesa encontrada", table));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.errorResponse(ex.getMessage()));
        }
    }

    @GetMapping("/by-number/{number}")
    public ResponseEntity<?> findByNumber(@PathVariable Integer number, @PageableDefault(size = 10, page = 0) Pageable pageable) {
        try {
            Page<TablesDTO> tables = tablesService.findAllByTableNumber(number, pageable);
            return ResponseEntity.ok(ResponseUtil.successResponse("Mesas encontradas exitosamente", tables));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.errorResponse(ex.getMessage()));
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> findByStatus(@PathVariable String status, @PageableDefault(size = 10, page = 0) Pageable pageable) {
        try {
            Page<TablesDTO> tables = tablesService.findAllByStatus(status, pageable);
            return ResponseEntity.ok(ResponseUtil.successResponse("Mesas encontradas exitosamente", tables));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.errorResponse(ex.getMessage()));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody TablesDTO tablesDTO) {
        try {
            tablesDTO.setId(id);
            TablesDTO updatedTable = tablesService.update(tablesDTO);
            return ResponseEntity.ok(ResponseUtil.successResponse("Mesa actualizada exitosamente", updatedTable));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseUtil.errorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.errorResponse("Error al actualizar la mesa"));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            tablesService.delete(id);
            return ResponseEntity.ok(ResponseUtil.successResponse("Mesa eliminada exitosamente", null));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseUtil.errorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.errorResponse("Error al eliminar la mesa"));
        }
    }

}