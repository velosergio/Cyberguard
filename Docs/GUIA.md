# Guía de CyberGuard

CyberGuard es una aplicación académica para **registrar** eventos de una red autorizada, **revisarlos con reglas** y **avisar** cuando algo merece atención. Sirve para practicar programación, bases de datos y seguridad en un laboratorio. No vigila la red por su cuenta y no bloquea conexiones.

Esta guía describe el programa tal como está construido. El documento del proyecto (`PROYECTO CYBERGUARD.docx.md`) explica el problema, los objetivos y el alcance académico.

## Qué hace, en una frase

Alguien anota lo que pasó en la red (un intento de conexión, un acceso a un puerto, un equipo desconocido). La aplicación guarda ese dato, lo compara con reglas y, si alguna se cumple, crea una alerta con un nivel de riesgo.

```text
Registro del evento → se guarda → se analizan las reglas activas
        → se suman las puntuaciones → se clasifica el riesgo
        → si hubo detecciones, nace una alerta → se consulta en el panel
```

Los eventos se cargan a mano desde la pantalla **Eventos**. La primera vez que la base está vacía, la aplicación también carga usuarios, reglas, tres dispositivos de ejemplo y una serie de eventos de demostración. Esos eventos de ejemplo pasan por el mismo análisis que un registro normal, así que al entrar ya hay alertas para revisar.

## Quién entra y qué puede hacer

Hay tres roles. Las contraseñas se guardan cifradas con BCrypt. Un usuario marcado como inactivo no puede iniciar sesión.

| Rol | Usuario de demostración | Qué puede hacer |
| --- | --- | --- |
| Administrador | `admin` / `Admin123*` | Todo lo del analista, más crear y editar usuarios, y ajustar las reglas. |
| Analista | `analista` / `Analista123*` | Registrar y editar dispositivos, registrar eventos, cambiar el estado de las alertas y descargar reportes. Puede ver las reglas. |
| Consulta | `consulta` / `Consulta123*` | Ver el panel, los dispositivos, los eventos y las alertas. No crea ni modifica datos, ni abre reportes. |

El menú y la API se controlan juntos: Spring Security decide en el backend si la petición está permitida, y el frontend oculta los botones y enlaces que ese rol no debe usar. Ocultar un botón no protege nada por sí solo; la protección real es la del backend.

## Pantallas

Son rutas del frontend (React). Cada una consulta la API REST del backend, descrita más abajo.

| Ruta | Para qué sirve |
| --- | --- |
| `/login` | Entrada. |
| `/` | Panel: conteos de dispositivos, eventos y alertas, más lo más reciente. |
| `/dispositivos` | Inventario de equipos de la red autorizada. |
| `/eventos` | Historial y formulario para registrar un evento. |
| `/alertas` | Lista y detalle. El analista o el administrador cambian el estado y dejan una nota. |
| `/reglas` | Condiciones del análisis. Solo el administrador edita puntuación, umbral, ventana y si está activa. |
| `/usuarios` | Altas y cambios de usuarios. Solo el administrador. |
| `/reportes` | Consulta y descarga en CSV de eventos y alertas. Administrador y analista. |
| `/info` | Explicación del sistema para cualquier rol: flujo, arquitectura, base de datos, cardinalidad, normalización, motor de reglas, seguridad, gobernanza y skills. Contenido estático (no consulta la API); si cambian estos documentos, revisa `frontend/src/components/info/contenido.js`. |

## API REST

Todas las rutas cuelgan de `/api`, envían y reciben JSON y, salvo el login, exigen `Authorization: Bearer <token>`. Los errores llevan `{"mensaje": "...", "errores": {"campo": "..."}}`; `errores` aparece cuando falla la validación de un formulario (400).

