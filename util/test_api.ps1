# ==============================================================================
# SCRIPT DE TESTING AUTOMATIZADO - TFG GESTIÓN DE INVENTARIO
# ==============================================================================
# Este script prueba todos los endpoints definidos en el FDD.
# Los fallos se registran en: util/test/errores/
# ==============================================================================

$BaseUrl = "http://localhost:8080/api"
$ErrorDir = Join-Path $PSScriptRoot "test/errores"
$JwtToken = ""

# Puertos definidos en el FDD (Sección 14.1)
$ServicePorts = @{
    "MySQL"         = 3306
    "RabbitMQ"      = 5672
    "Eureka Server" = 8761
    "API Gateway"   = 8080
    "MS-Users"      = 8081
    "MS-Catalog"    = 8082
    "MS-Suppliers"  = 8083
    "MS-Inventory"  = 8084
    "MS-Audit"      = 8085
    "MS-Mail"       = 8086
}

# Crear directorio de errores si no existe
if (-not (Test-Path $ErrorDir)) {
    New-Item -ItemType Directory -Path $ErrorDir -Force | Out-Null
}

# --- Funciones Auxiliares ---

function Test-ServicePort {
    param ([string]$Name, [int]$Port)
    try {
        $Connection = New-Object System.Net.Sockets.TcpClient
        $Wait = $Connection.BeginConnect("localhost", $Port, $null, $null)
        if ($Wait.AsyncWaitHandle.WaitOne(500, $false)) {
            $Connection.EndConnect($Wait)
            $Connection.Close()
            return $true
        }
    } catch {}
    return $false
}

function Log-Error {
    param (
        [string]$Name,
        [string]$Url,
        [string]$Method,
        [int]$StatusCode,
        [string]$ResponseBody
    )
    $Timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
    $FileName = "$($Name)_$($Timestamp).log"
    $FilePath = Join-Path $ErrorDir $FileName
    $Content = @"
Name: $Name
Timestamp: $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")
URL: $Url
Method: $Method
Status Code: $StatusCode

Response Body:
$ResponseBody
"@
    Set-Content -Path $FilePath -Value $Content
    Write-Host "[FAIL] Error guardado en: $FileName" -ForegroundColor Red
}

function Invoke-ApiRequest {
    param (
        [string]$Path,
        [string]$Method = "GET",
        $Body = $null,
        [string]$TestName = "Request"
    )

    $Url = "$BaseUrl$Path"
    $Headers = @{
        "Content-Type" = "application/json"
        "Accept"       = "application/json"
    }
    if ($JwtToken) {
        $Headers.Add("Authorization", "Bearer $JwtToken")
    }

    $Params = @{
        Uri     = $Url
        Method  = $Method
        Headers = $Headers
    }
    if ($Body) {
        $Params.Body = $Body | ConvertTo-Json
    }

    try {
        $Response = Invoke-RestMethod @Params -ResponseHeadersVariable ResHeaders -ErrorAction Stop
        Write-Host "[OK] $Method $Path - Success" -ForegroundColor Green
        return $Response
    }
    catch {
        $StatusCode = 0
        $ResponseBody = ""
        if ($_.Exception.Response) {
            $StatusCode = [int]$_.Exception.Response.StatusCode
            $Reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
            $ResponseBody = $Reader.ReadToEnd()
        }
        else {
            $ResponseBody = $_.Exception.Message
        }
        
        # Ignorar 409 en registro (usuario ya existe) o login (si ya estamos logueados o algo)
        if ($TestName -eq "Register" -and $StatusCode -eq 409) {
            Write-Host "[SKIP] $Method $Path - Usuario ya existe (409)" -ForegroundColor Yellow
            return $null
        }

        Log-Error -Name $TestName -Url $Url -Method $Method -StatusCode $StatusCode -ResponseBody $ResponseBody
        return $null
    }
}

# --- INICIO DE PRUEBAS ---

Write-Host "`n=== COMPROBANDO SERVICIOS (Local Environment) ===" -ForegroundColor Cyan
$AllOk = $true
foreach ($Svc in $ServicePorts.GetEnumerator() | Sort-Object Name) {
    if (Test-ServicePort -Name $Svc.Key -Port $Svc.Value) {
        Write-Host "[UP]   $($Svc.Key.PadRight(15)) (Puerto $($Svc.Value))" -ForegroundColor Green
    } else {
        Write-Host "[DOWN] $($Svc.Key.PadRight(15)) (Puerto $($Svc.Value))" -ForegroundColor Red
        $AllOk = $false
    }
}

