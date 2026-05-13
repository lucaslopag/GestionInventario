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
     * Escucha eventos de auditorÃ­a de mÃºltiples microservicios (Users, Catalog, Inventory, Suppliers).
     * Todos los eventos se procesan de forma centralizada y se guardan en la base de datos de auditorÃ­a.
     */
    @RabbitListener(queues = {
            RabbitMQConfig.COLA_AUDITORIA_USERS,
            RabbitMQConfig.COLA_AUDITORIA_CATALOG,
            RabbitMQConfig.COLA_AUDITORIA_INVENTORY,
            RabbitMQConfig.COLA_AUDITORIA_SUPPLIERS
    })
    public void recibirEventoAuditoria(AuditoriaEventoDTO evento) {
        System.out.println("\n[AUDIT] Nuevo evento recibido de: " + evento.getServicioOrigen());
        System.out.println("AcciÃ³n: " + evento.getAccion() + " | Entidad ID: " + evento.getEntidadId());

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

            // LÃ³gica de respaldo para compatibilidad si los campos especÃ­ficos son nulos
            if (log.getProductoId() == null && "CATALOG".equalsIgnoreCase(evento.getServicioOrigen())) {
                log.setProductoId(evento.getEntidadId());
            } else if (log.getProductoId() == null && "INVENTORY".equalsIgnoreCase(evento.getServicioOrigen())) {
                log.setProductoId(evento.getEntidadId());
            } else if (log.getProveedorId() == null && "SUPPLIERS".equalsIgnoreCase(evento.getServicioOrigen())) {
                log.setProveedorId(evento.getEntidadId());
            }

            logRepository.save(log);
            System.out.println("[AUDIT] Registro guardado con Ã©xito.");
            
        } catch (Exception e) {
            System.err.println("[AUDIT] Error al procesar evento de auditorÃ­a: " + e.getMessage());
            // En un entorno real, aquÃ­ se enviarÃ­a a una DLQ o se registrarÃ­a el error
        }
    }
}