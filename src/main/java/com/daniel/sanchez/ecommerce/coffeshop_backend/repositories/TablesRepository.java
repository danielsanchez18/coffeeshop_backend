package com.daniel.sanchez.ecommerce.coffeshop_backend.repositories;

import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Tables;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TablesRepository extends JpaRepository<Tables, Long> {

    boolean existsByTableNumber(Integer tableNumber);

    Page<Tables> findByTableNumber(Integer tableNumber, Pageable pageable);

    Page<Tables> findByStatus(String status, Pageable pageable);

}
