-- ============================================================
-- BASE DE DATOS: db_catalog
-- Responsable: Lucas (Alejandro provee el script)
-- Descripción: Catálogo de productos con borrado lógico
-- ============================================================

CREATE DATABASE IF NOT EXISTS db_catalog
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE db_catalog;

-- ------------------------------------------------------------
-- Tabla: productos
-- Almacena el catálogo de productos de la empresa.
-- Campos clave:
--   - sku: código único de producto (Stock Keeping Unit)
--   - precio_neto: precio sin IVA
--   - activo: FALSE = borrado lógico (el registro permanece en BD)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS productos (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    nombre          VARCHAR(200)    NOT NULL,
    sku             VARCHAR(50)     NOT NULL,
    descripcion     TEXT            NULL,
    precio_neto     DECIMAL(10, 2)  NOT NULL,
    activo          BOOLEAN         NOT NULL DEFAULT TRUE,

    PRIMARY KEY (id),
    UNIQUE KEY uq_productos_sku (sku)
) ENGINE=InnoDB;
