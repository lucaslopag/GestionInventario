package com.tfg.ms_audit.listener;

import com.tfg.ms_audit.dto.AuditoriaEventoDTO;
import com.tfg.ms_audit.model.LogAuditoria;
import com.tfg.ms_audit.repository.LogAuditoriaRepository;
import com.tfg.ms_audit.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class AuditoriaListener {

    @Autowired
    private LogAuditoriaRepository logRepository;

    /**
     * Escucha eventos de auditoría de múltiples microservicios (Users, Catalog, Inventory, Suppliers).
     * Todos los eventos se procesan de forma centralizada y se guardan en la base de datos de auditoría.
     */
    @RabbitListener(queues = {
            RabbitMQConfig.COLA_AUDITORIA_USERS,
            RabbitMQConfig.COLA_AUDITORIA_CATALOG,
            RabbitMQConfig.COLA_AUDITORIA_INVENTORY,
            RabbitMQConfig.COLA_AUDITORIA_SUPPLIERS
    })
    public void recibirEventoAuditoria(AuditoriaEventoDTO evento) {
        System.out.println("\n[AUDIT] Nuevo evento recibido de: " + evento.getServicioOrigen());
        System.out.println("Acción: " + evento.getAccion() + " | Entidad ID: " + evento.getEntidadId());

        try {
            LogAuditoria log = new LogAuditoria();
            log.setServicioOrigen(evento.getServicioOrigen());
            log.setAccion(evento.getAccion());
            log.setEntidadId(evento.getEntidadId());
            log.setUsuarioEmail(evento.getUsuarioEmail());
            log.setFecha(evento.getFecha() != null ? evento.getFecha() : LocalDateTime.now());
            log.setDetalles(evento.getDetalles());

            // Nuevos campos mapeados directamente
            log.setProductoId(evento.getProductoId());
            log.setProveedorId(evento.getProveedorId());
            log.setCantidad(evento.getCantidad());
            log.setStockResultante(evento.getStockResultante());

            // Lógica de respaldo para compatibilidad si los campos específicos son nulos
            if (log.getProductoId() == null && "CATALOG".equalsIgnoreCase(evento.getServicioOrigen())) {
                log.setProductoId(evento.getEntidadId());
            } else if (log.getProductoId() == null && "INVENTORY".equalsIgnoreCase(evento.getServicioOrigen())) {
                log.setProductoId(evento.getEntidadId());
            } else if (log.getProveedorId() == null && "SUPPLIERS".equalsIgnoreCase(evento.getServicioOrigen())) {
                log.setProveedorId(evento.getEntidadId());
            }

            logRepository.save(log);
            System.out.println("[AUDIT] Registro guardado con éxito.");
            
        } catch (Exception e) {
            System.err.println("[AUDIT] Error al procesar evento de auditoría: " + e.getMessage());
            // En un entorno real, aquí se enviaría a una DLQ o se registraría el error
        }
    }
}