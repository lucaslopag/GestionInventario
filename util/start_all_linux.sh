#!/bin/bash
# ======================================================================
# SCRIPT PARA ARRANCAR TODOS LOS MICROSERVICIOS (LINUX - LOGS INDIVIDUALES)
# ======================================================================

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" &> /dev/null && pwd)"
BACKEND_ROOT="$SCRIPT_DIR/../backend"
FRONTEND_ROOT="$SCRIPT_DIR/../frontend"
LOGS_DIR="$SCRIPT_DIR/../logs"

# Credenciales por defecto para todos los microservicios
export MYSQL_USER="root"
export MYSQL_PASSWORD="root"

echo -e "\n \e[36m[!] Iniciando despliegue local (Orden de Integracion)...\e[0m\n"

# Crear carpeta de logs si no existe
mkdir -p "$LOGS_DIR"
echo -e " \e[90m[i] Los logs de cada microservicio se guardarán en: $LOGS_DIR\e[0m"

launch_with_logs() {
    local dir=$1
    local cmd=$2
    local name=$3
    
    # Ejecutamos en background y redirigimos tanto stdout (1) como stderr (2) al archivo de log
    (cd "$dir" && eval "$cmd" > "$LOGS_DIR/${name}.log" 2>&1 &)
}

# 1. Eureka
echo -e " \e[33m[1/8] Lanzando EUREKA-SERVER (Log: eureka-server.log)...\e[0m"
launch_with_logs "$BACKEND_ROOT/eureka-server" "./mvnw spring-boot:run -Dspring-boot.run.arguments='--server.port=8761'" "eureka-server"
sleep 15

# 2 & 3. Infra
echo -e " \e[90m[2/8] Iniciando RABBITMQ...\e[0m"
sudo systemctl start rabbitmq-server 2>/dev/null || echo "  Info: RabbitMQ no gestionado por systemctl (Ignorar si está en Docker)"

echo -e " \e[90m[3/8] Iniciando MYSQL...\e[0m"
sudo systemctl start mysql 2>/dev/null || sudo systemctl start mariadb 2>/dev/null || echo "  Info: MySQL/MariaDB no gestionado por systemctl (Ignorar si está en Docker)"

# 4. Users
echo -e " \e[33m[4/8] Lanzando MS-USERS (Log: ms-users.log)...\e[0m"
launch_with_logs "$BACKEND_ROOT/ms-users" "./mvnw spring-boot:run" "ms-users"
sleep 5

# 5. Gateway
echo -e " \e[33m[5/8] Lanzando API-GATEWAY (Log: api-gateway.log)...\e[0m"
launch_with_logs "$BACKEND_ROOT/api-gateway" "./mvnw spring-boot:run" "api-gateway"
sleep 5

# 6. Dominio
echo -e " \e[33m[6/8] Lanzando MS-CATALOG, MS-SUPPLIERS y MS-INVENTORY...\e[0m"
launch_with_logs "$BACKEND_ROOT/ms-catalog" "./mvnw spring-boot:run" "ms-catalog"
launch_with_logs "$BACKEND_ROOT/ms-suppliers" "./mvnw spring-boot:run" "ms-suppliers"
launch_with_logs "$BACKEND_ROOT/ms-inventory" "./mvnw spring-boot:run" "ms-inventory"
sleep 5

# 7. Soporte
echo -e " \e[33m[7/8] Lanzando MS-AUDIT y MS-MAIL...\e[0m"
launch_with_logs "$BACKEND_ROOT/ms-audit" "./mvnw spring-boot:run" "ms-audit"
launch_with_logs "$BACKEND_ROOT/ms-mail" "./mvnw spring-boot:run" "ms-mail"

# 8. Frontend
echo -e " \e[36m[8/8] Lanzando FRONTEND (Log: frontend.log)...\e[0m"
if [ -d "$SCRIPT_DIR/../frontend-react" ]; then
    launch_with_logs "$SCRIPT_DIR/../frontend-react" "npm run dev" "frontend"
else
    launch_with_logs "$FRONTEND_ROOT" "echo 'Servidor Frontend corriendo en http://localhost:8000'; python3 -m http.server 8000" "frontend"
fi

echo -e "\n \e[32m======================================================================\e[0m"
echo -e " \e[32m  SISTEMA LANZADO COMPLETAMENTE.\e[0m"
echo -e " \e[32m  Puedes ver los logs en vivo ejecutando:\e[0m"
echo -e " \e[32m  tail -f ../logs/ms-users.log\e[0m"
echo -e " \e[32m======================================================================\e[0m\n"
