# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Proyecto

CyberGuard es una plataforma académica: registra a mano eventos de una red autorizada, los analiza con reglas, clasifica el riesgo y genera alertas. **No** escanea la red ni bloquea conexiones; no agregues funciones de captura, escaneo o bloqueo.

Son dos aplicaciones independientes que se hablan por HTTP + JSON:

- `backend/` — API REST, Spring Boot 3.5, Java 21, Maven (wrapper), MySQL/MariaDB de Laragon o XAMPP. Puerto 8080.
- `frontend/` — React 19 + Vite + react-router-dom 7, JavaScript (sin TypeScript). Puerto 5173; Vite reenvía `/api` al backend.

Todo el código, los nombres, los mensajes y la documentación están en **español**. Mantén ese idioma en identificadores, mensajes de error y commits.

Reglas de trabajo del equipo (ramas, PR, definición de terminado, uso de IA, límites de alcance): `GOBERNANZA.md`.

Skills del proyecto (skills.sh, en `.claude/skills/` y `.agents/skills/`): `find-skills`, `java-springboot`, `springboot-security`, `springboot-tdd`, `vercel-react-best-practices` y `mysql`. Para qué sirve cada una y cómo se administran: `AGENTS.md`. Son guías genéricas: si una contradice este archivo (p. ej. `ProblemDetail`, `@PreAuthorize`, Testcontainers, tablas de catálogo, Next.js), **manda este archivo**.

Documentación de referencia: `Docs/GUIA.md` (API completa, arquitectura y decisiones de diseño), `Docs/base-de-datos.md` (esquema), `Docs/NORMALIZACION.md` (formas normales y desnormalizaciones deliberadas; revísalo antes de agregar columnas copiadas o derivadas), `Docs/conexion.md` (`.env` y base de datos), `Docs/instalacion.md`.

## Comandos

Backend (desde `backend/`, en Windows usa `.\mvnw.cmd`; en Git Bash `./mvnw`):

```bat
copy .env.example .env                                       :: solo la primera vez
.\mvnw.cmd spring-boot:run                                   :: arranca la API (necesita MySQL en marcha)
.\mvnw.cmd test                                              :: todas las pruebas (H2 en memoria, sin MySQL)
.\mvnw.cmd test -Dtest=MotorAnalisisTest                     :: una clase de prueba
.\mvnw.cmd test -Dtest=SeguridadAccesoTest#nombreDelMetodo   :: un método
.\mvnw.cmd package                                           :: compila y empaqueta
```

Frontend (desde `frontend/`):

```bat
npm install
npm run dev      :: servidor de desarrollo
npm test         :: Vitest + Testing Library
npm run build    :: genera dist/
```

Usuarios de demostración: `admin`/`Admin123*`, `analista`/`Analista123*`, `consulta`/`Consulta123*`.

