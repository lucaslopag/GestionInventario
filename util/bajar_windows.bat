@echo off
REM ============================================================
REM  BAJAR CAMBIOS DE GITHUB — Windows
REM  Uso: doble clic o desde CMD: bajar_windows.bat
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
echo    BAJAR CAMBIOS ^<- GitHub/%REPO%
echo ======================================================
echo.

for /f "tokens=*" %%i in ('git branch --show-current 2^>nul') do set CURRENT_BRANCH=%%i
echo [i] Rama actual: %CURRENT_BRANCH%
echo.

set /p CONFIRM="^¿Deseas bajar los cambios? (y/n): "
echo.
if /i not "%CONFIRM%"=="y" (
    echo X Operacion cancelada.
    pause & exit /b 0
)

REM Actualizar remote con el token
git remote set-url origin %REPO_URL% 2>nul || git remote add origin %REPO_URL%

REM Pull sin abrir editor (--no-edit evita que se abra vim)
echo [-^>] Descargando cambios...
set GIT_MERGE_AUTOEDIT=no
git pull --no-edit origin %BRANCH% 2>&1

if %errorlevel% equ 0 (
    echo.
    echo ======================================================
    echo    OK - Cambios descargados correctamente!
    echo ======================================================
) else (
    echo.
    echo ======================================================
    echo    ERROR - Hay conflictos locales sin resolver.
    echo    Sube primero tus cambios con subir_windows.bat
    echo    o resuelve los conflictos manualmente.
    echo ======================================================
)

REM Quitar el token de la URL del remote por seguridad
git remote set-url origin https://github.com/%USUARIO%/%REPO%.git 2>nul

echo.
pause
