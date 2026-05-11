-- ============================================================
-- BASE DE DATOS: db_suppliers
-- Responsable: Lucas (Alejandro provee el script)
-- Descripción: Gestión de proveedores
-- ============================================================

CREATE DATABASE IF NOT EXISTS db_suppliers
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE db_suppliers;

-- ------------------------------------------------------------
-- Tabla: proveedores
-- Almacena los datos de los proveedores de la empresa.
-- Campos clave:
--   - email: único por proveedor, se usa para validación
--   - direccion: opcional
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS proveedores (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    nombre      VARCHAR(200)    NOT NULL,
    email       VARCHAR(150)    NOT NULL,
    telefono    VARCHAR(20)     NULL,
    direccion   VARCHAR(300)    NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uq_proveedores_email (email)
) ENGINE=InnoDB;
