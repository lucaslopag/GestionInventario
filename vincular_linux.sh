#!/bin/bash
# ============================================================
#  SCRIPT DE VINCULACIÓN GIT — Linux
#  Ejecutar dentro de la carpeta del proyecto
#  Uso: bash vincular_linux.sh
# ============================================================

USUARIO="lucaslopag"
REPO="GestionInventario"
BRANCH="main"

# Leer token desde .token (nunca se sube al repo)
if [ ! -f ".token" ]; then
  echo "❌ No se encontró el fichero .token en esta carpeta."
  echo "   Créalo con el contenido: ghp_TU_TOKEN_AQUI"
  exit 1
fi
TOKEN=$(cat .token | tr -d '[:space:]')
REPO_URL="https://${USUARIO}:${TOKEN}@github.com/${USUARIO}/${REPO}.git"

echo ""
echo "======================================================"
echo "   VINCULANDO PROYECTO A GITHUB — ${REPO}"
echo "======================================================"
echo ""

# Configurar identidad si no está
if [ -z "$(git config --global user.email)" ]; then
  read -p "Tu nombre: " GIT_NAME
  read -p "Tu email de GitHub: " GIT_EMAIL
  git config --global user.name "$GIT_NAME"
  git config --global user.email "$GIT_EMAIL"
  echo "[✓] Identidad configurada."
else
  echo "[✓] Identidad: $(git config --global user.name) <$(git config --global user.email)>"
fi
echo ""

# Inicializar git si no existe
if [ ! -d ".git" ]; then
  echo "[→] Inicializando repositorio git local..."
  git init
fi
echo ""

# Crear .gitignore
cat > .gitignore << 'EOF'
# Credenciales locales — NUNCA subir
.token
.env

# Java / Spring Boot
target/
*.class
*.jar
*.war
*.log
.mvn/wrapper/maven-wrapper.jar

# IDE
.idea/
*.iml
.vscode/
*.code-workspace

# OS
.DS_Store
Thumbs.db
EOF
echo "[✓] .gitignore actualizado."
echo ""

# Configurar remote
git remote set-url origin "$REPO_URL" 2>/dev/null || git remote add origin "$REPO_URL"
echo "[✓] Remote configurado."
echo ""

# Añadir, commitear y subir
git add .
git commit -m "chore: commit inicial — TFG Gestión de Inventario" 2>/dev/null || echo "[i] Nada nuevo que commitear."
git branch -M "$BRANCH"

echo "[→] Subiendo a GitHub..."
if git push -u origin "$BRANCH" --force 2>&1; then
  echo ""
  echo "======================================================"
  echo "   ✅ ¡Éxito! Proyecto subido a GitHub."
  echo "   🔗 https://github.com/${USUARIO}/${REPO}"
  echo "======================================================"
else
  echo ""
  echo "======================================================"
  echo "   ❌ Error al hacer push. Comprueba el token en .token"
  echo "======================================================"
fi
echo ""
