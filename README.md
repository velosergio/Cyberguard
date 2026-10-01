# CyberGuard

Plataforma académica para registrar eventos de una red autorizada, aplicar reglas de detección, clasificar el riesgo y consultar alertas. No escanea la red ni bloquea conexiones.

## Requisitos

- JDK 21 o superior
- MySQL de XAMPP en marcha (usuario `root`, contraseña vacía)

En el panel de XAMPP inicia MySQL. La aplicación crea la base `cyberguard` al arrancar. Si tu usuario de MySQL tiene contraseña, cámbiala en `src/main/resources/application.properties`.

## Dependencias

Están declaradas en `pom.xml`. No hace falta instalar Maven: `.\mvnw.cmd` lo descarga y resuelve las librerías en el repositorio local la primera vez.

Descargar las versiones que ya declara `pom.xml`:

```bat
.\mvnw.cmd dependency:resolve
```

Si el repositorio local quedó desactualizado respecto a esas mismas coordenadas, fuerza otra consulta a Maven Central:

```bat
.\mvnw.cmd dependency:resolve -U
```

Ese `-U` no cambia números de versión. Spring Boot, seguridad, JPA y el conector de MySQL salen del padre `spring-boot-starter-parent` en `pom.xml`. Para subirlas, edita esa versión (hoy `3.5.16`) y vuelve a resolver:

```bat
.\mvnw.cmd dependency:resolve
```

Ver el árbol resuelto:

```bat
.\mvnw.cmd dependency:tree
```

## Ejecutar

```bat
.\mvnw.cmd spring-boot:run
```

Abre http://localhost:8080

## Usuarios de demostración

- `admin` / `Admin123*` — administrador
- `analista` / `Analista123*` — analista
- `consulta` / `Consulta123*` — solo consulta

## Pruebas

```bat
.\mvnw.cmd test
```

Las pruebas usan H2 en memoria y no necesitan MySQL.
