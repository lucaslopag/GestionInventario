package com.tfg.ms_inventory.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "movimientos")
public class Movimiento {
    // id, productoId, proveedorId, usuarioEmail, cantidad, tipo (entrada, salida),
    // fecha, stockResultante

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "producto_id")
    private Long productoId;

    @Column(name = "proveedor_id")
    private Long proveedorId;

    @Column(name = "usuario_email")
    private String usuarioEmail;

    @Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "fecha")
    private java.time.LocalDateTime fecha;

    @Column(name = "stock_resultante")
    private Integer stockResultante;
}
