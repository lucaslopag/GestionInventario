package com.tfg.ms_suppliers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tfg.ms_suppliers.model.Proveedor;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    
    @Query("SELECT COUNT(p) > 0 FROM Proveedor p WHERE LOWER(p.email) = LOWER(:email)")
    boolean existsByEmail(@Param("email") String email);
}

