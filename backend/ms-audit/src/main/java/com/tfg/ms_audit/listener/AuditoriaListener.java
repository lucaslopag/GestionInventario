package com.tfg.ms_audit.listener;

import com.tfg.ms_audit.model.LogAuditoria;
import com.tfg.ms_audit.repository.LogAuditoriaRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuditoriaListener {

    @Autowired
    private LogAuditoriaRepository logRepository;

    @RabbitListener(queues = "cola.auditoria")
    public void recibirEventoAuditoria(LogAuditoria logEntrante) {
        System.out.println(" Recibido evento de auditoría para producto: " + logEntrante.getProductoId());

        logEntrante.setId(null);
        logRepository.save(logEntrante);

        System.out.println(" Auditoría guardada → accion=" + logEntrante.getAccion()
                + ", producto=" + logEntrante.getProductoId()
                + ", stock_resultante=" + logEntrante.getStockResultante());
    }
}