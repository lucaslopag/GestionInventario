@echo off
title Lanzador de Microservicios - TFG Gestión de Inventario
setlocal enabledelayedexpansion

echo.
echo  ======================================================================
echo   PREPARANDO EL ENTORNO DE MICROSERVICIOS
echo  ======================================================================
echo.

:: --- 1. LIMPIEZA DE PUERTOS (Evita errores de "Port already in use") ---
echo  [0/8] Limpiando puertos bloqueados...

:: Liberar puerto 8761 (Eureka)
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8761') do (
    if not "%%a"=="" (
        echo  - Liberando puerto 8761 ^(PID %%a^)...
        taskkill /F /PID %%a >nul 2>&1
    )
)

:: Liberar puerto 8080 (Gateway)
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080') do (
    if not "%%a"=="" (
        echo  - Liberando puerto 8080 ^(PID %%a^)...
        taskkill /F /PID %%a >nul 2>&1
    )
)

:: --- 2. EUREKA SERVER ---
echo  [1/8] Lanzando EUREKA-SERVER en puerto 8761...
start "EUREKA-SERVER" cmd /k "cd ..\backend\eureka-server && mvn spring-boot:run"

echo  [?] Esperando 15 segundos a que Eureka este operativo...
timeout /t 15 /nobreak > nul

:: --- 3. MICROSERVICIOS ---
echo  [2/8] Lanzando MS-USERS...
start "MS-USERS" cmd /k "cd ..\backend\ms-users && mvn spring-boot:run"

echo  [3/8] Lanzando MS-CATALOG...
start "MS-CATALOG" cmd /k "cd ..\backend\ms-catalog && mvn spring-boot:run"

echo  [4/8] Lanzando MS-SUPPLIERS...
start "MS-SUPPLIERS" cmd /k "cd ..\backend\ms-suppliers && mvn spring-boot:run"

echo  [5/8] Lanzando MS-INVENTORY...
start "MS-INVENTORY" cmd /k "cd ..\backend\ms-inventory && mvn spring-boot:run"

echo  [6/8] Lanzando MS-AUDIT...
start "MS-AUDIT" cmd /k "cd ..\backend\ms-audit && mvn spring-boot:run"

echo  [7/8] Lanzando MS-MAIL...
start "MS-MAIL" cmd /k "cd ..\backend\ms-mail && mvn spring-boot:run"

:: --- 4. API GATEWAY ---
echo  [8/8] Lanzando API-GATEWAY en puerto 8080...
start "API-GATEWAY" cmd /k "cd ..\backend\api-gateway && mvn spring-boot:run"

echo.
echo  ======================================================================
echo   TODOS LOS SERVICIOS HAN SIDO LANZADOS.
echo  ======================================================================
echo.
echo  NOTAS:
echo  1. Revisa las nuevas ventanas para ver los logs.
echo  2. Asegurate de que MySQL y RabbitMQ esten activos.
echo.
pause
