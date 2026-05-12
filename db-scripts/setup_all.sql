-- ============================================================
-- SCRIPT MAESTRO DE INICIALIZACIÓN
-- Crea el usuario MySQL y todas las bases de datos del proyecto
-- Usuario: tfg / Contraseña: tfg1234
-- Ejecutar como root: mysql -u root -p < setup_all.sql
-- ============================================================

-- ============================================================
-- 1. CREAR USUARIO tfg
-- ============================================================
CREATE USER IF NOT EXISTS 'tfg'@'localhost' IDENTIFIED BY 'tfg1234';
CREATE USER IF NOT EXISTS 'tfg'@'127.0.0.1' IDENTIFIED BY 'tfg1234';
CREATE USER IF NOT EXISTS 'tfg'@'%'         IDENTIFIED BY 'tfg1234';

-- ============================================================
-- 2. BASE DE DATOS: db_users  (MS-Users)
-- ============================================================
CREATE DATABASE IF NOT EXISTS db_users
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE db_users;

CREATE TABLE IF NOT EXISTS usuarios (
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    nombre              VARCHAR(100)    NOT NULL,
    email               VARCHAR(150)    NOT NULL,
    password            VARCHAR(255)    NOT NULL,
    rol                 ENUM('ADMIN', 'EMPLEADO') NOT NULL DEFAULT 'EMPLEADO',
    confirmado          BOOLEAN         NOT NULL DEFAULT FALSE,
    token_confirmacion  VARCHAR(100)    NULL,
    token_expira        DATETIME        NULL,
    fecha_creacion      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uq_usuarios_email (email)
) ENGINE=InnoDB;

-- ============================================================
-- 3. BASE DE DATOS: db_catalog  (MS-Catalog)
-- ============================================================
CREATE DATABASE IF NOT EXISTS db_catalog
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE db_catalog;

CREATE TABLE IF NOT EXISTS productos (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    nombre      VARCHAR(200)    NOT NULL,
    sku         VARCHAR(50)     NOT NULL,
    descripcion TEXT            NULL,
    precio_neto DECIMAL(10, 2)  NOT NULL,
    activo      BOOLEAN         NOT NULL DEFAULT TRUE,

    PRIMARY KEY (id),
    UNIQUE KEY uq_productos_sku (sku)
) ENGINE=InnoDB;

-- ============================================================
-- 4. BASE DE DATOS: db_suppliers  (MS-Suppliers)
-- ============================================================
CREATE DATABASE IF NOT EXISTS db_suppliers
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE db_suppliers;

CREATE TABLE IF NOT EXISTS proveedores (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    nombre      VARCHAR(200)    NOT NULL,
    email       VARCHAR(150)    NOT NULL,
    telefono    VARCHAR(20)     NULL,
    direccion   VARCHAR(300)    NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uq_proveedores_email (email)
) ENGINE=InnoDB;

-- ============================================================
-- 5. BASE DE DATOS: db_inventory  (MS-Inventory)
-- ============================================================
CREATE DATABASE IF NOT EXISTS db_inventory
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE db_inventory;

CREATE TABLE IF NOT EXISTS stock (
    id                  BIGINT  NOT NULL AUTO_INCREMENT,
    producto_id         BIGINT  NOT NULL,
    cantidad_disponible INT     NOT NULL DEFAULT 0,

    PRIMARY KEY (id),
    UNIQUE KEY uq_stock_producto (producto_id),
    CONSTRAINT chk_cantidad_no_negativa CHECK (cantidad_disponible >= 0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS movimientos (
    id               BIGINT          NOT NULL AUTO_INCREMENT,
    producto_id      BIGINT          NOT NULL,
    proveedor_id     BIGINT          NULL,
    usuario_email    VARCHAR(150)    NOT NULL,
    cantidad         INT             NOT NULL,
    tipo             ENUM('ENTRADA', 'SALIDA') NOT NULL,
    fecha            DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    stock_resultante INT             NOT NULL,

    PRIMARY KEY (id),
    INDEX idx_movimientos_producto (producto_id),
    INDEX idx_movimientos_fecha (fecha)
) ENGINE=InnoDB;

-- ============================================================
-- 6. BASE DE DATOS: db_audit  (MS-Audit)
-- ============================================================
CREATE DATABASE IF NOT EXISTS db_audit
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE db_audit;

CREATE TABLE IF NOT EXISTS logs (
    id               BIGINT          NOT NULL AUTO_INCREMENT,
    producto_id      BIGINT          NOT NULL,
    proveedor_id     BIGINT          NULL,
    usuario_email    VARCHAR(150)    NOT NULL,
    accion           ENUM('ENTRADA', 'SALIDA') NOT NULL,
    cantidad         INT             NOT NULL,
    fecha            DATETIME        NOT NULL,
    stock_resultante INT             NOT NULL,

    PRIMARY KEY (id),
    INDEX idx_logs_producto (producto_id),
    INDEX idx_logs_usuario (usuario_email),
    INDEX idx_logs_fecha (fecha)
) ENGINE=InnoDB;

-- ============================================================
-- 7. PERMISOS: grant al usuario tfg sobre todas las BBDDs
-- ============================================================
GRANT ALL PRIVILEGES ON db_users.*     TO 'tfg'@'localhost';
GRANT ALL PRIVILEGES ON db_catalog.*   TO 'tfg'@'localhost';
GRANT ALL PRIVILEGES ON db_suppliers.* TO 'tfg'@'localhost';
GRANT ALL PRIVILEGES ON db_inventory.* TO 'tfg'@'localhost';
GRANT ALL PRIVILEGES ON db_audit.*     TO 'tfg'@'localhost';

GRANT ALL PRIVILEGES ON db_users.*     TO 'tfg'@'127.0.0.1';
GRANT ALL PRIVILEGES ON db_catalog.*   TO 'tfg'@'127.0.0.1';
GRANT ALL PRIVILEGES ON db_suppliers.* TO 'tfg'@'127.0.0.1';
GRANT ALL PRIVILEGES ON db_inventory.* TO 'tfg'@'127.0.0.1';
GRANT ALL PRIVILEGES ON db_audit.*     TO 'tfg'@'127.0.0.1';

GRANT ALL PRIVILEGES ON db_users.*     TO 'tfg'@'%';
GRANT ALL PRIVILEGES ON db_catalog.*   TO 'tfg'@'%';
GRANT ALL PRIVILEGES ON db_suppliers.* TO 'tfg'@'%';
GRANT ALL PRIVILEGES ON db_inventory.* TO 'tfg'@'%';
GRANT ALL PRIVILEGES ON db_audit.*     TO 'tfg'@'%';

FLUSH PRIVILEGES;

-- ============================================================
-- VERIFICACIÓN FINAL
-- ============================================================
SELECT 'USUARIO CREADO:'  AS Info, User, Host FROM mysql.user WHERE User = 'tfg';
SELECT 'BASES DE DATOS:'  AS Info, schema_name
    FROM information_schema.schemata
    WHERE schema_name IN ('db_users','db_catalog','db_suppliers','db_inventory','db_audit')
    ORDER BY schema_name;

SELECT '✅ Setup completado correctamente.' AS Resultado;
