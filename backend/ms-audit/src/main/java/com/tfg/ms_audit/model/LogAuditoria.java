package com.tfg.ms_audit.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "logs")
public class LogAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(name = "proveedor_id")
    private Long proveedorId;

    @Column(name = "usuario_email", nullable = false, length = 150)
    private String usuarioEmail;

    @Column(nullable = false)
    private String accion;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(name = "stock_resultante", nullable = false)
    private Integer stockResultante;

    // Constructores
    public LogAuditoria() {}

    public LogAuditoria(Long id, Long productoId, Long proveedorId, String usuarioEmail, String accion, Integer cantidad, LocalDateTime fecha, Integer stockResultante) {
        this.id = id;
        this.productoId = productoId;
        this.proveedorId = proveedorId;
        this.usuarioEmail = usuarioEmail;
        this.accion = accion;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.stockResultante = stockResultante;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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