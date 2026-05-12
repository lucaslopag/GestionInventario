@echo off
setlocal enabledelayedexpansion

:: ======================================================================
:: SCRIPT PARA ARRANCAR TODOS LOS MICROSERVICIOS (DESARROLLO LOCAL)
:: ======================================================================

title Lanzador de Microservicios - TFG Gestion Inventario

echo.
echo  [!] Iniciando despliegue local de microservicios...
echo.

:: --- 0. INFRAESTRUCTURA (MySQL y RabbitMQ locales) ---
echo  [0/8] Intentando iniciar MySQL y RabbitMQ...
:: Intentar arrancar como servicio (puede requerir admin)
net start MySQL 2>nul
net start RabbitMQ 2>nul
:: Si no son servicios, al menos damos el aviso
echo  [!] Asegurate de que MySQL (puerto 3306) y RabbitMQ (puerto 5672) esten corriendo.
echo.

:: --- 1. EUREKA SERVER (Imprescindible para el registro de servicios) ---
echo  [1/8] Lanzando EUREKA-SERVER en puerto 8761...
start "EUREKA-SERVER" cmd /k "cd ..\backend\eureka-server && mvnw spring-boot:run"

echo  [?] Esperando 15 segundos a que Eureka este operativo...
timeout /t 15 /nobreak > nul

:: --- 2. MICROSERVICIOS DE DOMINIO Y SOPORTE ---
echo  [2/8] Lanzando MS-USERS...
start "MS-USERS" cmd /k "cd ..\backend\ms-users && mvnw spring-boot:run"

echo  [3/8] Lanzando MS-CATALOG...
start "MS-CATALOG" cmd /k "cd ..\backend\ms-catalog && mvnw spring-boot:run"

echo  [4/8] Lanzando MS-SUPPLIERS...
start "MS-SUPPLIERS" cmd /k "cd ..\backend\ms-suppliers && mvnw spring-boot:run"

echo  [5/8] Lanzando MS-INVENTORY...
start "MS-INVENTORY" cmd /k "cd ..\backend\ms-inventory && mvnw spring-boot:run"

echo  [6/8] Lanzando MS-AUDIT...
start "MS-AUDIT" cmd /k "cd ..\backend\ms-audit && mvnw spring-boot:run"

echo  [7/8] Lanzando MS-MAIL...
start "MS-MAIL" cmd /k "cd ..\backend\ms-mail && mvnw spring-boot:run"

:: --- 3. API GATEWAY (Se recomienda lanzar al final) ---
echo  [8/8] Lanzando API-GATEWAY en puerto 8080...
start "API-GATEWAY" cmd /k "cd ..\backend\api-gateway && mvnw spring-boot:run"

echo.
echo  ======================================================================
echo   TODOS LOS SERVICIOS HAN SIDO LANZADOS EN VENTANAS INDEPENDIENTES.
echo  ======================================================================
echo.
echo  NOTAS:
echo  1. MySQL y RabbitMQ deben estar instalados localmente (ya no se usa Docker).
echo  2. Revisa las consolas individuales para ver logs de error.
echo.
pause