| Método y ruta | Rol mínimo | Para qué sirve |
| --- | --- | --- |
| `POST /api/auth/login` | Público | Recibe `usuario` y `contrasena`; devuelve `token`, `expiraEn` y los datos del usuario. |
| `GET /api/auth/me` | Cualquiera | Usuario del token actual. |
| `GET /api/catalogos` | Cualquiera | Valores y etiquetas de tipos, estados, importancias, niveles y roles. |
| `GET /api/panel` | Cualquiera | Conteos y lo más reciente. |
| `GET /api/dispositivos`, `GET /api/dispositivos/{id}` | Cualquiera | Inventario. |
| `POST /api/dispositivos`, `PUT /api/dispositivos/{id}` | Analista | Alta y edición. |
| `GET /api/eventos?ip&tipo&desde&hasta` | Cualquiera | Historial con filtros (fechas `yyyy-MM-dd`). |
| `POST /api/eventos` | Analista | Registra el evento y lo analiza; responde 201. |
| `GET /api/alertas?nivel&estado&desde&hasta`, `GET /api/alertas/{id}` | Cualquiera | Lista y detalle con las reglas activadas. |
| `PUT /api/alertas/{id}/estado` | Analista | Cambia estado y nota. |
| `GET /api/reglas`, `GET /api/reglas/{id}` | Analista | Catálogo de reglas. |
| `PUT /api/reglas/{id}` | Administrador | Ajusta puntuación, umbral, ventana y estado. |
| `GET /api/reportes?ip&tipo&nivel&desde&hasta` | Analista | Eventos y alertas filtrados. |
| `GET /api/reportes/eventos.csv`, `GET /api/reportes/alertas.csv` | Analista | Descarga en CSV. |
| `GET/POST /api/usuarios`, `GET/PUT /api/usuarios/{id}` | Administrador | Gestión de usuarios. |

«Analista» incluye al administrador.

## Cómo está organizado el código

El repositorio tiene dos aplicaciones independientes:

```text
backend/     API REST con Spring Boot (Java 21, Maven)
frontend/    interfaz con React + Vite
Docs/        documentación
```

### Backend

Todo el código Java vive en `backend/src/main/java/com/cun/cyberguard`. Cada carpeta es una responsabilidad:

```text
CyberguardApplication.java     arranque de Spring Boot
config/                        seguridad (JWT, CORS), carga de datos de demostración
web/                           controladores REST bajo /api y manejo de errores
web/dto/                       datos que salen hacia el frontend (JSON)
web/form/                      datos que llegan del frontend, con validaciones
service/                       reglas de negocio y transacciones
analysis/                      motor que decide si un evento es sospechoso
analysis/reglas/               una clase por cada tipo de detección
domain/                        tablas: Usuario, Dispositivo, Evento, Regla, Alerta
domain/enums/                  valores fijos (rol, tipo de evento, nivel de riesgo…)
repository/                    consultas a la base de datos
```

Las pruebas están en `backend/src/test` y usan una base H2 en memoria, así que no necesitan MySQL.

### Frontend

```text
frontend/src/
├── main.jsx, App.jsx        arranque y tabla de rutas
├── api.js                   llamadas HTTP, token y manejo de errores
├── auth.jsx                 sesión y rol del usuario
├── catalogos.jsx            listas de valores (tipos, estados, roles) que sirve la API
├── components/              menú, rutas protegidas, piezas de formulario
├── pages/                   una pantalla por archivo
└── index.css                estilos
```

## Arquitectura

Son **dos aplicaciones separadas** que se hablan por HTTP. El frontend (React) corre en el navegador y pide datos en JSON a la API del backend (Spring Boot). El backend no genera HTML y no guarda sesión: cada petición lleva un token JWT.

```text
Navegador
    │  React (frontend/)    pantallas, formularios, rutas
    │
    │  HTTP + JSON, cabecera Authorization: Bearer <token>
    ▼
API REST          web/ + web/dto/      recibe JSON, valida y responde JSON
    │
    ▼
Negocio           service/              validar, guardar, consultar
    │
    ▼
Análisis          analysis/             reglas, puntuación, nivel de riesgo
    │
    ▼
Datos             repository/ + domain/ consultas y tablas
    │
    ▼
MySQL / MariaDB   base cyberguard (Laragon o XAMPP)
```

Cada capa habla con la de abajo a través de clases concretas de Spring (inyección por constructor). Un controlador no escribe SQL ni decide si un escaneo de puertos es sospechoso: llama a un servicio, y el servicio llama al motor cuando hace falta.

