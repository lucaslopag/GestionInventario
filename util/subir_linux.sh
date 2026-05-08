#!/bin/bash
# ============================================================
#  SUBIR CAMBIOS A GITHUB — Linux
#  Uso: bash subir_linux.sh
# ============================================================

# Cambiar al directorio raíz del proyecto
cd "$(dirname "$0")/.." || exit

USUARIO="lucaslopag"
REPO="GestionInventario"
BRANCH="main"

if [ ! -f ".token" ]; then
  echo "❌ No se encontró .token en esta carpeta."; exit 1
fi
TOKEN=$(cat .token | tr -d '[:space:]')
REPO_URL="https://${USUARIO}:${TOKEN}@github.com/${USUARIO}/${REPO}.git"

echo ""
echo "======================================================"
echo "   ⬆  SUBIR CAMBIOS → GitHub/${REPO}"
echo "======================================================"
echo ""
echo "Archivos modificados/nuevos:"
git status --short
echo ""

read -p "¿Deseas subir estos cambios? (y/n): " CONFIRM
echo ""
if [[ "$CONFIRM" != "y" && "$CONFIRM" != "Y" ]]; then
  echo "❌ Operación cancelada."; echo ""; exit 0
fi

read -p "Mensaje del commit (Enter = 'update'): " COMMIT_MSG
COMMIT_MSG="${COMMIT_MSG:-update}"

echo ""
git add .
git commit -m "$COMMIT_MSG" 2>/dev/null || echo "[i] Nada nuevo que commitear."
git remote set-url origin "$REPO_URL" 2>/dev/null || git remote add origin "$REPO_URL"

echo "[→] Subiendo..."
if git push origin "$BRANCH" 2>&1; then
  echo ""
  echo "======================================================"
  echo "   ✅ ¡Cambios subidos correctamente!"
  echo "   🔗 https://github.com/${USUARIO}/${REPO}"
  echo "======================================================"
else
  echo ""
  echo "======================================================"
  echo "   ❌ Error. Comprueba el token en .token"
  echo "======================================================"
fi
echo ""
