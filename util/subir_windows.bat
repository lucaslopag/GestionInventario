@echo off
REM ============================================================
REM  SUBIR CAMBIOS A GITHUB — Windows
REM  Uso: doble clic o desde CMD: subir_windows.bat
REM ============================================================

cd /d "%~dp0.."

set USUARIO=lucaslopag
set REPO=GestionInventario
set BRANCH=main

if not exist ".token" (
    echo [ERROR] No se encontro el fichero .token en la raiz del proyecto.
    echo         Crea el fichero .token con tu token de GitHub.
    pause & exit /b 1
)

REM Leer token y limpiar espacios/saltos de linea
set /p TOKEN=<.token
set "TOKEN=%TOKEN: =%"

set REPO_URL=https://%USUARIO%:%TOKEN%@github.com/%USUARIO%/%REPO%.git

echo.
echo ======================================================
echo    SUBIR CAMBIOS -^> GitHub/%REPO%
echo ======================================================
echo.
echo Archivos modificados/nuevos:
git status --short
echo.

set /p CONFIRM="^¿Deseas subir estos cambios? (y/n): "
echo.
if /i not "%CONFIRM%"=="y" (
    echo X Operacion cancelada.
    pause & exit /b 0
)

set /p COMMIT_MSG="Mensaje del commit (Enter = 'update'): "
if "%COMMIT_MSG%"=="" set COMMIT_MSG=update

echo.
git add .
git commit -m "%COMMIT_MSG%"

REM Actualizar remote con el token (solo en memoria, no queda en historial)
git remote set-url origin %REPO_URL% 2>nul || git remote add origin %REPO_URL%

REM --- PULL AUTOMATICO antes de subir para evitar conflictos ---
echo [i] Sincronizando con el servidor antes de subir...
set GIT_MERGE_AUTOEDIT=no
git pull --no-edit origin %BRANCH% 2>&1
if %errorlevel% neq 0 (
    echo.
    echo ======================================================
    echo    ERROR al hacer pull. Hay conflictos que resolver
    echo    manualmente antes de poder subir.
    echo ======================================================
    echo.
    pause & exit /b 1
)

echo.
echo [-^>] Subiendo...
git push origin %BRANCH% 2>&1

if %errorlevel% equ 0 (
    echo.
    echo ======================================================
    echo    OK - Cambios subidos correctamente!
    echo    https://github.com/%USUARIO%/%REPO%
    echo ======================================================
) else (
    echo.
    echo ======================================================
    echo    ERROR al subir. Comprueba el token en .token
    echo ======================================================
)

REM Quitar el token de la URL del remote por seguridad
git remote set-url origin https://github.com/%USUARIO%/%REPO%.git 2>nul

echo.
pause
