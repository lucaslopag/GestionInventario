-- ============================================================
-- Script para limpiar BD de proveedores y eliminar constraints
-- Ejecutar en base de datos db_suppliers
-- ============================================================

-- 1. Eliminar la constraint UNIQUE en email si existe
ALTER TABLE proveedores DROP INDEX IF EXISTS email;

-- 2. Borrar todos los proveedores (LIMPIAR COMPLETAMENTE)
TRUNCATE TABLE proveedores;

-- 3. Verificar que está vacío
SELECT COUNT(*) as total_proveedores FROM proveedores;

-- ============================================================
-- Después de ejecutar esto, intenta crear un nuevo proveedor
-- ============================================================
