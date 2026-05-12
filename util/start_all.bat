@echo off
setlocal enabledelayedexpansion

:: ======================================================================
:: SCRIPT PARA ARRANCAR TODOS LOS MICROSERVICIOS (ORDEN ESPECÍFICO)
:: ======================================================================

title Lanzador de Microservicios - TFG Gestion Inventario

echo.
echo  [!] Iniciando despliegue local siguiendo el orden de integracion...
echo.

:: --- 1. EUREKA SERVER ---
echo  [1/8] Lanzando EUREKA-SERVER (Puerto 8761)...
start "EUREKA-SERVER" cmd /k "cd ..\backend\eureka-server && mvnw spring-boot:run"
echo  [?] Esperando a que Eureka inicialice...
timeout /t 15 /nobreak > nul

:: --- 2. INFRAESTRUCTURA LOCAL ---
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

:: --- 6. MICROSERVICIOS DE DOMINIO (Lucas) ---
echo  [6/8] Lanzando MS-CATALOG, MS-SUPPLIERS y MS-INVENTORY...
start "MS-CATALOG" cmd /k "cd ..\backend\ms-catalog && mvnw spring-boot:run"
start "MS-SUPPLIERS" cmd /k "cd ..\backend\ms-suppliers && mvnw spring-boot:run"
start "MS-INVENTORY" cmd /k "cd ..\backend\ms-inventory && mvnw spring-boot:run"
timeout /t 5 /nobreak > nul

:: --- 7. MICROSERVICIOS DE SOPORTE (Angel) ---
echo  [7/8] Lanzando MS-AUDIT y MS-MAIL...
start "MS-AUDIT" cmd /k "cd ..\backend\ms-audit && mvnw spring-boot:run"
start "MS-MAIL" cmd /k "cd ..\backend\ms-mail && mvnw spring-boot:run"


echo.
echo  ======================================================================
echo   SISTEMA LANZADO COMPLETAMENTE SEGUN EL PLAN DE INTEGRACION.
echo  ======================================================================
echo.
pause
