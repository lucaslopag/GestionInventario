package com.tfg.ms_suppliers.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.tfg.ms_suppliers.model.Proveedor;
import com.tfg.ms_suppliers.repository.ProveedorRepository;
import java.util.List;
import com.tfg.ms_suppliers.dto.ProveedorDTO;
import java.util.stream.Collectors;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Service
public class ProveedorService {

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private void auditar(String accion, Long entidadId, String usuarioEmail, String detalles) {
        com.tfg.ms_suppliers.dto.AuditoriaEventoDTO evento = new com.tfg.ms_suppliers.dto.AuditoriaEventoDTO(
            "SUPPLIERS", accion, entidadId, usuarioEmail, java.time.LocalDateTime.now(), detalles
        );
        rabbitTemplate.convertAndSend(
            com.tfg.ms_suppliers.config.RabbitMQConfig.EXCHANGE_AUDITORIA,
            com.tfg.ms_suppliers.config.RabbitMQConfig.ROUTING_KEY_AUDITORIA,
            evento
        );
    }

    public List<ProveedorDTO> findAll(String usuarioEmail) {
        auditar("GET_ALL", null, usuarioEmail, "Consulta de todos los proveedores");
        return proveedorRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public ProveedorDTO findById(Long id, String usuarioEmail) {
        auditar("GET", id, usuarioEmail, "Consulta de proveedor por ID");
        return proveedorRepository.findById(id).map(this::mapToDTO).orElse(null);
    }

    public ProveedorDTO save(ProveedorDTO proveedorDTO, String usuarioEmail) {
        // Normalizar email: trim y lowercase
        String emailNormalizado = proveedorDTO.getEmail().trim().toLowerCase();
        
        if (proveedorRepository.existsByEmail(emailNormalizado)) {
            throw new IllegalArgumentException("El email '" + emailNormalizado + "' ya está registrado en otro proveedor");
        }
        
        Proveedor proveedor = mapToEntity(proveedorDTO);
        proveedor.setEmail(emailNormalizado); // Guardar normalizado
        Proveedor proveedorGuardado = proveedorRepository.save(proveedor);
        auditar("POST", proveedorGuardado.getId(), usuarioEmail, "Creación de proveedor: " + proveedor.getNombre());
        return mapToDTO(proveedorGuardado);
    }

    public ProveedorDTO update(Long id, ProveedorDTO proveedorDTO, String usuarioEmail) {
        Proveedor proveedorExistente = proveedorRepository.findById(id).orElse(null);
        if (proveedorExistente == null) {
            return null;
        }
        
        // Normalizar email
        String emailNormalizado = proveedorDTO.getEmail().trim().toLowerCase();
        
        // Verificar si el email cambia y si el nuevo ya existe
        if (!proveedorExistente.getEmail().equals(emailNormalizado) && 
            proveedorRepository.existsByEmail(emailNormalizado)) {
            throw new IllegalArgumentException("El email '" + emailNormalizado + "' ya está registrado por otro proveedor");
        }

        proveedorExistente.setNombre(proveedorDTO.getNombre());
        proveedorExistente.setEmail(emailNormalizado);
        proveedorExistente.setDireccion(proveedorDTO.getDireccion());
        
        ProveedorDTO actualizado = mapToDTO(proveedorRepository.save(proveedorExistente));
        auditar("PUT", id, usuarioEmail, "Actualización de proveedor: " + proveedorExistente.getNombre());
        return actualizado;
    }

    public void delete(Long id, String usuarioEmail) {
        proveedorRepository.deleteById(id);
        auditar("DELETE", id, usuarioEmail, "Borrado de proveedor");
    }

    public ProveedorDTO mapToDTO(Proveedor proveedor) {
        ProveedorDTO proveedorDTO = new ProveedorDTO();
        proveedorDTO.setId(proveedor.getId());
        proveedorDTO.setNombre(proveedor.getNombre());
        proveedorDTO.setEmail(proveedor.getEmail());
        proveedorDTO.setDireccion(proveedor.getDireccion());
        return proveedorDTO;
    }

    private Proveedor mapToEntity(ProveedorDTO dto) {
        Proveedor entity = new Proveedor();
        entity.setNombre(dto.getNombre());
        entity.setEmail(dto.getEmail());
        entity.setDireccion(dto.getDireccion());
        return entity;
    }
}
