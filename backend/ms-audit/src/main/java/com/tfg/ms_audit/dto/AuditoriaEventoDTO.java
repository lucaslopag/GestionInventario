package com.tfg.ms_audit.dto;

import java.time.LocalDateTime;

public class AuditoriaEventoDTO {
    private String servicioOrigen;
    private String accion;
    private Long entidadId;
    private String usuarioEmail;
    private LocalDateTime fecha;
    private String detalles;
    private Long productoId;
    private Long proveedorId;
    private Integer cantidad;
    private Integer stockResultante;

    public AuditoriaEventoDTO() {
    }

    public AuditoriaEventoDTO(String servicioOrigen, String accion, Long entidadId, String usuarioEmail, LocalDateTime fecha, String detalles) {
        this.servicioOrigen = servicioOrigen;
        this.accion = accion;
        this.entidadId = entidadId;
        this.usuarioEmail = usuarioEmail;
        this.fecha = fecha;
        this.detalles = detalles;
    }

    // Constructor completo
    public AuditoriaEventoDTO(String servicioOrigen, String accion, Long entidadId, String usuarioEmail, LocalDateTime fecha, String detalles, Long productoId, Long proveedorId, Integer cantidad, Integer stockResultante) {
        this.servicioOrigen = servicioOrigen;
        this.accion = accion;
        this.entidadId = entidadId;
        this.usuarioEmail = usuarioEmail;
        this.fecha = fecha;
        this.detalles = detalles;
        this.productoId = productoId;
        this.proveedorId = proveedorId;
        this.cantidad = cantidad;
        this.stockResultante = stockResultante;
    }

    public String getServicioOrigen() {
        return servicioOrigen;
    }

    public void setServicioOrigen(String servicioOrigen) {
        this.servicioOrigen = servicioOrigen;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public Long getEntidadId() {
        return entidadId;
    }

    public void setEntidadId(Long entidadId) {
        this.entidadId = entidadId;
    }

    public String getUsuarioEmail() {
        return usuarioEmail;
    }

    public void setUsuarioEmail(String usuarioEmail) {
        this.usuarioEmail = usuarioEmail;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Long getProveedorId() {
        return proveedorId;
    }

    public void setProveedorId(Long proveedorId) {
        this.proveedorId = proveedorId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getStockResultante() {
        return stockResultante;
    }

    public void setStockResultante(Integer stockResultante) {
        this.stockResultante = stockResultante;
    }
}
