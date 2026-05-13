package com.tfg.ms_suppliers.Service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.tfg.ms_suppliers.Model.Proveedor;
import com.tfg.ms_suppliers.Repository.ProveedorRepository;
import java.util.List;
import com.tfg.ms_suppliers.DTO.ProveedorDTO;
import java.util.stream.Collectors;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Service
public class ProveedorService {

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private void auditar(String accion, Long entidadId, String usuarioEmail, String detalles) {
        com.tfg.ms_suppliers.DTO.AuditoriaEventoDTO evento = new com.tfg.ms_suppliers.DTO.AuditoriaEventoDTO(
            "SUPPLIERS", accion, entidadId, usuarioEmail, java.time.LocalDateTime.now(), detalles
        );
        rabbitTemplate.convertAndSend(
            com.tfg.ms_suppliers.Config.RabbitMQConfig.EXCHANGE_AUDITORIA,
            com.tfg.ms_suppliers.Config.RabbitMQConfig.ROUTING_KEY_AUDITORIA,
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
        if (proveedorRepository.existsByEmail(proveedorDTO.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        Proveedor proveedor = mapToEntity(proveedorDTO);
        Proveedor proveedorGuardado = proveedorRepository.save(proveedor);
        auditar("POST", proveedorGuardado.getId(), usuarioEmail, "Creación de proveedor");
        return mapToDTO(proveedorGuardado);
    }

    public ProveedorDTO update(Long id, ProveedorDTO proveedorDTO, String usuarioEmail) {
        Proveedor proveedorExistente = proveedorRepository.findById(id).orElse(null);
        if (proveedorExistente == null) {
            return null;
        }
        
        // Verificar si el email cambia y si el nuevo ya existe
        if (!proveedorExistente.getEmail().equals(proveedorDTO.getEmail()) && 
            proveedorRepository.existsByEmail(proveedorDTO.getEmail())) {
            throw new IllegalArgumentException("El nuevo email ya está registrado por otro proveedor");
        }

        proveedorExistente.setNombre(proveedorDTO.getNombre());
        proveedorExistente.setDireccion(proveedorDTO.getDireccion());
        proveedorExistente.setEmail(proveedorDTO.getEmail());
        
        ProveedorDTO actualizado = mapToDTO(proveedorRepository.save(proveedorExistente));
        auditar("PUT", id, usuarioEmail, "Actualización de proveedor");
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
        proveedorDTO.setDireccion(proveedor.getDireccion());
        proveedorDTO.setEmail(proveedor.getEmail());
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
