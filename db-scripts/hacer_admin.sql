-- ============================================================
-- Convertir una cuenta existente en ADMIN
-- Ejecutar en la base de datos de ms-users
-- ============================================================
-- No hay ninguna contrasena de administrador guardada en el repositorio.
--
-- Pasos:
-- 1. Registrate en la aplicacion con tu propio correo y una contrasena que elijas.
-- 2. Cambia TU_CORREO por ese correo en la sentencia de abajo y ejecutala.
-- 3. Inicia sesion de nuevo para que el token incluya el rol ADMIN.

UPDATE usuarios
SET rol = 'ADMIN', confirmado = true, token_confirmacion = NULL, token_expira = NULL
WHERE email = 'TU_CORREO';

-- Comprobar el resultado
SELECT id, nombre, email, rol, confirmado FROM usuarios WHERE email = 'TU_CORREO';
