-- ============================================================
-- BASE DE DATOS: db_inventory
-- Responsable: Lucas (Alejandro provee el script)
-- Descripción: Control de stock y movimientos de inventario
-- ============================================================

CREATE DATABASE IF NOT EXISTS db_inventory
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE db_inventory;

-- ------------------------------------------------------------
-- Tabla: stock
-- Una fila por producto. Refleja la cantidad actual disponible.
-- NOTA: producto_id es un entero simple, SIN FK física a db_catalog
--       (arquitectura de microservicios: no hay FK entre BBDDs)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS stock (
    id                      BIGINT  NOT NULL AUTO_INCREMENT,
    producto_id             BIGINT  NOT NULL,
    cantidad_disponible     INT     NOT NULL DEFAULT 0,

    PRIMARY KEY (id),
    UNIQUE KEY uq_stock_producto (producto_id),
    CONSTRAINT chk_cantidad_no_negativa CHECK (cantidad_disponible >= 0)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- Tabla: movimientos
-- Registro histórico de todas las entradas y salidas de stock.
-- Campos clave:
--   - tipo: 'ENTRADA' o 'SALIDA'
--   - usuario_email: inyectado por el API Gateway desde el JWT
--   - stock_resultante: snapshot del stock tras el movimiento
--   - proveedor_id: solo aplica en ENTRADA (puede ser NULL en SALIDA)
-- NOTA: Sin FK físicas (arquitectura microservicios)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS movimientos (
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    producto_id         BIGINT          NOT NULL,
    proveedor_id        BIGINT          NULL,
    usuario_email       VARCHAR(150)    NOT NULL,
    cantidad            INT             NOT NULL,
    tipo                ENUM('ENTRADA', 'SALIDA') NOT NULL,
    fecha               DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    stock_resultante    INT             NOT NULL,

    PRIMARY KEY (id),
    INDEX idx_movimientos_producto (producto_id),
    INDEX idx_movimientos_fecha (fecha)
) ENGINE=InnoDB;
