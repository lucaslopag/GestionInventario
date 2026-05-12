package com.tfg.ms_suppliers.Service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.tfg.ms_suppliers.Model.Proveedor;
import com.tfg.ms_suppliers.Repository.ProveedorRepository;
import java.util.List;
import com.tfg.ms_suppliers.DTO.ProveedorDTO;
import java.util.stream.Collectors;

@Service
public class ProveedorService {

    @Autowired
    private ProveedorRepository proveedorRepository;

    public List<ProveedorDTO> findAll() {
        return proveedorRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public ProveedorDTO findById(Long id) {
        return proveedorRepository.findById(id).map(this::mapToDTO).orElse(null);
    }

    public ProveedorDTO save(ProveedorDTO proveedorDTO) {
        if (proveedorRepository.existsByEmail(proveedorDTO.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        Proveedor proveedor = mapToEntity(proveedorDTO);
        Proveedor proveedorGuardado = proveedorRepository.save(proveedor);
        return mapToDTO(proveedorGuardado);
    }

    public ProveedorDTO update(Long id, ProveedorDTO proveedorDTO) {
        Proveedor proveedorExistente = proveedorRepository.findById(id).orElse(null);
        if (proveedorExistente == null) {
            return null;
        }
        
        // Verificar si el email cambia y si el nuevo ya existe
        if (!proveedorExistente.getEmail().equals(proveedorDTO.getEmail()) && 
            proveedorRepository.existsByEmail(proveedorDTO.getEmail())) {
            throw new RuntimeException("El nuevo email ya está registrado por otro proveedor");
        }

        proveedorExistente.setNombre(proveedorDTO.getNombre());
        proveedorExistente.setDireccion(proveedorDTO.getDireccion());
        proveedorExistente.setEmail(proveedorDTO.getEmail());
        
        return mapToDTO(proveedorRepository.save(proveedorExistente));
    }

    public void delete(Long id) {
        proveedorRepository.deleteById(id);
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

