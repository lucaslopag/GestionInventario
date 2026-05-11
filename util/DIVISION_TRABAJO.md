# 📦 Plan de Trabajo — Sistema de Gestión de Inventario (TFG DAM)

> **Equipo:** Lucas · Ángel · Alejandro  
> **Duración:** 7 días  
> **Stack:** Spring Boot · Spring Cloud Gateway · Eureka · JWT · RabbitMQ · MySQL · Docker · HTML/CSS/JS (nginx)

---

## 🧠 Visión general de la arquitectura

```
Frontend (nginx) → API Gateway → [Eureka] → Microservicios
                                 [RabbitMQ] → MS-Audit / MS-Mail
```

| Componente     | Puerto expuesto | Responsable |
|----------------|-----------------|-------------|
| Frontend       | 80              | Alejandro   |
| API Gateway    | 8080            | Ángel       |
| Eureka Server  | 8761 (dev)      | Ángel       |
| RabbitMQ       | 15672 (dev UI)  | Ángel       |
| MS-Users       | interno         | Alejandro   |
| MS-Catalog     | interno         | Lucas       |
| MS-Suppliers   | interno         | Lucas       |
| MS-Inventory   | interno         | Lucas       |
| MS-Audit       | interno         | Ángel       |
| MS-Mail        | interno         | Ángel       |
| MySQL (BBDDs)  | interno         | Alejandro   |

---

## 📋 División de responsabilidades fijas

### 🔵 Lucas — Backend: Microservicios de dominio
- `ms-catalog` (CRUD productos, SKU único, borrado lógico)
- `ms-suppliers` (CRUD proveedores, email único)
- `ms-inventory` (stock, entradas/salidas, publica en `cola.auditoria`)

### 🟢 Ángel — Backend: Infraestructura y servicios de soporte
- `eureka-server` (service registry)
- `api-gateway` (Spring Cloud Gateway, filtro JWT, CORS, enrutamiento)
- `ms-audit` (consume `cola.auditoria`, consultas de historial)
- `ms-mail` (consume `cola.mails`, envía por SMTP)

### 🟡 Alejandro — Frontend + Infraestructura + MS-Users
- `ms-users` (registro, login, JWT, confirmación por email → publica en `cola.mails`)
- Todas las páginas HTML/CSS/JS (login, registro, confirmación, productos, proveedores, inventario, auditoría)
- Esquemas SQL de todas las BBDDs
- `docker-compose.yml` completo con healthchecks y red interna
- Fichero `.env` de ejemplo
- Configuración nginx

---

## 📅 Plan de 7 días — ¿Qué hace cada uno y en qué orden?

> **Filosofía:** Los primeros 3 días todos trabajan de forma **completamente independiente**.  
> Los días 4-5 se unen las piezas. Los días 6-7 se pule y prueba.

---

### ⚡ Días 1-2 — Base independiente (todos en paralelo)

#### 🔵 Lucas — Días 1-2
> Objetivo: los 3 microservicios de dominio funcionan en **local standalone** (sin Gateway, sin Eureka, sin JWT), listos para ser integrados.

**Día 1:**
- [ ] Crear proyecto `ms-catalog` con Spring Initializr:
  - Dependencias: Spring Web, Spring Data JPA, MySQL Driver, Validation, Lombok
  - Entidad `Producto` (id, nombre, sku, precio_neto, activo)
  - Repositorio + Service + Controller REST
  - Endpoints: `GET /productos`, `GET /productos/{id}`, `POST /productos`, `PUT /productos/{id}`, `DELETE /productos/{id}` (borrado lógico)
  - `application.yml` apuntando a `localhost:3306/db_catalog` (para pruebas locales)
- [ ] Crear proyecto `ms-suppliers`:
  - Entidad `Proveedor` (id, nombre, email, direccion)
  - CRUD completo en `/proveedores`
  - Validación de email único

**Día 2:**
- [ ] Crear proyecto `ms-inventory`:
  - Entidad `Stock` (id, producto_id, cantidad_disponible)
  - Entidad `Movimiento` (id, producto_id, proveedor_id, usuario_email, cantidad, tipo, fecha, stock_resultante)
  - Endpoints: `POST /inventario/entrada`, `POST /inventario/salida`, `GET /inventario/stock/{id}`, `GET /inventario/movimientos`
  - Leer `usuario_email` de la cabecera `X-User-Email` (simularlo con un valor fijo mientras no hay Gateway)
  - **Dejar preparado** el método `publicarEventoAuditoria()` → por ahora solo log en consola, se conectará a RabbitMQ el día 3

