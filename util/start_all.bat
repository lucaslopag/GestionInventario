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
start "EUREKA-SERVER" cmd /k "cd ..\backend\eureka-server && mvnw spring-boot:run"

echo  [?] Esperando 15 segundos a que Eureka este operativo...
timeout /t 15 /nobreak > nul

:: --- 3. INFRAESTRUCTURA LOCAL ---
echo  [2/8] Iniciando RABBITMQ...
net start RabbitMQ 2>nul
echo  [3/8] Iniciando MYSQL...
net start MySQL 2>nul
echo.

:: --- 4. MS-USERS (Autenticacion) ---
echo  [4/8] Lanzando MS-USERS...
start "MS-USERS" cmd /k "cd ..\backend\ms-users && mvnw spring-boot:run"
timeout /t 5 /nobreak > nul

:: --- 5. API-GATEWAY (Puerta de enlace) ---
echo  [5/8] Lanzando API-GATEWAY (Puerto 8080)...
start "API-GATEWAY" cmd /k "cd ..\backend\api-gateway && mvnw spring-boot:run"
timeout /t 5 /nobreak > nul

:: --- 6. MICROSERVICIOS DE DOMINIO ---
echo  [6/8] Lanzando MS-CATALOG, MS-SUPPLIERS y MS-INVENTORY...
start "MS-CATALOG" cmd /k "cd ..\backend\ms-catalog && mvnw spring-boot:run"
start "MS-SUPPLIERS" cmd /k "cd ..\backend\ms-suppliers && mvnw spring-boot:run"
start "MS-INVENTORY" cmd /k "cd ..\backend\ms-inventory && mvnw spring-boot:run"
timeout /t 5 /nobreak > nul

:: --- 7. MICROSERVICIOS DE SOPORTE ---
echo  [7/8] Lanzando MS-AUDIT y MS-MAIL...
start "MS-AUDIT" cmd /k "cd ..\backend\ms-audit && mvnw spring-boot:run"
start "MS-MAIL" cmd /k "cd ..\backend\ms-mail && mvnw spring-boot:run"

echo.
echo  ======================================================================
echo   SISTEMA LANZADO COMPLETAMENTE SEGUN EL PLAN DE INTEGRACION.
echo  ======================================================================
echo.
echo  NOTAS:
echo  1. Revisa las nuevas ventanas para ver los logs.
echo  2. Asegurate de que MySQL y RabbitMQ esten activos.
echo.

pause
