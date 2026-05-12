package com.tfg.ms_suppliers.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tfg.ms_suppliers.Model.Proveedor;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    boolean existsByEmail(String email);
}