> 💡 **Truco:** Alejandro sube los scripts SQL el día 1. Lucas los usa para crear su MySQL local sin perder tiempo.

---

#### 🟢 Ángel — Días 1-2
> Objetivo: Eureka + Gateway funcionando (Alejandro se encarga de `ms-users` con JWT).

**Día 1:**
- [ ] Crear proyecto `eureka-server`:
  - Solo `@EnableEurekaServer` y configurar `application.yml`
  - Verificar que arranca y la UI es visible en `localhost:8761`

**Día 2:**
- [ ] Crear proyecto `api-gateway`:
  - Dependencias: Spring Cloud Gateway, Eureka Client
  - Rutas configuradas para todos los MS por nombre lógico (`lb://ms-catalog`, etc.)
  - **`JwtAuthFilter` (GlobalFilter):**
    - Rutas públicas: `/api/auth/register`, `/api/auth/login`, `/api/auth/confirmar`
    - Validar firma JWT con el mismo secreto que `ms-users` (secreto definido por Alejandro)
    - Validar expiración
    - Inyectar `X-User-Email` y `X-User-Roles` a los microservicios
    - Devolver 401/403 según corresponda
  - Configurar CORS
- [ ] Registrar `eureka-server` y esperar que `ms-users` (Alejandro) se registre para probar el Gateway

> 💡 Ángel necesita el `JWT_SECRET` que Alejandro define el día 1 para poder configurar el `JwtAuthFilter` del Gateway.

---

#### 🟡 Alejandro — Días 1-2
> Objetivo: infraestructura Docker lista, `ms-users` con autenticación JWT completa, y frontend con estructura básica.

**Día 1:**
- [ ] Definir `JWT_SECRET` (64 caracteres mínimo) y añadirlo al `.env.example` — **comunicárselo a Ángel el día 1**
- [ ] Crear scripts SQL de todas las BBDDs y subir al repo (`/db-scripts/`):
  - `db_users.sql`, `db_catalog.sql`, `db_suppliers.sql`, `db_inventory.sql`, `db_audit.sql`
- [ ] Crear `docker-compose.yml` base:
  - Contenedor MySQL + RabbitMQ
  - Red interna Docker
  - Variables de entorno desde `.env`
  - Healthchecks básicos para MySQL y RabbitMQ
- [ ] Crear `.env.example` con todas las variables y subirlo al repo
- [ ] Crear proyecto `ms-users`:
  - Entidades: `Usuario` (id, nombre, email, password, rol, confirmado, token_confirmacion, token_expira, fecha_creacion)
  - Endpoints públicos: `POST /auth/register`, `POST /auth/login`, `GET /auth/confirmar`
  - Endpoint protegido: `GET /auth/me`
  - Cifrado BCrypt de contraseñas
  - Generación y firma de JWT (librería `jjwt`, HS256, claims: sub, roles, iat, exp=1h)
  - Publicar en `cola.mails`: por ahora solo log en consola; RabbitMQ real el día 3

**Día 2:**
- [ ] Crear la estructura del frontend (`/frontend/`):
  - `index.html` → Login
  - `register.html` → Registro
  - `confirmacion.html` → Resultado confirmación
  - `dashboard.html` → Panel principal
  - `productos.html` → Gestión de productos
  - `proveedores.html` → Gestión de proveedores
  - `inventario.html` → Entradas y salidas
  - `historial.html` → Auditoría (solo ADMIN)
  - `style.css` → Estilos globales
  - `api.js` → Interceptor HTTP (añade `Authorization: Bearer <token>` automáticamente)
  - Mockear todas las llamadas API con datos estáticos para trabajar sin backend

---

### ⚡ Día 3 — Conectar RabbitMQ y afinar integración

