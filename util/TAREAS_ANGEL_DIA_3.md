# 🟢 TAREAS ÁNGEL — Día 3 (Paso a Paso)

> Sigue cada paso en orden. Crea cada archivo tú mismo copiando el código.

---

## PARTE 1: Crear proyecto `ms-audit`

### PASO 1.1 — Crear el proyecto con Spring Initializr

1. Ve a https://start.spring.io/
2. Configura:
   - **Project:** Maven
   - **Language:** Java
   - **Spring Boot:** 3.3.0
   - **Group:** com.tfg
   - **Artifact:** ms-audit
   - **Packaging:** Jar
   - **Java:** 17
3. Añade estas dependencias:
   - Spring Web
   - Spring Data JPA
   - MySQL Driver
   - Spring for RabbitMQ
   - Lombok
   - Eureka Discovery Client
4. Descarga, descomprime y copia la carpeta `ms-audit` dentro de `GestionInventario/`

### PASO 1.2 — Añadir jackson-databind al pom.xml

Abre `ms-audit/pom.xml` y añade esta dependencia dentro de `<dependencies>`:

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
</dependency>
```

---

### PASO 1.3 — Configurar application.properties

Abre `ms-audit/src/main/resources/application.properties` y **reemplaza todo** por:

```properties
server.port=8085
spring.application.name=ms-audit

spring.datasource.url=jdbc:mysql://localhost:3306/db_audit?createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false
spring.datasource.username=tfg
spring.datasource.password=tfg1234
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

---

### PASO 1.4 — Crear la entidad LogAuditoria.java

Crea el paquete `model` y dentro el archivo:
`ms-audit/src/main/java/com/tfg/ms_audit/model/LogAuditoria.java`

```java
package com.tfg.ms_audit.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(name = "proveedor_id")
    private Long proveedorId;

    @Column(name = "usuario_email", nullable = false, length = 150)
    private String usuarioEmail;

    @Column(nullable = false)
    private String accion;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(name = "stock_resultante", nullable = false)
    private Integer stockResultante;
}
```

---

### PASO 1.5 — Crear el repositorio LogAuditoriaRepository.java

Crea el paquete `repository` y dentro:
`ms-audit/src/main/java/com/tfg/ms_audit/repository/LogAuditoriaRepository.java`

```java
package com.tfg.ms_audit.repository;

import com.tfg.ms_audit.model.LogAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LogAuditoriaRepository extends JpaRepository<LogAuditoria, Long> {

    List<LogAuditoria> findByProductoIdOrderByFechaDesc(Long productoId);

    List<LogAuditoria> findByUsuarioEmailOrderByFechaDesc(String usuarioEmail);

    List<LogAuditoria> findAllByOrderByFechaDesc();
}
```

---

### PASO 1.6 — Crear RabbitMQConfig.java

Crea el paquete `config` y dentro:
`ms-audit/src/main/java/com/tfg/ms_audit/config/RabbitMQConfig.java`

```java
package com.tfg.ms_audit.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String COLA_AUDITORIA = "cola.auditoria";
    public static final String COLA_AUDITORIA_DLQ = "cola.auditoria.dlq";

    @Bean
    public Queue colaAuditoria() {
        return QueueBuilder.durable(COLA_AUDITORIA)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", COLA_AUDITORIA_DLQ)
                .build();
    }

    @Bean
    public Queue colaAuditoriaDlq() {
        return QueueBuilder.durable(COLA_AUDITORIA_DLQ).build();
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
```

---

### PASO 1.7 — Crear AuditoriaListener.java

Crea el paquete `listener` y dentro:
`ms-audit/src/main/java/com/tfg/ms_audit/listener/AuditoriaListener.java`

```java
package com.tfg.ms_audit.listener;

import com.tfg.ms_audit.model.LogAuditoria;
import com.tfg.ms_audit.repository.LogAuditoriaRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuditoriaListener {

    @Autowired
    private LogAuditoriaRepository logRepository;

    @RabbitListener(queues = "cola.auditoria")
    public void recibirEventoAuditoria(LogAuditoria logEntrante) {
        System.out.println("📥 Recibido evento de auditoría para producto: " + logEntrante.getProductoId());

        logEntrante.setId(null);
        logRepository.save(logEntrante);

        System.out.println("✅ Auditoría guardada → accion=" + logEntrante.getAccion()
                + ", producto=" + logEntrante.getProductoId()
                + ", stock_resultante=" + logEntrante.getStockResultante());
    }
}
```

---

### PASO 1.8 — Crear AuditoriaController.java

Crea el paquete `controller` y dentro:
`ms-audit/src/main/java/com/tfg/ms_audit/controller/AuditoriaController.java`

```java
package com.tfg.ms_audit.controller;

import com.tfg.ms_audit.model.LogAuditoria;
import com.tfg.ms_audit.repository.LogAuditoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/auditoria")
public class AuditoriaController {

    @Autowired
    private LogAuditoriaRepository logRepository;

    @GetMapping
    public ResponseEntity<?> listarTodos(
            @RequestHeader(value = "X-User-Roles", required = false) String roles) {
        if (!esAdmin(roles)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acceso denegado: solo ADMIN puede ver la auditoría");
        }
        return ResponseEntity.ok(logRepository.findAllByOrderByFechaDesc());
    }

    @GetMapping("/producto/{id}")
    public ResponseEntity<?> listarPorProducto(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Roles", required = false) String roles) {
        if (!esAdmin(roles)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acceso denegado: solo ADMIN puede ver la auditoría");
        }
        return ResponseEntity.ok(logRepository.findByProductoIdOrderByFechaDesc(id));
    }

    @GetMapping("/usuario/{email}")
    public ResponseEntity<?> listarPorUsuario(
            @PathVariable String email,
            @RequestHeader(value = "X-User-Roles", required = false) String roles) {
        if (!esAdmin(roles)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acceso denegado: solo ADMIN puede ver la auditoría");
        }
        return ResponseEntity.ok(logRepository.findByUsuarioEmailOrderByFechaDesc(email));
    }

    private boolean esAdmin(String roles) {
        return roles != null && roles.toUpperCase().contains("ADMIN");
    }
}
```

