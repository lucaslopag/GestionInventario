@echo off
REM ============================================================
REM  BAJAR CAMBIOS DE GITHUB — Windows
REM  Uso: doble clic o desde CMD: bajar_windows.bat
REM ============================================================

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

git remote set-url origin %REPO_URL% 2>nul || git remote add origin %REPO_URL%

echo [->] Descargando cambios...
git pull origin %BRANCH%

if %errorlevel% equ 0 (
    echo.
    echo ======================================================
    echo    OK - Cambios descargados correctamente!
    echo ======================================================
) else (
    echo.
    echo ======================================================
    echo    ERROR - Puede haber conflictos locales.
    echo    Tip: sube primero con subir_windows.bat
    echo ======================================================
)
echo.
pause