#### 🔵 Lucas — Día 3
- [ ] Añadir dependencia RabbitMQ a `ms-inventory`
- [ ] Implementar `publicarEventoAuditoria()` real: serializar evento JSON y enviarlo a `cola.auditoria`
- [ ] Añadir `RabbitMQConfig` (cola durable, DLQ `cola.auditoria.dlq`)
- [ ] Añadir cliente Eureka a `ms-catalog`, `ms-suppliers` e `ms-inventory`
- [ ] Verificar que los tres MS arrancan y se registran en Eureka
- [ ] Test con Postman: entrada → ver stock → ver evento en RabbitMQ

#### 🟢 Ángel — Día 3
- [ ] Crear proyecto `ms-audit`:
  - Entidad `Log` (append-only)
  - Listener RabbitMQ que consume `cola.auditoria` y persiste en `db_audit`
  - Endpoints: `GET /auditoria`, `GET /auditoria/producto/{id}`, `GET /auditoria/usuario/{email}`
  - Solo accesible por rol ADMIN
- [ ] Conectar `ms-users` a RabbitMQ real: publicar mensaje en `cola.mails` al registrarse
- [ ] Crear proyecto `ms-mail`:
  - Listener que consume `cola.mails`
  - Envío por SMTP (JavaMailSender)
  - DLQ `cola.mails.dlq` tras fallos

#### 🟡 Alejandro — Día 3
- [ ] Añadir al `docker-compose.yml` todos los microservicios con sus imágenes
- [ ] Configurar `depends_on` con `condition: service_healthy`
- [ ] Añadir contenedor `frontend` (nginx)
- [ ] Añadir `nginx.conf`

---

### ⚡ Días 4-5 — Integración completa

**Orden de prueba sugerido:**
```
1. Eureka arranca ✓
2. RabbitMQ arranca ✓
3. MySQL arranca ✓
4. ms-users arranca y se registra en Eureka ✓
5. api-gateway arranca ✓
6. ms-catalog, ms-suppliers, ms-inventory arrancan ✓
7. ms-audit, ms-mail arrancan ✓
8. Frontend sirve en :80 ✓
9. POST /api/auth/register → recibir email ✓
10. POST /api/auth/login → obtener JWT ✓
11. POST /api/productos (con JWT ADMIN) → 201 ✓
12. POST /api/inventario/entrada → 201 ✓
13. GET /api/auditoria → logs visibles ✓
```

**Mientras tanto, Alejandro** trabaja en el frontend real:
- [ ] Login: POST, guardar JWT en `sessionStorage`, redirigir a dashboard
- [ ] Registro: POST, mostrar "revisa tu correo"
- [ ] Productos: listar, crear, editar, desactivar
- [ ] Proveedores: CRUD completo
- [ ] Inventario: formulario entrada/salida, mostrar stock
- [ ] Historial (solo ADMIN): tabla con filtros de fecha, paginación

---

### ⚡ Días 6-7 — Pulido, pruebas y Docker final

#### 🔵 Lucas — Días 6-7
- [ ] Manejo correcto de errores: 404, 409 (stock insuficiente), 400
- [ ] Verificar que `cantidad_disponible` nunca queda negativo
- [ ] Probar borrado lógico de productos (historial sigue visible)

#### 🟢 Ángel — Días 6-7
- [ ] Probar flujo completo registro + confirmación + login
- [ ] Verificar que EMPLEADO recibe 403 en `/api/auditoria`
- [ ] Verificar 401 con token inválido/caducado
- [ ] Revisar que mensajes fallidos van a DLQ
- [ ] Añadir Dockerfile a cada microservicio

#### 🟡 Alejandro — Días 6-7
- [ ] Pulir CSS/UX (mensajes de error, loading states)
- [ ] Ocultar elementos según rol (EMPLEADO no ve borrar ni historial)
- [ ] Verificar `docker-compose up --build` desde cero
- [ ] Crear `README.md`: cómo arrancar, qué hace cada servicio

---

## 🔗 Dependencias críticas

```
JWT_SECRET  (Ángel define día 1, Alejandro pone en .env)
    │
    └── ms-users [firma JWT] ──────── api-gateway [valida JWT]
    └── ms-users ──[cola.mails]────── ms-mail [consume]
    
ms-inventory ──[cola.auditoria]────── ms-audit [consume]

api-gateway ──[lb://ms-*]──────────── Eureka [resuelve IPs]
                                          ▲
                               todos los MS se registran aquí

Alejandro (docker-compose) ──── todos arrancan juntos
```

