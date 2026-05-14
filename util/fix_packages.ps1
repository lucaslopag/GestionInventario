# Script para estandarizar paquetes a minúsculas
$services = Get-ChildItem -Path backend -Directory | Where-Object { $_.Name -like "ms-*" }

foreach ($svc in $services) {
    Write-Host "Procesando $($svc.Name)..." -ForegroundColor Cyan
    $basePath = Join-Path $svc.FullName "src\main\java\com\tfg"
    if (-not (Test-Path $basePath)) { continue }
    
    # Obtener el subdirectorio del microservicio (ej. ms_users)
    $subDir = Get-ChildItem -Path $basePath -Directory | Select-Object -First 1
    if ($null -eq $subDir) { continue }
    
    # 1. Renombrar subdirectorios a minúsculas
    $dirs = Get-ChildItem -Path $subDir.FullName -Directory
    foreach ($dir in $dirs) {
        $lowerName = $dir.Name.ToLower()
        if ($dir.Name -ne $lowerName) {
            Write-Host "  Renombrando $($dir.Name) -> $lowerName" -ForegroundColor Yellow
            $tempName = "$($dir.FullName)_tmp"
            Rename-Item -Path $dir.FullName -NewName $tempName
            Rename-Item -Path $tempName -NewName $dir.FullName.Replace($dir.Name, $lowerName)
        }
    }
    
    # 2. Corregir declaraciones de paquete e imports en archivos Java
    $javaFiles = Get-ChildItem -Path $svc.FullName -Filter "*.java" -Recurse
    foreach ($file in $javaFiles) {
        $content = Get-Content $file.FullName -Raw
        
        # Lista de segmentos comunes a pasar a minúsculas
        $segments = @("Config", "Controller", "Service", "Repository", "Entity", "DTO", "Model", "Security", "Exception", "Mapper")
        
        $newContent = $content
        foreach ($seg in $segments) {
            # Reemplazar solo cuando es parte de un paquete com.tfg...
            # Ejemplo: com.tfg.ms_users.Config -> com.tfg.ms_users.config
            $newContent = $newContent -replace "com\.tfg\.ms_(\w+)\.$seg", "com.tfg.ms_`$1.$($seg.ToLower())"
            
            # También para la declaración del paquete local
            # Ejemplo: package com.tfg.ms_users.Config; -> package com.tfg.ms_users.config;
            $newContent = $newContent -replace "package com\.tfg\.ms_(\w+)\.$seg", "package com.tfg.ms_`$1.$($seg.ToLower())"
        }
        
        if ($content -ne $newContent) {
            Write-Host "  Actualizado: $($file.Name)" -ForegroundColor Green
            $newContent | Set-Content $file.FullName -Encoding UTF8
        }
    }
}