if (-not $AllOk) {
    Write-Host "`n[!] ADVERTENCIA: Algunos servicios parecen estar caídos." -ForegroundColor Yellow
    Write-Host "Asegúrate de haber iniciado MySQL, RabbitMQ y todos los microservicios." -ForegroundColor Gray
    # No detenemos el script, dejamos que intente las peticiones por si acaso (ej. si corren en IP distinta)
}

Write-Host "`n=== INICIANDO PRUEBAS DE API (vía Gateway) ===`n" -ForegroundColor Cyan

# 1. AUTENTICACIÓN
Write-Host "--- Sección 1: Autenticación ---" -ForegroundColor Gray
$UserEmail = "test_admin@example.com"
$UserPass = "Admin123!"

# Registro (Puede fallar si ya existe, se ignora 409)
Invoke-ApiRequest -Path "/auth/register" -Method "POST" -Body @{
    nombre = "Admin Test"
    email = $UserEmail
    password = $UserPass
} -TestName "Register"

# Login
$LoginRes = Invoke-ApiRequest -Path "/auth/login" -Method "POST" -Body @{
    email = $UserEmail
    password = $UserPass
} -TestName "Login"

if ($LoginRes -and $LoginRes.accessToken) {
    $JwtToken = $LoginRes.accessToken
    Write-Host "[INFO] Token JWT obtenido." -ForegroundColor Cyan
} else {
    Write-Host "[ERROR] No se pudo obtener el token. ¿Está el usuario confirmado?" -ForegroundColor Red
    Write-Host "[TIP] Si acabas de registrar al usuario, recuerda marcar 'confirmado=1' en la DB 'db_users.usuarios'." -ForegroundColor Yellow
    # Intentamos continuar con lo que haya, pero la mayoría de tests fallarán 401
}

# 2. PRODUCTOS (MS-Catalog)
Write-Host "`n--- Sección 2: Productos ---" -ForegroundColor Gray
$NewProduct = @{
    nombre = "Producto de Prueba $(Get-Random)"
    sku = "SKU-$(Get-Random)"
    descripcion = "Descripción del producto de prueba"
    precio_neto = 25.50
}

$CreatedProduct = Invoke-ApiRequest -Path "/productos" -Method "POST" -Body $NewProduct -TestName "CreateProduct"
$ProductId = if ($CreatedProduct) { $CreatedProduct.id } else { 1 } # Fallback a ID 1 si falla

Invoke-ApiRequest -Path "/productos" -TestName "ListProducts" | Out-Null
Invoke-ApiRequest -Path "/productos/$ProductId" -TestName "GetProduct" | Out-Null

# 3. PROVEEDORES (MS-Suppliers)
Write-Host "`n--- Sección 3: Proveedores ---" -ForegroundColor Gray
$NewSupplier = @{
    nombre = "Proveedor Test $(Get-Random)"
    email = "prov_$(Get-Random)@test.com"
    telefono = "600000000"
    direccion = "Calle Falsa 123"
}
$CreatedSupplier = Invoke-ApiRequest -Path "/proveedores" -Method "POST" -Body $NewSupplier -TestName "CreateSupplier"
$SupplierId = if ($CreatedSupplier) { $CreatedSupplier.id } else { 1 }

Invoke-ApiRequest -Path "/proveedores" -TestName "ListSuppliers" | Out-Null

# 4. INVENTARIO (MS-Inventory)
Write-Host "`n--- Sección 4: Inventario ---" -ForegroundColor Gray
# Entrada de stock
Invoke-ApiRequest -Path "/inventario/entrada" -Method "POST" -Body @{
    producto_id = $ProductId
    proveedor_id = $SupplierId
    cantidad = 100
} -TestName "StockEntry" | Out-Null

# Consultar stock
Invoke-ApiRequest -Path "/inventario/stock/$ProductId" -TestName "GetStock" | Out-Null

# Salida de stock
Invoke-ApiRequest -Path "/inventario/salida" -Method "POST" -Body @{
    producto_id = $ProductId
    cantidad = 10
} -TestName "StockExit" | Out-Null

# Listar movimientos
Invoke-ApiRequest -Path "/inventario/movimientos" -TestName "ListMovements" | Out-Null

# 5. AUDITORÍA (MS-Audit)
Write-Host "`n--- Sección 5: Auditoría ---" -ForegroundColor Gray
Invoke-ApiRequest -Path "/auditoria" -TestName "ListAuditLogs" | Out-Null
Invoke-ApiRequest -Path "/auditoria/producto/$ProductId" -TestName "ProductAudit" | Out-Null

Write-Host "`n=== PRUEBAS FINALIZADAS ===`n" -ForegroundColor Cyan
Write-Host "Revisa los errores en: $ErrorDir" -ForegroundColor Gray
