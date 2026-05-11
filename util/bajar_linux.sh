#!/bin/bash
# ============================================================
#  BAJAR CAMBIOS DE GITHUB — Linux
#  Uso: bash bajar_linux.sh
# ============================================================

cd "$(dirname "$0")/.." || exit 1

USUARIO="lucaslopag"
REPO="GestionInventario"
BRANCH="main"

if [ ! -f ".token" ]; then
  echo "ERROR: No se encontró .token en la raíz del proyecto."
  echo "       Crea el fichero .token con tu token de GitHub."
  exit 1
fi

TOKEN=$(cat .token | tr -d '[:space:]')
REPO_URL="https://${USUARIO}:${TOKEN}@github.com/${USUARIO}/${REPO}.git"

echo ""
echo "======================================================"
echo "   BAJAR CAMBIOS <- GitHub/${REPO}"
echo "======================================================"
echo ""
echo "[i] Rama actual: $(git branch --show-current 2>/dev/null || echo 'desconocida')"
echo ""

read -p "¿Deseas bajar los cambios del repo? (y/n): " CONFIRM
echo ""
if [[ "$CONFIRM" != "y" && "$CONFIRM" != "Y" ]]; then
  echo "Operación cancelada."
  echo ""
  exit 0
fi

# Actualizar remote con el token
git remote set-url origin "$REPO_URL" 2>/dev/null || git remote add origin "$REPO_URL"

# Pull sin abrir editor (--no-edit evita que se abra vim)
echo "[->] Descargando cambios..."
if GIT_MERGE_AUTOEDIT=no git pull --no-edit origin "$BRANCH" 2>&1; then
  echo ""
  echo "======================================================"
  echo "   OK - ¡Cambios descargados correctamente!"
  echo "======================================================"
else
  echo ""
  echo "======================================================"
  echo "   ERROR - Hay conflictos locales sin resolver."
  echo "   Sube primero tus cambios con subir_linux.sh"
  echo "   o resuelve los conflictos manualmente."
  echo "======================================================"
fi

# Quitar el token de la URL del remote por seguridad
git remote set-url origin "https://github.com/${USUARIO}/${REPO}.git" 2>/dev/null

echo ""
