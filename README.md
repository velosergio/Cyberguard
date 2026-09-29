# CyberGuard

Plataforma académica para registrar eventos de una red autorizada, aplicar reglas de detección, clasificar el riesgo y consultar alertas. No escanea la red ni bloquea conexiones.

## Requisitos

- JDK 21 o superior
- MySQL de XAMPP en marcha (usuario `root`, contraseña vacía)

En el panel de XAMPP inicia MySQL. La aplicación crea la base `cyberguard` al arrancar. Si tu usuario de MySQL tiene contraseña, cámbiala en `src/main/resources/application.properties`.

## Ejecutar

```bat
mvnw.cmd spring-boot:run
```

Abre http://localhost:8080

## Usuarios de demostración

- `admin` / `Admin123*` — administrador
- `analista` / `Analista123*` — analista
- `consulta` / `Consulta123*` — solo consulta

## Pruebas

```bat
mvnw.cmd test
```

Las pruebas usan H2 en memoria y no necesitan MySQL.
