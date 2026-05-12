package com.tfg.ms_inventory.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tfg.ms_inventory.Model.Movimiento;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

}
