-- ============================================================
-- BASE DE DATOS: db_audit
-- Responsable: Ángel (Alejandro provee el script)
-- Descripción: Registro de auditoría (append-only)
-- ============================================================

CREATE DATABASE IF NOT EXISTS db_audit
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE db_audit;

-- ------------------------------------------------------------
-- Tabla: logs
-- Tabla append-only: solo se insertan registros, NUNCA se editan ni borran.
-- Los datos llegan desde ms-inventory a través de RabbitMQ (cola.auditoria).
-- Campos clave:
--   - accion: 'ENTRADA' o 'SALIDA'
--   - Los demás campos son snapshot del evento en el momento del movimiento
-- NOTA: Sin FK físicas (arquitectura microservicios)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS logs (
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    producto_id         BIGINT          NOT NULL,
    proveedor_id        BIGINT          NULL,
    usuario_email       VARCHAR(150)    NOT NULL,
    accion              ENUM('ENTRADA', 'SALIDA') NOT NULL,
    cantidad            INT             NOT NULL,
    fecha               DATETIME        NOT NULL,
    stock_resultante    INT             NOT NULL,

    PRIMARY KEY (id),
    INDEX idx_logs_producto (producto_id),
    INDEX idx_logs_usuario (usuario_email),
    INDEX idx_logs_fecha (fecha)
) ENGINE=InnoDB;