### Qué pasa al registrar un evento

1. `EventoController` (`POST /api/eventos`) recibe el JSON y comprueba que los campos sean válidos.
2. `EventoService.registrar` exige un puerto cuando el tipo es «acceso a puerto», busca el dispositivo por el seleccionado o por la IP, y guarda el evento.
3. Si el dispositivo existe y no está marcado como desconocido, actualiza su última actividad, solo si el evento es más reciente que la que ya tenía.
4. `MotorAnalisis.analizar` recorre solo las reglas activas.
5. Para cada regla busca la clase Java que tiene el mismo código, arma el historial de esa IP dentro de la ventana de minutos y le pide una evaluación.
6. `ClasificadorRiesgo` suma las puntuaciones de las reglas que se cumplieron y las deja entre 0 y 100.
7. Si hubo al menos una detección, `AlertaService` crea **una** alerta ligada a ese evento y a todas las reglas que saltaron. El estado inicial es pendiente.

La consulta de listas no vuelve a analizar. El análisis ocurre en el momento del registro.

## El motor de reglas

Hay dos piezas que trabajan juntas:

- **La fila en la base** (`Regla`): nombre, descripción, puntuación, umbral, ventana en minutos y si está activa. El administrador puede cambiar esos números sin tocar código.
- **La clase Java** (`ReglaDeteccion`): el criterio. Por ejemplo, contar puertos distintos o intentos de conexión. Spring descubre todas las clases marcadas como componente y el motor las guarda en un mapa por código.

Para agregar un comportamiento nuevo se escribe otra clase que implemente `ReglaDeteccion`, se le da un código y se inserta la fila correspondiente. El motor no hay que reescribirlo: si no encuentra una clase para un código, ignora esa fila.

Cada evaluación recibe un `ContextoAnalisis`: el evento nuevo, la regla con sus números, el historial reciente de la misma IP y si esa IP pertenece a un dispositivo registrado (y que no esté en estado desconocido).

| Código | Cuándo se cumple | Puntuación inicial | Umbral | Ventana |
| --- | --- | --- | --- | --- |
| Dispositivo no registrado | La IP no está en el inventario, o el equipo está como desconocido. | 10 | 1 | 0 min |
| Actividad inusual | El tipo del evento es «actividad inusual». | 10 | 1 | 60 min |
| Repetición de conexiones | La misma IP repite el mismo tipo de evento al menos el umbral de veces. | 15 | 5 | 10 min |
| Múltiples intentos | Hay al menos el umbral de intentos de conexión en la ventana. | 20 | 8 | 5 min |
| Escaneo de puertos | La IP tocó al menos el umbral de puertos distintos. | 25 | 5 | 2 min |
| Acumulación sospechosa | Hay al menos el umbral de eventos de importancia alta. | 30 | 4 | 30 min |

El historial incluye el evento que se acaba de guardar, porque ya está en la base cuando corre el análisis. Por eso un umbral de 5 significa «cinco eventos en la ventana», contando el actual.

La ventana va hacia atrás desde la fecha del evento analizado: desde `fechaHora − ventana` hasta `fechaHora`, ambos límites incluidos. Los eventos posteriores de la misma IP no cuentan, así que registrar un evento con fecha pasada no dispara reglas por lo que pasó después. Por la misma razón, el formulario no acepta fechas futuras (`@PastOrPresent` en `EventoForm` y `max` en el campo del cliente).

### Cómo se traduce la suma en un nivel

| Puntuación | Nivel |
| --- | --- |
| 0 a 20 | Bajo |
| 21 a 50 | Medio |
| 51 a 80 | Alto |
| 81 a 100 | Crítico |

Si varias reglas se cumplen a la vez, sus puntos se suman y el tope es 100. Una alerta con nivel alto o crítico es una señal para que una persona la revise. No es un dictamen de que hubo un ataque.

Los estados de seguimiento son: pendiente, en revisión, atendida y cerrada.

## Qué se guarda

