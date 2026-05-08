@echo off
REM ============================================================
REM  SUBIR CAMBIOS A GITHUB — Windows
REM  Uso: doble clic o desde CMD: subir_windows.bat
REM ============================================================

REM Cambiar al directorio raiz del proyecto
cd /d "%~dp0.."

set USUARIO=lucaslopag
set REPO=GestionInventario
set BRANCH=main

if not exist ".token" (
    echo [ERROR] No se encontro .token en esta carpeta.
    pause & exit /b 1
)
set /p TOKEN=<.token
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
git remote set-url origin %REPO_URL% 2>nul || git remote add origin %REPO_URL%

echo [->] Subiendo...
git push origin %BRANCH%

if %errorlevel% equ 0 (
    echo.
    echo ======================================================
    echo    OK - Cambios subidos correctamente!
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
