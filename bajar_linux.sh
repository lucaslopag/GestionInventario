#!/bin/bash
# ============================================================
#  BAJAR CAMBIOS DE GITHUB — Linux
#  Uso: bash bajar_linux.sh
# ============================================================

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
echo "   ⬇  BAJAR CAMBIOS ← GitHub/${REPO}"
echo "======================================================"
echo ""
echo "[i] Rama actual: $(git branch --show-current 2>/dev/null || echo 'desconocida')"
echo ""

read -p "¿Deseas bajar los cambios del repo? (y/n): " CONFIRM
echo ""
if [[ "$CONFIRM" != "y" && "$CONFIRM" != "Y" ]]; then
  echo "❌ Operación cancelada."; echo ""; exit 0
fi

git remote set-url origin "$REPO_URL" 2>/dev/null || git remote add origin "$REPO_URL"

echo "[→] Descargando cambios..."
if git pull origin "$BRANCH" 2>&1; then
  echo ""
  echo "======================================================"
  echo "   ✅ ¡Cambios descargados correctamente!"
  echo "======================================================"
else
  echo ""
  echo "======================================================"
  echo "   ❌ Error. Puede haber conflictos locales."
  echo "   Tip: sube primero tus cambios con subir_linux.sh"
  echo "======================================================"
fi
echo ""
