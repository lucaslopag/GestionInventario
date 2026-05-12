# ======================================================================
# SCRIPT PARA ARRANCAR TODOS LOS MICROSERVICIOS (POWERSHELL)
# ======================================================================

$services = @(
    @{ name = "EUREKA-SERVER"; path = "eureka-server"; wait = 15 },
    @{ name = "MS-USERS";      path = "ms-users";      wait = 2 },
    @{ name = "MS-CATALOG";    path = "ms-catalog";    wait = 2 },
    @{ name = "MS-SUPPLIERS";  path = "ms-suppliers";  wait = 2 },
    @{ name = "MS-INVENTORY";  path = "ms-inventory";  wait = 2 },
    @{ name = "MS-AUDIT";      path = "ms-audit";      wait = 2 },
    @{ name = "MS-MAIL";       path = "ms-mail";       wait = 2 },
    @{ name = "API-GATEWAY";   path = "api-gateway";   wait = 0 }
)

$backendRoot = Join-Path $PSScriptRoot "..\backend"

Write-Host "`n [!] Iniciando despliegue local de microservicios...`n" -ForegroundColor Cyan

# --- 0. INFRAESTRUCTURA (MySQL y RabbitMQ locales) ---
Write-Host " [0] Intentando iniciar servicios de infraestructura..." -ForegroundColor Gray
try {
    Start-Service -Name "MySQL" -ErrorAction SilentlyContinue
    Start-Service -Name "RabbitMQ" -ErrorAction SilentlyContinue
} catch {
    Write-Host " [!] No se pudieron iniciar los servicios automáticamente. Asegúrate de que MySQL y RabbitMQ estén corriendo." -ForegroundColor DarkYellow
}
Write-Host ""

foreach ($service in $services) {
    Write-Host " [+] Lanzando $($service.name)..." -ForegroundColor Yellow
    
    $targetDir = Join-Path $backendRoot $service.path
    
    # Abrir en una nueva ventana de PowerShell
    Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$targetDir'; Write-Host 'Iniciando $($service.name)...' -ForegroundColor Cyan; ./mvnw spring-boot:run"
    
    if ($service.wait -gt 0) {
        Write-Host " [?] Esperando $($service.wait) segundos..." -ForegroundColor Gray
        Start-Sleep -Seconds $service.wait
    }
}

Write-Host "`n ======================================================================" -ForegroundColor Green
Write-Host "  TODOS LOS SERVICIOS HAN SIDO LANZADOS." -ForegroundColor Green
Write-Host " ======================================================================`n" -ForegroundColor Green
Write-Host " NOTAS:"
Write-Host " 1. MySQL y RabbitMQ deben estar instalados localmente (sin Docker)."
Write-Host " 2. Revisa las consolas individuales para ver logs de error.`n"