---

## ✅ FIN PARTE 1 — ms-audit tiene 6 clases:

| Archivo | Paquete | Qué hace |
|---------|---------|----------|
| MsAuditApplication.java | (raíz) | Arranca la app (ya viene creada de Spring Initializr) |
| LogAuditoria.java | model | Entidad → tabla logs |
| LogAuditoriaRepository.java | repository | Queries JPA |
| RabbitMQConfig.java | config | Colas + DLQ + JSON |
| AuditoriaListener.java | listener | Consume cola.auditoria |
| AuditoriaController.java | controller | Endpoints GET (solo ADMIN) |

---

## PARTE 2: Crear proyecto `ms-mail` (ver siguiente sección)

### PASO 2.1 — Crear el proyecto con Spring Initializr

1. Ve a https://start.spring.io/
2. Configura:
   - **Group:** com.tfg
   - **Artifact:** ms-mail
   - **Spring Boot:** 3.3.0 / **Java:** 17
3. Dependencias:
   - Spring Web
   - Spring for RabbitMQ
   - Java Mail Sender
   - Lombok
   - Eureka Discovery Client
4. Descarga y copia `ms-mail` dentro de `GestionInventario/`

### PASO 2.2 — Añadir jackson-databind al pom.xml

Igual que en ms-audit, añade en `ms-mail/pom.xml`:

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
</dependency>
```

---

### PASO 2.3 — Configurar application.properties

`ms-mail/src/main/resources/application.properties`:

```properties
server.port=8086
spring.application.name=ms-mail

spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=tu_correo@gmail.com
spring.mail.password=tu_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

> ⚠️ Cambia tu_correo@gmail.com y tu_app_password por datos reales.

---

### PASO 2.4 — Crear RabbitMQConfig.java

`ms-mail/src/main/java/com/tfg/ms_mail/config/RabbitMQConfig.java`

```java
package com.tfg.ms_mail.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String COLA_MAILS = "cola.mails";
    public static final String COLA_MAILS_DLQ = "cola.mails.dlq";

    @Bean
    public Queue colaMails() {
        return QueueBuilder.durable(COLA_MAILS)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", COLA_MAILS_DLQ)
                .build();
    }

    @Bean
    public Queue colaMailsDlq() {
        return QueueBuilder.durable(COLA_MAILS_DLQ).build();
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
```

---

### PASO 2.5 — Crear MailListener.java

`ms-mail/src/main/java/com/tfg/ms_mail/listener/MailListener.java`

```java
package com.tfg.ms_mail.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class MailListener {

    @Autowired
    private JavaMailSender mailSender;

    @RabbitListener(queues = "cola.mails")
    public void recibirMensajeMail(Map<String, String> mensaje) {
        String email = mensaje.get("email");
        String nombre = mensaje.get("nombre");
        String token = mensaje.get("token");

        System.out.println("📥 Recibido mensaje para enviar email a: " + email);

        try {
            SimpleMailMessage emailMsg = new SimpleMailMessage();
            emailMsg.setTo(email);
            emailMsg.setSubject("Confirma tu cuenta en Sistema de Inventario");
            emailMsg.setText("Hola " + nombre + ",\n\n" +
                    "Confirma tu cuenta aquí:\n" +
                    "http://localhost:8080/api/auth/confirmar?token=" + token + "\n\n" +
                    "Saludos,\nSistema de Gestión de Inventario");

            mailSender.send(emailMsg);
            System.out.println("✅ Email enviado a: " + email);
        } catch (Exception e) {
            System.err.println("❌ Error enviando email: " + e.getMessage());
            throw e;
        }
    }
}
```

---

## ✅ FIN PARTE 2 — ms-mail tiene 3 clases:

| Archivo | Paquete | Qué hace |
|---------|---------|----------|
| MsMailApplication.java | (raíz) | Arranca la app (ya viene de Initializr) |
| RabbitMQConfig.java | config | Colas + DLQ + JSON |
| MailListener.java | listener | Consume cola.mails + envía SMTP |

---

## Checklist final

- [ ] ms-audit creado con Spring Initializr
- [ ] ms-audit: jackson-databind en pom.xml
- [ ] ms-audit: application.properties configurado
- [ ] ms-audit: LogAuditoria.java creado
- [ ] ms-audit: LogAuditoriaRepository.java creado
- [ ] ms-audit: RabbitMQConfig.java creado
- [ ] ms-audit: AuditoriaListener.java creado
- [ ] ms-audit: AuditoriaController.java creado
- [ ] ms-mail creado con Spring Initializr
- [ ] ms-mail: jackson-databind en pom.xml
- [ ] ms-mail: application.properties configurado
- [ ] ms-mail: RabbitMQConfig.java creado
- [ ] ms-mail: MailListener.java creado