Cinco tablas principales, creadas y actualizadas por Hibernate al arrancar (`ddl-auto=update`):

- **usuarios**: nombre, usuario, correo, contraseña cifrada, rol y si está activo.
- **dispositivos**: IP única, MAC, nombre, tipo (computador, teléfono, servidor, impresora, cámara u otro) y estado (activo, inactivo, desconocido).
- **eventos**: IP, tipo, puerto opcional, fecha, detalle, importancia (baja, media, alta) y, si se conoce, el dispositivo.
- **reglas**: el catálogo ajustable descrito arriba. El código es único.
- **alertas**: evento, dispositivo si lo hay, puntuación, nivel, estado y nota. La tabla `alerta_regla` une una alerta con las reglas que la provocaron.

Los valores de los enums se guardan como texto (`ADMINISTRADOR`, `ALTO`, …), así un cambio de orden en el código Java no desordena los datos ya guardados.

La zona horaria de la aplicación y de JDBC es `America/Bogota`.

## Decisiones de diseño

**Registro manual, en un entorno autorizado.** El alcance del proyecto es académico. La aplicación centraliza dispositivos, eventos y alertas para que se puedan relacionar. No captura tráfico, no ejecuta escaneos y no corta conexiones.

**Capas separadas.** La interfaz, el negocio, el análisis y la base cambian por motivos distintos. Un ajuste de una regla de detección se queda en `analysis/reglas` y no obliga a rehacer las pantallas.

**Patrón estrategia en el motor.** La condición vive en código, porque cada regla mira datos distintos (puertos, importancia, tipo de evento). Los números viven en la base, porque el laboratorio debe poder afinarlos. Un mapa `codigo → estrategia` une las dos partes.

**Una alerta por evento analizado.** Si en el mismo momento saltan tres reglas, el resultado es una sola alerta con la suma de puntos y la lista de reglas. Así el seguimiento (pendiente, en revisión, atendida, cerrada) tiene un solo lugar.

**Formularios y DTO aparte de las entidades.** `UsuarioForm`, `EventoForm` y el resto reciben lo que envía el frontend y llevan las validaciones de Jakarta. Hacia afuera, la API responde con DTO (`UsuarioDto`, `AlertaDto`…), no con entidades JPA. Así el JSON nunca incluye la contraseña cifrada y una entidad con relaciones perezosas no rompe la serialización.

**Backend y frontend separados.** El backend expone una API REST y no sabe nada de pantallas; el frontend es una aplicación React que se puede compilar y desplegar por su cuenta. Las listas de valores (tipos de evento, estados, roles) las sirve `GET /api/catalogos`, para no repetir los enums en JavaScript.

**`open-in-view` desactivado.** Hibernate no mantiene una conexión abierta mientras se arma la respuesta. Las lecturas necesarias ocurren dentro del servicio, en una transacción. El detalle de una alerta, por ejemplo, carga las reglas antes de salir del servicio, y el panel usa un DTO de alerta sin reglas.

**Autenticación con JWT.** `POST /api/auth/login` valida usuario y contraseña (BCrypt) y devuelve un token firmado con HS256 que dura 120 minutos y lleva el usuario y su rol. Spring Security lo valida en cada petición y no crea sesión en el servidor (`STATELESS`), por eso CSRF está desactivado: sin cookies de sesión no hay ataque CSRF que prevenir. Solo `/api/auth/login` es público. Sin token o con uno inválido o vencido la respuesta es 401; con token pero sin el rol adecuado, 403. Cerrar sesión borra el token en el navegador. Como el token es autónomo, desactivar un usuario no lo expulsa hasta que su token venza. El secreto de firma se define con `CYBERGUARD_JWT_SECRET`.

**El token vive en `sessionStorage`.** Se borra al cerrar la pestaña y no se envía solo con cada petición. A cambio, un script malicioso en la página podría leerlo, igual que con `localStorage`; por eso el frontend no inserta HTML sin escapar.

**CORS.** El backend solo acepta peticiones de los orígenes de `CYBERGUARD_CORS_ORIGENES` (por defecto `http://localhost:5173`). En desarrollo Vite reenvía `/api` al backend y el navegador ve un solo origen.

