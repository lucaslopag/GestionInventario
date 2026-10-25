# Gestión de Inventario

Trabajo de fin de grado (DAM). Aplicación web para gestionar productos, proveedores y stock de un almacén, hecha con una arquitectura de microservicios.

Proyecto en equipo de tres personas. El reparto de tareas está en [`util/DIVISION_TRABAJO.md`](util/DIVISION_TRABAJO.md).

## Qué hace

- Registro e inicio de sesión con confirmación por correo.
- Gestión de productos (alta, edición, borrado lógico) con SKU único.
- Gestión de proveedores.
- Entradas y salidas de stock, con control para que no quede en negativo.
- Historial de auditoría de las acciones realizadas.
- Roles de usuario (ADMIN y EMPLEADO), con una pantalla de usuarios solo para administradores.

## Arquitectura

```
Frontend (HTML/CSS/JS) -> API Gateway -> Eureka -> Microservicios
                                         RabbitMQ -> ms-audit / ms-mail
```

| Servicio | Puerto | Función |
|---|---|---|
| api-gateway | 8080 | Entrada única, validación del JWT y rutas |
| eureka-server | 8761 | Registro de servicios |
| ms-users | 8081 | Registro, login, JWT y usuarios |
| ms-catalog | 8082 | Productos |
| ms-suppliers | 8083 | Proveedores |
| ms-inventory | 8084 | Stock y movimientos |
| ms-audit | 8085 | Historial de auditoría (consume la cola de RabbitMQ) |
| ms-mail | 8086 | Envío de correos (consume la cola de RabbitMQ) |

## Tecnologías

Java 17, Spring Boot 3.3, Spring Cloud Gateway, Eureka, JWT, RabbitMQ, MySQL y HTML, CSS y JavaScript sin frameworks.

## Cómo arrancarlo

Requisitos: Java 17, Maven, MySQL y RabbitMQ en marcha.

1. Crear las bases de datos con `db-scripts/setup_all.sql`.
2. Copiar `.env.example` y definir las variables de entorno que indica (`JWT_SECRET`, `MAIL_USERNAME`, `MAIL_PASSWORD`). Los valores reales no se suben al repositorio.
3. Arrancar los servicios con los scripts de la carpeta `util` (`start_all.ps1`, `start_all.bat` o `start_all_linux.sh`).
4. Abrir `frontend/index.html` con un servidor estático, por ejemplo `python -m http.server 5500` dentro de la carpeta `frontend`.

## Primer administrador

No hay ninguna cuenta de administrador creada de serie. Regístrate en la aplicación con tu correo, edita el correo en `db-scripts/hacer_admin.sql` y ejecútalo. Después, los administradores pueden crear otros usuarios y cambiar roles desde la pantalla de usuarios.

## Seguridad

- Solo el API Gateway (puerto 8080) debe ser accesible desde fuera. Los demás servicios se fían de las cabeceras `X-User-Email` y `X-User-Roles` que pone el gateway, así que no deben exponerse directamente.
- El registro público siempre crea usuarios con rol EMPLEADO. Solo un administrador con sesión iniciada puede crear otro administrador.
- Los secretos (JWT y cuenta de correo) se leen de variables de entorno y no se guardan en el repositorio.

## Tests

Los tests de la lógica de stock están en `backend/ms-inventory`:

```bash
cd backend/ms-inventory
./mvnw test
```
