# CyberGuard

Plataforma académica para registrar eventos de una red autorizada, aplicar reglas de detección, clasificar el riesgo y consultar alertas. No escanea la red ni bloquea conexiones.

El proyecto son dos aplicaciones separadas:

| Carpeta | Qué es | Tecnología | Puerto |
| --- | --- | --- | --- |
| `backend/` | API REST con autenticación JWT | Spring Boot 3.5, Java 21, MySQL | 8080 |
| `frontend/` | Interfaz web | React 19 + Vite | 5173 |

## Requisitos

- JDK 21 o superior
- Node.js 20 o superior
- MySQL de XAMPP en marcha (usuario `root`, contraseña vacía)

En el panel de XAMPP inicia MySQL. El backend crea la base `cyberguard` al arrancar. Si tu usuario de MySQL tiene contraseña, cámbiala en `backend/src/main/resources/application.properties`.

## Ejecutar

Hacen falta dos terminales.

**Backend** (API en http://localhost:8080):

```bat
cd backend
.\mvnw.cmd spring-boot:run
```

No hace falta instalar Maven: `mvnw.cmd` lo descarga y resuelve las librerías la primera vez.

**Frontend** (aplicación en http://localhost:5173):

```bat
cd frontend
npm install
npm run dev
```

Abre http://localhost:5173. En desarrollo Vite reenvía las llamadas a `/api` al backend.

## Usuarios de demostración

- `admin` / `Admin123*` — administrador
- `analista` / `Analista123*` — analista
- `consulta` / `Consulta123*` — solo consulta

## Configuración

Valores del backend en `backend/src/main/resources/application.properties`. Estos dos se pueden cambiar con variables de entorno:

| Variable | Para qué | Valor por defecto |
| --- | --- | --- |
| `CYBERGUARD_JWT_SECRET` | Clave con la que se firman los tokens (mínimo 32 caracteres). Cámbiala fuera de un equipo de laboratorio. | Una clave de desarrollo incluida en el código |
| `CYBERGUARD_CORS_ORIGENES` | Orígenes del frontend que pueden llamar a la API, separados por coma | `http://localhost:5173` |

En el frontend, `VITE_API_URL` (ver `frontend/.env.example`) fija la URL del backend cuando no se usa el proxy de Vite, por ejemplo en producción.

## Compilar el frontend para publicar

```bat
cd frontend
npm run build
```

Genera `frontend/dist/`, archivos estáticos que sirve cualquier servidor web. Si el backend queda en otra dirección, define `VITE_API_URL` antes de compilar y agrega el origen del frontend a `CYBERGUARD_CORS_ORIGENES`.

## Dependencias del backend

Están declaradas en `backend/pom.xml`. Para volver a resolverlas:

```bat
cd backend
.\mvnw.cmd dependency:resolve
```

Si el repositorio local quedó desactualizado, fuerza otra consulta a Maven Central con `-U`. Las versiones de Spring Boot, seguridad, JPA y el conector de MySQL salen del padre `spring-boot-starter-parent` (hoy `3.5.16`). Ver el árbol resuelto: `.\mvnw.cmd dependency:tree`.

## Pruebas

```bat
cd backend
.\mvnw.cmd test
```

Las pruebas usan H2 en memoria y no necesitan MySQL.

## Documentación

- `Docs/instalacion.md` — instalación paso a paso y solución de problemas
- `Docs/GUIA.md` — qué hace el sistema, la API, la arquitectura y las decisiones de diseño
- `Docs/base-de-datos.md` — esquema de la base de datos
