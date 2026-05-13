-- ============================================================
-- BASE DE DATOS: db_users
-- Responsable: Alejandro
-- Descripción: Gestión de usuarios, autenticación JWT y confirmación por email
-- ============================================================

CREATE DATABASE IF NOT EXISTS db_users
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE db_users;

-- ------------------------------------------------------------
-- Tabla: usuarios
-- Almacena los datos de cada usuario del sistema.
-- Campos clave:
--   - rol: 'ADMIN' o 'EMPLEADO'
--   - confirmado: el usuario debe confirmar su email antes de poder hacer login
--   - token_confirmacion: token UUID enviado por email
--   - token_expira: el token caduca (recomendado: 24h)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuarios (
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    nombre              VARCHAR(100)    NOT NULL,
    email               VARCHAR(150)    NOT NULL,
    password            VARCHAR(255)    NOT NULL,        -- BCrypt hash
    rol                 VARCHAR(20)     NOT NULL DEFAULT 'ROLE_USER',
    confirmado          BOOLEAN         NOT NULL DEFAULT FALSE,
    token_confirmacion  VARCHAR(100)    NULL,
    token_expira        DATETIME        NULL,
    fecha_creacion      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uq_usuarios_email (email)
) ENGINE=InnoDB;
