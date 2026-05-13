param (
    [string]$Email = "skiperxdlol@gmail.com",
    [string]$Nombre = "Test User"
)

$Url = "http://localhost:8080/api/auth/register"
$Body = @{
    nombre = $Nombre
    email = $Email
    password = "TestPassword123!"
} | ConvertTo-Json

Write-Host "Enviando solicitud de registro para: $Email..." -ForegroundColor Cyan

try {
    $Response = Invoke-RestMethod -Uri $Url -Method Post -Body $Body -ContentType "application/json"
    Write-Host "`n[OK] Respuesta del servidor: $Response" -ForegroundColor Green
    Write-Host "`nAhora revisa la ventana de MS-MAIL para confirmar el envio." -ForegroundColor Yellow
} catch {
    Write-Host "`n[ERROR] No se pudo conectar con el servidor. ¿Has ejecutado start_all.bat?" -ForegroundColor Red
    Write-Host $_.Exception.Message
}
