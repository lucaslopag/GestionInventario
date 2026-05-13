package com.tfg.ms_audit.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "logs")
public class LogAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entidad_id")
    private Long entidadId;

    @Column(name = "servicio_origen", nullable = false, length = 50)
    private String servicioOrigen;

    @Column(name = "detalles", length = 500)
    private String detalles;

    @Column(name = "producto_id")
    private Long productoId;

    @Column(name = "proveedor_id")
    private Long proveedorId;

    @Column(name = "usuario_email", nullable = false, length = 150)
    private String usuarioEmail;

    @Column(nullable = false)
    private String accion;

    @Column
    private Integer cantidad;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(name = "stock_resultante")
    private Integer stockResultante;

    // Constructores
    public LogAuditoria() {}

    public LogAuditoria(Long id, Long entidadId, String servicioOrigen, String detalles, String usuarioEmail, String accion, LocalDateTime fecha) {
        this.id = id;
        this.entidadId = entidadId;
        this.servicioOrigen = servicioOrigen;
        this.detalles = detalles;
        this.usuarioEmail = usuarioEmail;
        this.accion = accion;
        this.fecha = fecha;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEntidadId() { return entidadId; }
    public void setEntidadId(Long entidadId) { this.entidadId = entidadId; }

    public String getServicioOrigen() { return servicioOrigen; }
    public void setServicioOrigen(String servicioOrigen) { this.servicioOrigen = servicioOrigen; }

    public String getDetalles() { return detalles; }
    public void setDetalles(String detalles) { this.detalles = detalles; }

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public Long getProveedorId() { return proveedorId; }
    public void setProveedorId(Long proveedorId) { this.proveedorId = proveedorId; }

    public String getUsuarioEmail() { return usuarioEmail; }
    public void setUsuarioEmail(String usuarioEmail) { this.usuarioEmail = usuarioEmail; }

    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public Integer getStockResultante() { return stockResultante; }
    public void setStockResultante(Integer stockResultante) { this.stockResultante = stockResultante; }
}