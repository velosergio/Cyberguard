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

El menú y las direcciones web se controlan juntos: Spring Security decide si la ruta está permitida, y las plantillas ocultan los botones que ese rol no debe usar.

## Pantallas

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

## Cómo está organizado el código

Todo el código Java vive en `src/main/java/com/cun/cyberguard`. Cada carpeta es una responsabilidad:

```text
CyberguardApplication.java     arranque de Spring Boot
config/                        seguridad, carga de datos de demostración
web/                           pantallas: recibe el formulario y elige la vista
web/form/                      datos que llegan del navegador, con validaciones
service/                       reglas de negocio y transacciones
analysis/                      motor que decide si un evento es sospechoso
analysis/reglas/               una clase por cada tipo de detección
domain/                        tablas: Usuario, Dispositivo, Evento, Regla, Alerta
domain/enums/                  valores fijos (rol, tipo de evento, nivel de riesgo…)
repository/                    consultas a la base de datos
```

Las pantallas están en `src/main/resources/templates` (Thymeleaf) y los estilos en `src/main/resources/static`. Las pruebas están en `src/test` y usan una base H2 en memoria, así que no necesitan MySQL.

## Arquitectura

Es una aplicación web de **una sola pieza**, organizada en capas. El navegador pide una página, Spring Boot responde con HTML ya armado. No hay una API aparte ni una aplicación de frontend independiente.

```text
Navegador
    │
    ▼
Interfaz          web/ + templates/     formularios, listas, panel
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
MySQL (XAMPP)     base cyberguard
```

Cada capa habla con la de abajo a través de clases concretas de Spring (inyección por constructor). Un controlador no escribe SQL ni decide si un escaneo de puertos es sospechoso: llama a un servicio, y el servicio llama al motor cuando hace falta.

### Qué pasa al registrar un evento

1. `EventoController` recibe el formulario y comprueba que los campos sean válidos.
2. `EventoService.registrar` exige un puerto cuando el tipo es «acceso a puerto», busca el dispositivo por el seleccionado o por la IP, y guarda el evento.
3. Si el dispositivo existe y no está marcado como desconocido, actualiza su última actividad.
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

**Formularios aparte de las entidades.** `UsuarioForm`, `EventoForm` y el resto reciben lo que escribe el usuario y llevan las validaciones de Jakarta. La entidad JPA no se rellena directamente desde el HTML. Eso evita, por ejemplo, que un formulario de edición de usuario reescriba la contraseña cifrada por accidente.

**Thymeleaf en el servidor.** HTML, CSS y un poco de JavaScript bastan para las listas, filtros y formularios. Los permisos se reflejan en la misma plantilla con Spring Security, sin mantener otra aplicación.

**`open-in-view` desactivado.** Hibernate no mantiene una conexión abierta mientras se dibuja el HTML. Las lecturas que la pantalla necesita ocurren dentro del servicio, en una transacción. El detalle de una alerta, por ejemplo, carga las reglas antes de salir del servicio.

**Seguridad por URL, con formulario de login.** Las rutas públicas son el login, los estilos, el script y la página de error. El resto exige sesión. Crear usuarios, editar reglas, registrar dispositivos o eventos, cambiar alertas y descargar reportes piden el rol que corresponde. Quien no tiene permiso ve `/acceso-denegado`.

**MySQL de XAMPP en local y H2 solo en pruebas.** La URL crea la base `cyberguard` si no existe. Hibernate no lee la metadata JDBC de MariaDB (el motor que trae XAMPP), porque esa lectura falla con la columna `RESERVED`; por eso `allow_jdbc_metadata_access` está en falso y el dialecto se indica a mano.

**Datos de demostración solo si la tabla está vacía.** Reiniciar la aplicación no duplica usuarios ni eventos. Los eventos de ejemplo se registran con `EventoService`, así el panel de la primera visita ya muestra el circuito completo.

**CSV con punto y coma y marca BOM.** Los reportes se abren en Excel con la configuración habitual en español, y el texto conserva tildes.

**El menú no usa `#request`.** Thymeleaf 3.1 ya no expone la petición HTTP dentro de la plantilla. `NavegacionAdvice` publica la ruta actual para marcar la sección activa.

## Pruebas

`mvnw.cmd test` ejecuta tres grupos, todos contra H2:

- `ClasificadorRiesgoTest`: los cortes de puntuación (bajo, medio, alto, crítico) y el tope 0–100.
- `MotorAnalisisTest`: que las reglas de ejemplo disparan la detección esperada.
- `SeguridadAccesoTest`: que cada rol entra solo a lo que le corresponde.

## Cómo arrancarla

Hace falta JDK 21 o superior y MySQL de XAMPP en marcha. Si `root` tiene contraseña, se cambia en `src/main/resources/application.properties`.

```bat
mvnw.cmd spring-boot:run
```

La aplicación queda en http://localhost:8080.
