package com.tfg.ms_suppliers.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tfg.ms_suppliers.model.Proveedor;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    boolean existsByEmail(String email);
}

