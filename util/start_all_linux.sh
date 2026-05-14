#!/bin/bash
# ======================================================================
# SCRIPT PARA ARRANCAR TODOS LOS MICROSERVICIOS (LINUX - ORDENADO)
# ======================================================================

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" &> /dev/null && pwd)"
BACKEND_ROOT="$SCRIPT_DIR/../backend"
FRONTEND_ROOT="$SCRIPT_DIR/../frontend"

echo -e "\n \e[36m[!] Iniciando despliegue local (Orden de Integracion)...\e[0m\n"

# Detectar emulador de terminal
TERM_EXEC=""
if command -v gnome-terminal &> /dev/null; then
    TERM_EXEC="gnome-terminal -- bash -c"
elif command -v konsole &> /dev/null; then
    TERM_EXEC="konsole -e bash -c"
elif command -v xterm &> /dev/null; then
    TERM_EXEC="xterm -e bash -c"
fi

launch() {
    local dir=$1
    local cmd=$2
    if [ -n "$TERM_EXEC" ]; then
        $TERM_EXEC "cd '$dir' && $cmd; exec bash" &
    else
        # Fallback a background process
        (cd "$dir" && eval "$cmd" > /dev/null 2>&1 &)
    fi
}

# 1. Eureka
echo -e " \e[33m[1/8] Lanzando EUREKA-SERVER...\e[0m"
launch "$BACKEND_ROOT/eureka-server" "./mvnw spring-boot:run"
sleep 15

# 2 & 3. Infra
echo -e " \e[90m[2/8] Iniciando RABBITMQ...\e[0m"
sudo systemctl start rabbitmq-server 2>/dev/null || echo "  Info: RabbitMQ no gestionado por systemctl (Ignorar si está en Docker o ya está corriendo)"

echo -e " \e[90m[3/8] Iniciando MYSQL...\e[0m"
sudo systemctl start mysql 2>/dev/null || sudo systemctl start mariadb 2>/dev/null || echo "  Info: MySQL/MariaDB no gestionado por systemctl (Ignorar si está en Docker o ya está corriendo)"

# 4. Users
echo -e " \e[33m[4/8] Lanzando MS-USERS...\e[0m"
launch "$BACKEND_ROOT/ms-users" "./mvnw spring-boot:run"
sleep 5

# 5. Gateway
echo -e " \e[33m[5/8] Lanzando API-GATEWAY...\e[0m"
launch "$BACKEND_ROOT/api-gateway" "./mvnw spring-boot:run"
sleep 5

# 6. Dominio
echo -e " \e[33m[6/8] Lanzando MS-CATALOG, MS-SUPPLIERS y MS-INVENTORY...\e[0m"
launch "$BACKEND_ROOT/ms-catalog" "./mvnw spring-boot:run"
launch "$BACKEND_ROOT/ms-suppliers" "./mvnw spring-boot:run"
launch "$BACKEND_ROOT/ms-inventory" "./mvnw spring-boot:run"
sleep 5

# 7. Soporte
echo -e " \e[33m[7/8] Lanzando MS-AUDIT y MS-MAIL...\e[0m"
launch "$BACKEND_ROOT/ms-audit" "./mvnw spring-boot:run"
launch "$BACKEND_ROOT/ms-mail" "./mvnw spring-boot:run"

# 8. Frontend
echo -e " \e[36m[8/8] Lanzando FRONTEND...\e[0m"
if [ -d "$SCRIPT_DIR/../frontend-react" ]; then
    launch "$SCRIPT_DIR/../frontend-react" "npm run dev"
else
    # Servidor estático por defecto usando Python3 para la carpeta frontend
    launch "$FRONTEND_ROOT" "echo 'Servidor Frontend corriendo en http://localhost:8000'; python3 -m http.server 8000"
fi

echo -e "\n \e[32m======================================================================\e[0m"
echo -e " \e[32m  SISTEMA LANZADO COMPLETAMENTE.\e[0m"
echo -e " \e[32m======================================================================\e[0m\n"