### Tabla: ¿qué necesito de quién?

| Para que funcione... | Necesito de... |
|---|---|
| Lucas pruebe sus MS | Alejandro levante MySQL (`docker-compose up mysql`) |
| Ángel pruebe el Gateway | Al menos un MS de Lucas registrado en Eureka |
| Alejandro conecte el frontend | Gateway de Ángel devolviendo JWT |
| ms-audit funcione | Lucas tenga ms-inventory publicando en RabbitMQ (día 3) |
| ms-mail funcione | Ángel tenga ms-users publicando en cola.mails (día 3) |

---

## ⚠️ Reglas de equipo importantes

1. **JWT_SECRET**: Alejandro lo define el día 1 (es el responsable de `ms-users`) y lo comunica a Ángel para el Gateway. String de 64 caracteres mínimo. **Crítico: sin esto el Gateway no puede validar tokens.**

2. **Nombres de servicios en Eureka** (acordar día 1 y no cambiar):
   - `ms-users`, `ms-catalog`, `ms-suppliers`, `ms-inventory`, `ms-audit`

3. **Sin FK físicas entre servicios.** `producto_id` en `movimientos` es un entero simple, sin FK real.

4. **`X-User-Email`**: Lucas la lee directamente del header. El Gateway la inyecta; los MS internos no extraen JWT.

5. **Formato del mensaje de auditoría** (acordar entre Lucas y Ángel antes del día 3):
   ```json
   {
     "producto_id": 42,
     "proveedor_id": 7,
     "usuario_email": "empleado@empresa.es",
     "accion": "ENTRADA",
     "cantidad": 25,
     "fecha": "2025-09-12T10:32:11",
     "stock_resultante": 130
   }
   ```

6. **Para pruebas locales sin Docker**: usar MySQL en local con los scripts de Alejandro y conexión `localhost:3306`.

---

## 📁 Estructura de carpetas recomendada

```
gestionInventario/
├── .env.example                  (Alejandro)
├── docker-compose.yml            (Alejandro)
├── README.md                     (todos)
├── db-scripts/                   (Alejandro)
│   ├── db_users.sql
│   ├── db_catalog.sql
│   ├── db_suppliers.sql
│   ├── db_inventory.sql
│   └── db_audit.sql
├── frontend/                     (Alejandro)
│   ├── nginx.conf
│   ├── Dockerfile
│   ├── index.html
│   ├── register.html
│   ├── confirmacion.html
│   ├── dashboard.html
│   ├── productos.html
│   ├── proveedores.html
│   ├── inventario.html
│   ├── historial.html
│   ├── style.css
│   └── api.js
├── eureka-server/                (Ángel)
├── api-gateway/                  (Ángel)
├── ms-users/                     (Alejandro)
├── ms-audit/                     (Ángel)
├── ms-mail/                      (Ángel)
├── ms-catalog/                   (Lucas)
├── ms-suppliers/                 (Lucas)
└── ms-inventory/                 (Lucas)
```

---

## 📊 Resumen visual de los 7 días

| Día | Lucas | Ángel | Alejandro |
|-----|-------|-------|-----------|
| 1 | ms-catalog + ms-suppliers | eureka-server | Scripts SQL + docker-compose base + ms-users (JWT) |
| 2 | ms-inventory (sin RabbitMQ aún) | api-gateway (JWT filter + rutas) | Estructura frontend (mocks) |
| 3 | RabbitMQ en ms-inventory + Eureka en todos | ms-audit + ms-mail + RabbitMQ en ms-users | Docker completo + nginx |
| 4 | Pruebas integración completa | Pruebas auth + auditoría | Conectar frontend a API real |
| 5 | Corrección de bugs | Corrección de bugs | Conectar frontend a API real |
| 6 | Errores + validaciones | Pruebas seguridad (roles, 401/403) | CSS/UX + roles en frontend |
| 7 | Revisión final | Revisión final + Dockerfiles | README + docker-compose up --build |

---

*TFG DAM — Gestión de Inventario con Microservicios · Lucas · Ángel · Alejandro*
