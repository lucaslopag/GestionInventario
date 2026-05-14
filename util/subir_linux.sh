#!/bin/bash
# ============================================================
#  SUBIR CAMBIOS A GITHUB — Linux
#  Uso: bash subir_linux.sh
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
echo "   SUBIR CAMBIOS -> GitHub/${REPO}"
echo "======================================================"
echo ""
echo "Archivos modificados/nuevos:"
git status --short
echo ""

read -p "¿Deseas subir estos cambios? (y/n): " CONFIRM
echo ""
if [[ "$CONFIRM" != "y" && "$CONFIRM" != "Y" ]]; then
  echo "Operación cancelada."
  echo ""
  exit 0
fi

read -p "Mensaje del commit (Enter = 'update'): " COMMIT_MSG
COMMIT_MSG="${COMMIT_MSG:-update}"

echo ""
git add .
git commit -m "$COMMIT_MSG" 2>/dev/null || echo "[i] Nada nuevo que commitear."

# Actualizar remote con el token
git remote set-url origin "$REPO_URL" 2>/dev/null || git remote add origin "$REPO_URL"

# --- PULL AUTOMATICO antes de subir para evitar conflictos (priorizando lo local) ---
echo "[i] Sincronizando con el servidor antes de subir..."
git config pull.rebase false
GIT_MERGE_AUTOEDIT=no git pull --no-edit -X ours origin "$BRANCH" 2>&1
if [ $? -ne 0 ]; then
  echo ""
  echo "======================================================"
  echo "   ERROR al hacer pull. Hay conflictos que resolver"
  echo "   manualmente antes de poder subir."
  echo "======================================================"
  echo ""
  git remote set-url origin "https://github.com/${USUARIO}/${REPO}.git" 2>/dev/null
  exit 1
fi

echo ""
echo "[->] Subiendo..."
if GIT_MERGE_AUTOEDIT=no git push origin "$BRANCH" 2>&1; then
  echo ""
  echo "======================================================"
  echo "   OK - ¡Cambios subidos correctamente!"
  echo "   https://github.com/${USUARIO}/${REPO}"
  echo "======================================================"
else
  echo ""
  echo "======================================================"
  echo "   ERROR al subir. Comprueba el token en .token"
  echo "======================================================"
fi

# Quitar el token de la URL del remote por seguridad
git remote set-url origin "https://github.com/${USUARIO}/${REPO}.git" 2>/dev/null

echo ""