**Los CSV se descargan con `fetch`.** Un enlace normal no puede enviar la cabecera `Authorization`, así que el frontend pide el archivo con el token y lo guarda desde un `Blob`.

**MySQL de Laragon o XAMPP en local y H2 solo en pruebas.** La URL crea la base `cyberguard` si no existe. Hibernate no lee la metadata JDBC de MariaDB (el motor que trae XAMPP), porque esa lectura falla con la columna `RESERVED`; por eso `allow_jdbc_metadata_access` está en falso y el dialecto se indica a mano.

**Configuración en `backend/.env`.** La conexión (`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`), el puerto (`SERVER_PORT`), el secreto JWT y los orígenes CORS se leen de `backend/.env` con la dependencia `springboot3-dotenv`. `application.properties` solo referencia esas variables, con valores por defecto de laboratorio. Así cada integrante usa su propia contraseña de MySQL sin tocar un archivo versionado. Se sube `.env.example` como plantilla; `.env` queda fuera del repositorio. Detalle en `conexion.md`.

**Identificadores con nombre propio.** La clave primaria de cada tabla se llama `id_<tabla>` (`id_usuarios`, `id_dispositivos`, `id_eventos`, `id_reglas`, `id_alertas`) y en Java `id<Tabla>` (`idDispositivos`…). El mismo nombre llega al JSON y al frontend. Como `ddl-auto=update` no renombra columnas, una base creada antes del cambio hay que recrearla (ver `conexion.md`).

**Skills de asistentes versionadas en el repositorio.** El equipo programa con Claude Code y Cursor. Para que los dos reciban la misma guía sobre Spring Boot, Spring Security, pruebas, React y MySQL, el repositorio trae seis skills de skills.sh (`.claude/skills/`, `.agents/skills/`, `skills-lock.json`), instaladas como copia y no como enlace porque los enlaces simbólicos fallan en Windows sin permisos de administrador. Son guías genéricas, así que `CLAUDE.md` tiene prioridad cuando chocan con una convención del proyecto; los choques conocidos están en `AGENTS.md`.

**Datos de demostración solo si la tabla está vacía.** Reiniciar la aplicación no duplica usuarios ni eventos. Los eventos de ejemplo se registran con `EventoService`, así el panel de la primera visita ya muestra el circuito completo.

**CSV con punto y coma y marca BOM.** Los reportes se abren en Excel con la configuración habitual en español, y el texto conserva tildes.

## Pruebas

`mvnw.cmd test`, dentro de `backend/`, ejecuta la suite contra H2 (**62** pruebas):

- `ClasificadorRiesgoTest`: cortes de puntuación y tope 0–100.
- `MotorAnalisisTest` y `ReglasDeteccionTest`: motor y cada regla (caso que cumple y que no).
- `EventoRepositoryTest`: historial por IP, límites de ventana y exclusión de eventos posteriores.
- `EventoFormTest`, `DispositivoFormTest`, `UsuarioFormTest`: validaciones de Jakarta.
- `EventoServiceTest`, `UsuarioServiceTest`, `AlertaServiceTest`, `FechasTest`: reglas de negocio.
- `DispositivoTest`: última actividad que solo avanza.
- `SeguridadAccesoTest`: login y token, 401/403/400/404 y recorrido de registro de evento con serialización JSON.

En el frontend, `npm test` ejecuta Vitest + Testing Library (api, auth, rutas protegidas, login, utilidades y piezas de UI). `npm run build` comprueba que compila.

## Cómo arrancarla

Hace falta JDK 21 o superior, Node.js 20 o superior y MySQL de Laragon o XAMPP en marcha. La primera vez se copia `backend/.env.example` a `backend/.env`; si `root` tiene contraseña, se escribe en `DB_PASSWORD`. Se levantan dos procesos, cada uno en su terminal:

```bat
cd backend
mvnw.cmd spring-boot:run
```

```bat
cd frontend
npm install
npm run dev
```

La API queda en http://localhost:8080 y la aplicación en http://localhost:5173. Se entra por el 5173.
