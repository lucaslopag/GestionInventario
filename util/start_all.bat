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

:: Liberar puerto 5500 (Frontend, por si usan Live Server)
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :5500') do (
    if not "%%a"=="" (
        echo  - Liberando puerto 5500 ^(PID %%a^)...
        taskkill /F /PID %%a >nul 2>&1
    )
)

:: --- 2. INFRAESTRUCTURA LOCAL (antes de Eureka para dar tiempo a arrancar) ---
echo  [2/8] Iniciando RABBITMQ...
net start RabbitMQ 2>nul
echo  [3/8] Iniciando MYSQL...
net start MySQL95 2>nul
echo.

:: --- 3. EUREKA SERVER ---
echo  [1/8] Lanzando EUREKA-SERVER en puerto 8761...
start "EUREKA-SERVER" cmd /k "cd /d ""%~dp0..\backend\eureka-server"" && mvnw spring-boot:run"

echo  [?] Esperando 15 segundos a que Eureka y la BD esten operativos...
timeout /t 15 /nobreak > nul

:: --- 4. MS-USERS (Autenticacion) ---
echo  [4/8] Lanzando MS-USERS...
start "MS-USERS" cmd /k "cd /d ""%~dp0..\backend\ms-users"" && mvnw spring-boot:run"
timeout /t 5 /nobreak > nul

:: --- 5. API-GATEWAY (Puerta de enlace) ---
echo  [5/8] Lanzando API-GATEWAY (Puerto 8080)...
start "API-GATEWAY" cmd /k "cd /d ""%~dp0..\backend\api-gateway"" && mvnw spring-boot:run"
timeout /t 5 /nobreak > nul

:: --- 6. MICROSERVICIOS DE DOMINIO ---
echo  [6/8] Lanzando MS-CATALOG, MS-SUPPLIERS y MS-INVENTORY...
start "MS-CATALOG" cmd /k "cd /d ""%~dp0..\backend\ms-catalog"" && mvnw spring-boot:run"
start "MS-SUPPLIERS" cmd /k "cd /d ""%~dp0..\backend\ms-suppliers"" && mvnw spring-boot:run"
start "MS-INVENTORY" cmd /k "cd /d ""%~dp0..\backend\ms-inventory"" && mvnw spring-boot:run"
timeout /t 5 /nobreak > nul

:: --- 7. MICROSERVICIOS DE SOPORTE ---
echo  [7/8] Lanzando MS-AUDIT y MS-MAIL...
start "MS-AUDIT" cmd /k "cd /d ""%~dp0..\backend\ms-audit"" && mvnw spring-boot:run"
start "MS-MAIL" cmd /k "cd /d ""%~dp0..\backend\ms-mail"" && mvnw spring-boot:run"

:: --- 8. FRONTEND (Servidor web local) ---
echo  [8/8] Lanzando Servidor Frontend en puerto 5500...
start "FRONTEND" cmd /k "cd /d ""%~dp0..\frontend"" && python -m http.server 5500"

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
