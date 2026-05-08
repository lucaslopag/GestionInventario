@echo off
REM ============================================================
REM  SCRIPT DE VINCULACIÓN GIT — Windows
REM  Uso: doble clic o desde CMD: vincular_windows.bat
REM ============================================================

set USUARIO=lucaslopag
set REPO=GestionInventario
set BRANCH=main

REM Leer token desde .token (nunca se sube al repo)
if not exist ".token" (
    echo [ERROR] No se encontro el fichero .token en esta carpeta.
    echo         Crealo con el contenido: ghp_TU_TOKEN_AQUI
    pause
    exit /b 1
)
set /p TOKEN=<.token

set REPO_URL=https://%USUARIO%:%TOKEN%@github.com/%USUARIO%/%REPO%.git

echo.
echo ======================================================
echo    VINCULANDO PROYECTO A GITHUB -- %REPO%
echo ======================================================
echo.

where git >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Git no instalado. Descargalo en: https://git-scm.com
    pause & exit /b 1
)

if not exist ".git" (
    echo [->] Inicializando git...
    git init
)
echo.

REM Crear .gitignore limpio
(
echo # Credenciales locales -- NUNCA subir
echo .token
echo .env
echo.
echo # Java / Spring Boot
echo target/
echo *.class
echo *.jar
echo *.war
echo *.log
echo.
echo # IDE
echo .idea/
echo *.iml
echo .vscode/
echo.
echo # OS
echo .DS_Store
echo Thumbs.db
) > .gitignore
echo [OK] .gitignore actualizado.

git remote set-url origin %REPO_URL% 2>nul || git remote add origin %REPO_URL%
echo [OK] Remote configurado.
echo.

git add .
git commit -m "chore: commit inicial -- TFG Gestion de Inventario"
git branch -M %BRANCH%

echo [->] Subiendo a GitHub...
git push -u origin %BRANCH% --force

if %errorlevel% equ 0 (
    echo.
    echo ======================================================
    echo    OK - Proyecto subido a GitHub correctamente.
    echo    https://github.com/%USUARIO%/%REPO%
    echo ======================================================
) else (
    echo.
    echo ======================================================
    echo    ERROR - Comprueba el token en el fichero .token
    echo ======================================================
)
echo.
pause
