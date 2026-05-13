package com.tfg.ms_audit.repository;

import com.tfg.ms_audit.model.LogAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LogAuditoriaRepository extends JpaRepository<LogAuditoria, Long> {

    List<LogAuditoria> findByProductoIdOrderByFechaDesc(Long productoId);

    List<LogAuditoria> findByUsuarioEmailOrderByFechaDesc(String usuarioEmail);

    List<LogAuditoria> findByServicioOrigenOrderByFechaDesc(String servicioOrigen);

    List<LogAuditoria> findByEntidadIdOrderByFechaDesc(Long entidadId);

    List<LogAuditoria> findAllByOrderByFechaDesc();
}