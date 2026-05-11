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

    public ProveedorDTO save(Proveedor proveedor) {
        Proveedor proveedorGuardado = proveedorRepository.save(proveedor);
        return mapToDTO(proveedorGuardado);
    }

    public ProveedorDTO update(Long id, Proveedor proveedor) {
        Proveedor proveedorExistente = proveedorRepository.findById(id);
        if (proveedorExistente == null) {
            return null;
        }
        proveedorExistente.setNombre(proveedor.getNombre());
        proveedorExistente.setDireccion(proveedor.getDireccion());
        proveedorExistente.setEmail(proveedor.getEmail());
        return mapToDTO(proveedorRepository.save(proveedorExistente));
    }

    public ProveedorDTO mapToDTO(Proveedor proveedor) {
        ProveedorDTO proveedorDTO = new ProveedorDTO();
        proveedorDTO.setNombre(proveedor.getNombre());
        proveedorDTO.setDireccion(proveedor.getDireccion());
        proveedorDTO.setEmail(proveedor.getEmail());
        return proveedorDTO;
    }

}
