package com.tfg.ms_audit.repository;

import com.tfg.ms_audit.model.LogAuditoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface LogAuditoriaRepository extends JpaRepository<LogAuditoria, Long> {

    @Query("SELECT l FROM LogAuditoria l WHERE " +
           "(:desde IS NULL OR l.fecha >= :desde) AND " +
           "(:hasta IS NULL OR l.fecha <= :hasta) " +
           "ORDER BY l.fecha DESC")
    Page<LogAuditoria> findAllFiltrado(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta, Pageable pageable);

    @Query("SELECT l FROM LogAuditoria l WHERE l.productoId = :productoId AND " +
           "(:desde IS NULL OR l.fecha >= :desde) AND " +
           "(:hasta IS NULL OR l.fecha <= :hasta) " +
           "ORDER BY l.fecha DESC")
    Page<LogAuditoria> findByProductoIdFiltrado(@Param("productoId") Long productoId, @Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta, Pageable pageable);

    @Query("SELECT l FROM LogAuditoria l WHERE l.usuarioEmail = :usuarioEmail AND " +
           "(:desde IS NULL OR l.fecha >= :desde) AND " +
           "(:hasta IS NULL OR l.fecha <= :hasta) " +
           "ORDER BY l.fecha DESC")
    Page<LogAuditoria> findByUsuarioEmailFiltrado(@Param("usuarioEmail") String usuarioEmail, @Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta, Pageable pageable);

    @Query("SELECT l FROM LogAuditoria l WHERE l.servicioOrigen = :servicioOrigen AND " +
           "(:desde IS NULL OR l.fecha >= :desde) AND " +
           "(:hasta IS NULL OR l.fecha <= :hasta) " +
           "ORDER BY l.fecha DESC")
    Page<LogAuditoria> findByServicioOrigenFiltrado(@Param("servicioOrigen") String servicioOrigen, @Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta, Pageable pageable);

}
