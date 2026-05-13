package com.tfg.ms_catalog.repository;

import com.tfg.ms_catalog.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByActivoTrue();

    List<Producto> findByActivoFalse();

    List<Producto> findByNombreContaining(String nombre);

    List<Producto> findByNombreContainingAndActivoFalse(String nombre);

    Optional<Producto> findBySku(String sku);

}
