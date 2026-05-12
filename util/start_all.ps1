# ======================================================================
# SCRIPT PARA ARRANCAR TODOS LOS MICROSERVICIOS (POWERSHELL - ORDENADO)
# ======================================================================

$backendRoot = Join-Path $PSScriptRoot "..\backend"
$frontendRoot = Join-Path $PSScriptRoot "..\frontend-react"

Write-Host "`n [!] Iniciando despliegue local (Orden de Integracion)...`n" -ForegroundColor Cyan

# 1. Eureka
Write-Host " [1/8] Lanzando EUREKA-SERVER..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$backendRoot\eureka-server'; ./mvnw spring-boot:run"
Start-Sleep -Seconds 15

# 2 & 3. Infra
Write-Host " [2/8] Iniciando RABBITMQ..." -ForegroundColor Gray
Start-Service -Name "RabbitMQ" -ErrorAction SilentlyContinue
Write-Host " [3/8] Iniciando MYSQL..." -ForegroundColor Gray
Start-Service -Name "MySQL" -ErrorAction SilentlyContinue

# 4. Users
Write-Host " [4/8] Lanzando MS-USERS..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$backendRoot\ms-users'; ./mvnw spring-boot:run"
Start-Sleep -Seconds 5

# 5. Gateway
Write-Host " [5/8] Lanzando API-GATEWAY..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$backendRoot\api-gateway'; ./mvnw spring-boot:run"
Start-Sleep -Seconds 5

# 6. Dominio
Write-Host " [6/8] Lanzando MS-CATALOG, MS-SUPPLIERS y MS-INVENTORY..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$backendRoot\ms-catalog'; ./mvnw spring-boot:run"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$backendRoot\ms-suppliers'; ./mvnw spring-boot:run"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$backendRoot\ms-inventory'; ./mvnw spring-boot:run"
Start-Sleep -Seconds 5

# 7. Soporte
Write-Host " [7/8] Lanzando MS-AUDIT y MS-MAIL..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$backendRoot\ms-audit'; ./mvnw spring-boot:run"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$backendRoot\ms-mail'; ./mvnw spring-boot:run"

# 8. Frontend
Write-Host " [8/8] Lanzando FRONTEND-REACT..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$frontendRoot'; npm run dev"

Write-Host "`n ======================================================================" -ForegroundColor Green
Write-Host "  SISTEMA LANZADO COMPLETAMENTE." -ForegroundColor Green
Write-Host " ======================================================================`n" -ForegroundColor Green
