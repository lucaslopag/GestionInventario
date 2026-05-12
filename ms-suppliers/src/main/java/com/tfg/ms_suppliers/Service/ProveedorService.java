package com.tfg.ms_suppliers.Service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.tfg.ms_suppliers.Model.Proveedor;
import com.tfg.ms_suppliers.Repository.ProveedorRepository;
import java.util.List;
import com.tfg.ms_suppliers.DTO.ProveedorDTO;
import java.util.stream.Collectors;
import java.util.Optional;

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

    public ProveedorDTO save(Proveedor proveedorDTO) {
        if (existsByEmail(proveedorDTO.getEmail())) {
            throw new IllegalArgumentException("El email ya está en uso por otro proveedor");
        }
        Proveedor proveedorGuardado = proveedorRepository.save(proveedorDTO);
        return mapToDTO(proveedorGuardado);
    }

    public ProveedorDTO update(Long id, Proveedor proveedorDTO) {
        Optional<Proveedor> proveedorExistente = proveedorRepository.findById(id);
        if (proveedorExistente.isPresent()) {
            Proveedor proveedor = proveedorExistente.get();
            if (!proveedor.getEmail().equals(proveedorDTO.getEmail()) && existsByEmail(proveedorDTO.getEmail())) {
                throw new IllegalArgumentException("El email ya está en uso por otro proveedor");
            }
            proveedor.setNombre(proveedorDTO.getNombre());
            proveedor.setDireccion(proveedorDTO.getDireccion());
            proveedor.setEmail(proveedorDTO.getEmail());
            return mapToDTO(proveedorRepository.save(proveedor));
        }
        return null;
    }

    public ProveedorDTO delete(Long id) {
        Optional<Proveedor> proveedorExistente = proveedorRepository.findById(id);
        if (proveedorExistente.isPresent()) {
            proveedorRepository.deleteById(id);
            return mapToDTO(proveedorExistente.get());
        }
        return null;
    }

    public boolean existsByEmail(String email) {
        return proveedorRepository.existsByEmail(email);
    }

    public ProveedorDTO mapToDTO(Proveedor proveedor) {
        ProveedorDTO proveedorDTO = new ProveedorDTO();
        proveedorDTO.setNombre(proveedor.getNombre());
        proveedorDTO.setDireccion(proveedor.getDireccion());
        proveedorDTO.setEmail(proveedor.getEmail());
        return proveedorDTO;
    }

}
