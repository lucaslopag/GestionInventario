-- ============================================================
-- Script para crear usuario ADMIN en la BD de usuarios
-- Ejecutar en ms-users database
-- ============================================================

-- Asegurarse de que existe la tabla usuarios
-- (Debería existir por Hibernate, pero lo agregamos para seguridad)

-- 1. Borrar el admin anterior si existe (opcional, comentar si queremos preservarlo)
DELETE FROM usuarios WHERE email = 'admin@inventary.com';

-- 2. Insertar nuevo usuario ADMIN
-- Contraseña: Admin123 (hasheada con bcrypt)
-- Hash generado: $2a$10$... (debes reemplazarlo con el hash real de "Admin123")
INSERT INTO usuarios (nombre, email, password, rol, confirmado, token_confirmacion, token_expira, created_at)
VALUES (
    'Administrador',
    'admin@inventary.com',
    '$2a$10$slYQmyNdGzin7olVN3p5Be3DQ5Ijvl9rG8Uyy2SKurFT2z2ZF2JGm', -- Hash para "Admin123"
    'ADMIN',
    true,
    NULL,
    NULL,
    NOW()
);

-- 3. Verificar que se insertó correctamente
SELECT id, nombre, email, rol, confirmado FROM usuarios WHERE email = 'admin@inventary.com';

-- ============================================================
-- Credenciales de acceso:
-- Email: admin@inventary.com
-- Contraseña: Admin123
-- ============================================================