Configuración del backend: `backend/.env`, que se carga sola con `springboot3-dotenv` (plantilla en `backend/.env.example`; `.env` no se versiona). Variables: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`, `SERVER_PORT`, `CYBERGUARD_JWT_SECRET` (≥32 caracteres) y `CYBERGUARD_CORS_ORIGENES`. `application.properties` solo las referencia con valores por defecto de laboratorio: una configuración nueva se agrega en los dos archivos y nunca se pone un secreto directamente en `application.properties`. Frontend: `VITE_API_URL` (solo cuando no se usa el proxy de Vite).

## Arquitectura del backend

Paquete raíz `com.cun.cyberguard`. Capas estrictas, cada una solo habla con la de abajo, con inyección por constructor:

```
web/ (controladores REST, ApiExceptionHandler)  →  service/  →  analysis/  →  repository/ + domain/
web/form/  entrada validada con Jakarta Validation
web/dto/   salida JSON (records con fábrica estática `de(entidad)`)
config/    seguridad JWT/CORS, DataInitializer (datos demo solo si la tabla está vacía)
```

Puntos que requieren leer varios archivos para entenderse:

- **Flujo de registro de evento**: `EventoController` → `EventoService.registrar` (guarda, asocia dispositivo por id o IP, y avanza la última actividad con `Dispositivo.registrarActividad`, que nunca la retrocede) → `MotorAnalisis.analizar` → `ClasificadorRiesgo` → si hubo detecciones, `AlertaService` crea **una sola** alerta con todas las reglas que saltaron. El análisis ocurre solo al registrar, nunca al listar.
- **Motor de reglas = patrón estrategia**. La condición vive en código (`analysis/reglas/*`, implementan `ReglaDeteccion`); los números ajustables (puntuación, umbral, ventana, activa) viven en la tabla `reglas`. `MotorAnalisis` recibe `List<ReglaDeteccion>` de Spring y las indexa por `codigo()`. Los códigos están en `CodigosRegla`. Si una fila no tiene clase, se ignora. El historial que recibe cada regla **incluye el evento recién guardado** y abarca `[fechaHora − ventana, fechaHora]` de esa IP: los eventos posteriores no cuentan (`EventoRepository.historial` con `desde` y `hasta`). Por eso `EventoForm` rechaza fechas futuras (`@PastOrPresent`).
- **Nueva regla de detección**: crear clase `@Component` que implemente `ReglaDeteccion`, añadir el código en `CodigosRegla` y la fila en `DataInitializer`. No modificar `MotorAnalisis`. Ojo: `DataInitializer` solo siembra si la tabla está vacía, así que en una base existente la fila nueva hay que insertarla aparte.
- **Errores**: los servicios lanzan `RecursoNoEncontradoException` (→404) o `IllegalArgumentException` (→400 regla de negocio); `ApiExceptionHandler` los traduce a `{"mensaje", "errores"}`. No construyas `ResponseEntity` de error en los controladores.
- **Seguridad**: JWT HS256 stateless (sin sesión, CSRF desactivado). Los permisos por ruta y método están centralizados en `SecurityConfig` (`requestMatchers`); al añadir un endpoint, define ahí su rol. Roles: `ADMINISTRADOR` ⊃ `ANALISTA` ⊃ `CONSULTA`.
- **`open-in-view=false`**: todo lo perezoso debe cargarse dentro de la transacción del servicio antes de mapear a DTO. Nunca devolver entidades JPA desde un controlador.
- **Enums**: se persisten como `STRING` y cada uno expone `getEtiqueta()`. `CatalogoController` (`GET /api/catalogos`) los sirve al frontend; un enum nuevo visible en la UI se registra ahí.
- **Base de datos**: `ddl-auto=update`; `allow_jdbc_metadata_access=false` y dialecto fijo por incompatibilidad de MariaDB (no quitar). Zona horaria `America/Bogota`. Pruebas con perfil `test` y H2 (`src/test/resources/application-test.properties`).
- **Convención de IDs**: la clave primaria se llama `id<Entidad en plural>` en Java (`idDispositivos`, `idAlertas`…) con columna `id_<tabla>`. DTO, forms y frontend usan el mismo nombre. Respétala en entidades nuevas. `ddl-auto=update` no renombra columnas: tras un cambio de este tipo hay que recrear la base local (`Docs/conexion.md`).
- **CSV**: separador `;` y BOM UTF-8 (para Excel en español).

## Arquitectura del frontend

- `api.js` es el **único** punto de acceso HTTP: añade el token (`sessionStorage`), arma query params omitiendo vacíos, convierte errores en `ApiError {status, mensaje, errores}` y dispara el cierre de sesión ante un 401. Usa `api.get/post/put` o `descargar` (archivos protegidos vía `Blob`); nunca `fetch` directo en páginas.
- `auth.jsx` expone `useAuth()` con `esAdmin` y `puedeEscribir`; úsalos para ocultar acciones. Ocultar en la UI no protege: el control real está en `SecurityConfig`.
- `catalogos.jsx` carga `/api/catalogos` una vez; usa `etiqueta(catalogo, valor)` y las opciones de ahí. No repitas enums ni etiquetas en JavaScript.
- `util.js`: `useConsulta(fn, deps)` para cargar datos (`{datos, error, cargando}`) y `formatFecha`. `components/useFormulario.js`: estado de formularios y reparto de errores 400 por campo.
- `components/ui.jsx`: piezas compartidas (`Encabezado`, `Estado`, `Campo`, `Selector`, `Nivel`, `EnlaceBoton`, `MensajeError`). `pages/`: una pantalla por archivo; rutas en `App.jsx` con `RutaProtegida`.

## Principios de diseño

### SOLID — cómo se aplican aquí

- **S — Responsabilidad única.** Controlador: HTTP, validación `@Valid`, mapeo a DTO. Servicio: reglas de negocio y transacciones. `analysis/`: decidir si algo es sospechoso. Repositorio: consultas. Una página React muestra; la lógica HTTP vive en `api.js` y la de formularios en `useFormulario`. Si un método mezcla dos de estas cosas, divídelo.
- **O — Abierto/cerrado.** Extiende añadiendo, no editando: una detección nueva es una clase `ReglaDeteccion` nueva; un catálogo nuevo es una línea en `CatalogoController`; un error nuevo es un `@ExceptionHandler`. Evita `switch`/`if` por código de regla o por tipo de evento dentro del motor o de los servicios.
- **L — Sustitución de Liskov.** Toda `ReglaDeteccion` debe respetar el contrato: sin efectos secundarios, sin acceso a repositorios (todo llega en `ContextoAnalisis`), devolver `Optional.empty()` cuando no se cumple y nunca lanzar por datos normales. El motor no debe necesitar saber qué implementación concreta tiene.
- **I — Segregación de interfaces.** Interfaces pequeñas y específicas (`ReglaDeteccion` tiene dos métodos). Forms de entrada y DTO de salida separados por caso de uso (p. ej. `AlertaEstadoForm` solo trae estado y nota; el panel usa un DTO de alerta sin reglas). No crees un DTO "universal" con campos opcionales para todo.
- **D — Inversión de dependencias.** Inyección por constructor con campos `final`; nada de `@Autowired` en campos ni `new` de servicios. El motor depende de la abstracción `ReglaDeteccion`, no de las clases concretas. Configuración (secreto JWT, CORS, duración) vía `application.properties` + `@Value` (como en `JwtConfig`, `TokenService`, `SecurityConfig`), no constantes en código.

### DRY — fuentes únicas de verdad

- Enums y etiquetas: solo en `domain/enums`; el frontend los obtiene de `/api/catalogos`.
- Códigos de regla: solo en `CodigosRegla`.
- Permisos por ruta: solo en `SecurityConfig` (el frontend solo refleja con `esAdmin`/`puedeEscribir`).
- Formato de errores: solo en `ApiExceptionHandler` + `ErrorRespuesta`; en el cliente, solo en `api.js`.
- Mapeo entidad → JSON: solo en el `de(...)` de cada DTO.
- Rangos de fechas de filtros: usa `service/Fechas` (`inicio`, `finExclusivo`).
- Cortes de nivel de riesgo y tope 0–100: solo en `ClasificadorRiesgo`.
- Frontend: reutiliza `useConsulta`, `useFormulario`, `ui.jsx` y `formatFecha` antes de escribir estado de carga, manejo de errores o formato a mano.

Antes de escribir un helper, busca si ya existe uno equivalente. Pero no abstraigas por adelantado: extrae solo cuando la misma lógica aparece por segunda vez y cambia por el mismo motivo.

## Al cambiar algo

- Si cambias la API (rutas, campos de DTO/form, roles), actualiza el frontend que la consume y la tabla de API en `Docs/GUIA.md`; si cambias el esquema, `Docs/base-de-datos.md`.
- Los cambios de permisos o de contrato HTTP deben reflejarse en `SeguridadAccesoTest`; los de reglas o clasificación, en `MotorAnalisisTest` / `ClasificadorRiesgoTest`; los de consultas del historial, en `EventoRepositoryTest`; los del registro de eventos, en `EventoServiceTest`; los de validaciones de formulario, en `web/form/*Test` (como `EventoFormTest`).
- Si cambias la configuración (`application.properties`, `.env.example`), actualiza `Docs/conexion.md` y la tabla de configuración del `README.md`.
- Verifica con `.\mvnw.cmd test` (backend), `npm test` y `npm run build` (frontend).
