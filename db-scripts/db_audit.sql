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
-- Los eventos llegan de todos los microservicios a través de RabbitMQ.
-- Campos clave:
--   - accion: tipo de evento (POST, GET, LOGIN, POST_MOVIMIENTO...)
--   - producto_id, cantidad y stock_resultante solo se rellenan en eventos de stock
-- NOTA: Sin FK físicas (arquitectura microservicios)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS logs (
    id               BIGINT          NOT NULL AUTO_INCREMENT,
    servicio_origen  VARCHAR(50)     NOT NULL,
    accion           VARCHAR(255)    NOT NULL,
    usuario_email    VARCHAR(150)    NOT NULL,
    entidad_id       BIGINT          NULL,
    producto_id      BIGINT          NULL,
    proveedor_id     BIGINT          NULL,
    cantidad         INT             NULL,
    stock_resultante INT             NULL,
    detalles         VARCHAR(500)    NULL,
    fecha            DATETIME(6)     NOT NULL,

    PRIMARY KEY (id),
    INDEX idx_logs_producto (producto_id),
    INDEX idx_logs_usuario (usuario_email),
    INDEX idx_logs_fecha (fecha)
) ENGINE=InnoDB;
