# Base de datos de CyberGuard

CyberGuard guarda usuarios, el inventario de equipos, los eventos registrados a mano, el catálogo de reglas y las alertas que produce el análisis. El motor es **MySQL** (en XAMPP suele ser MariaDB) y el esquema lo crea Hibernate a partir de las entidades de `src/main/java/com/cun/cyberguard/domain`.

La base se llama `cyberguard`. La URL de conexión la crea si todavía no existe. La zona horaria de la aplicación y de JDBC es `America/Bogota`.

## Cómo nace el esquema

No hay un script SQL en el repositorio. Al arrancar, `spring.jpa.hibernate.ddl-auto=update` compara las entidades con las tablas y añade lo que falte. Los identificadores son `BIGINT` autoincrementales (`GenerationType.IDENTITY`). Los nombres de columna pasan de camelCase a snake_case: `fechaHora` queda como `fecha_hora`.

Los valores de los enums se guardan como texto (`EnumType.STRING`): `ADMINISTRADOR`, `ALTO`, `PENDIENTE`. Un cambio de orden en el código Java deja intactos los datos ya almacenados.

Las pruebas automáticas usan una base **H2 en memoria** con modo MySQL (`application-test.properties`, `ddl-auto=create-drop`). MySQL puede estar apagado mientras corren los tests.

XAMPP trae MariaDB. Hibernate 6 no puede leer su metadata JDBC (falla en la columna `RESERVED`), así que el dialecto se fija a mano (`MySQLDialect`) y `allow_jdbc_metadata_access` queda en falso.

## Idea del modelo

El inventario y el historial viven separados a propósito.

Un **dispositivo** es un equipo de la red autorizada, identificado por su IP. Un **evento** es lo que alguien anotó: un intento de conexión, un acceso a un puerto, un equipo desconocido. El evento siempre lleva su propia IP. El enlace al dispositivo es opcional, porque un evento puede hablar de una IP que todavía no está en el inventario.

Cuando el análisis encuentra detecciones, nace **una alerta por evento**. Si en ese momento se cumplen varias reglas, los puntos se suman y la alerta queda ligada a todas ellas. La puntuación y el nivel se guardan en la alerta: son el resultado de aquel análisis, y un cambio posterior de la regla no los reescribe.

La tabla **reglas** guarda los números que el laboratorio puede afinar (puntuación, umbral, ventana y si está activa). La condición de cada regla vive en Java y se reconoce por el `codigo`.

Los **usuarios** sirven para entrar y para el rol. No hay clave foránea hacia eventos ni alertas: el esquema no registra quién cargó un evento ni quién cambió el estado de una alerta.

```text
usuarios          (cuentas, independientes del resto)

dispositivos 1 ──< eventos 1 ──< alertas >──< reglas
                      │              │
                      └──────────────┘
                    la alerta también apunta al dispositivo,
                    copiado del evento en el momento de crearla
```

## Tablas

### `usuarios`

Cuenta de acceso. La contraseña se guarda cifrada con BCrypt (el hash cabe en 100 caracteres). `usuario` y `correo` son únicos.

| Columna | Obligatorio | Qué guarda |
| --- | --- | --- |
| `id` | sí | Identificador |
| `nombre` | sí | Nombre visible, hasta 80 caracteres |
| `usuario` | sí | Nombre de inicio de sesión, único, hasta 40 |
| `correo` | sí | Correo, único, hasta 120 |
| `contrasena` | sí | Hash BCrypt |
| `rol` | sí | `ADMINISTRADOR`, `ANALISTA` o `CONSULTA` |
| `activo` | sí | Si puede iniciar sesión. Por defecto, sí |
| `creado_en` | sí | Fecha de alta |

Un usuario inactivo existe en la tabla y no puede entrar.

### `dispositivos`

Inventario de la red autorizada. La IP es única: dos equipos no comparten dirección.

| Columna | Obligatorio | Qué guarda |
| --- | --- | --- |
| `id` | sí | Identificador |
| `direccion_ip` | sí | IPv4, única, hasta 45 caracteres |
| `direccion_mac` | no | MAC, hasta 17 caracteres (`AA:BB:CC:DD:EE:FF`) |
| `nombre` | sí | Nombre del equipo, hasta 80 |
| `tipo` | sí | Computador, teléfono, servidor, impresora, cámara u otro |
| `estado` | sí | `ACTIVO`, `INACTIVO` o `DESCONOCIDO` |
| `fecha_registro` | sí | Cuándo se dio de alta |
| `ultima_actividad` | no | Fecha del último evento ligado a este equipo |

`ultima_actividad` se actualiza al registrar un evento de ese dispositivo, salvo que su estado sea `DESCONOCIDO`.

### `eventos`

Historial. Cada fila es un registro cargado desde la pantalla o por los datos de demostración.

| Columna | Obligatorio | Qué guarda |
| --- | --- | --- |
| `id` | sí | Identificador |
| `dispositivo_id` | no | Equipo del inventario, si se conoce |
| `direccion_ip` | sí | IP del hecho, hasta 45 caracteres |
| `tipo` | sí | Intento de conexión, acceso a puerto, actividad inusual, dispositivo desconocido u otro |
| `puerto` | no | Número de puerto. Hace falta cuando el tipo es acceso a puerto |
| `fecha_hora` | sí | Momento del hecho |
| `detalle` | no | Texto libre, hasta 500 caracteres |
| `importancia` | sí | `BAJA`, `MEDIA` o `ALTA` |

Si el formulario elige un dispositivo, la IP del evento se toma de ese equipo. Si no, se busca la IP en el inventario: si existe, el evento queda ligado; si no, `dispositivo_id` queda vacío y la IP se guarda igual. Así se pueden anotar equipos que todavía no están dados de alta, y las reglas que miran el historial de una IP siguen funcionando.

### `reglas`

Catálogo ajustable. El `codigo` es único y coincide con la estrategia Java (`CodigosRegla`). El administrador puede cambiar puntuación, umbral, ventana y el interruptor `activa`. El código, el nombre y la descripción se cargan con los datos iniciales.

| Columna | Obligatorio | Qué guarda |
| --- | --- | --- |
| `id` | sí | Identificador |
| `codigo` | sí | Clave estable, única, hasta 40 caracteres |
| `nombre` | sí | Nombre visible, hasta 80 |
| `descripcion` | sí | Qué detecta, hasta 300 |
| `puntuacion` | sí | Puntos que suma si se cumple |
| `umbral` | sí | Cuántos hechos hacen falta dentro de la ventana |
| `ventana_minutos` | sí | Periodo que mira la regla. Cero significa que no usa ventana |
| `activa` | sí | Si el motor la evalúa. Por defecto, sí |

Valores iniciales, solo si la tabla está vacía:

| Código | Puntos | Umbral | Ventana |
| --- | --- | --- | --- |
| `DISPOSITIVO_NO_REGISTRADO` | 10 | 1 | 0 min |
| `ACTIVIDAD_INUSUAL` | 10 | 1 | 60 min |
| `REPETICION_CONEXIONES` | 15 | 5 | 10 min |
| `MULTIPLES_INTENTOS` | 20 | 8 | 5 min |
| `ESCANEO_PUERTOS` | 25 | 5 | 2 min |
| `ACUMULACION_SOSPECHOSA` | 30 | 4 | 30 min |

El historial que consultan las reglas incluye el evento recién guardado. Un umbral de 5 cuenta cinco eventos en la ventana, con el actual entre ellos. Si varias reglas se cumplen, los puntos se suman y el tope guardado es 100.

### `alertas`

Resultado del análisis y su seguimiento. Solo se inserta una fila cuando hubo al menos una detección.

| Columna | Obligatorio | Qué guarda |
| --- | --- | --- |
| `id` | sí | Identificador |
| `evento_id` | sí | Evento que disparó el análisis |
| `dispositivo_id` | no | Equipo del evento en ese momento |
| `fecha_hora` | sí | La misma fecha del evento |
| `puntuacion` | sí | Suma limitada entre 0 y 100 |
| `nivel` | sí | `BAJO`, `MEDIO`, `ALTO` o `CRITICO` |
| `estado` | sí | Seguimiento. Al crearse, `PENDIENTE` |
| `nota` | no | Comentario de quien revisa, hasta 500 caracteres |

El nivel sale de la puntuación ya guardada:

| Puntuación | Nivel |
| --- | --- |
| 0 a 20 | Bajo |
| 21 a 50 | Medio |
| 51 a 80 | Alto |
| 81 a 100 | Crítico |

Los estados de seguimiento son `PENDIENTE`, `EN_REVISION`, `ATENDIDA` y `CERRADA`.

### `alerta_regla`

Tabla de unión de muchos a muchos. Una alerta puede citar varias reglas, y una regla puede aparecer en muchas alertas.

| Columna | Qué guarda |
| --- | --- |
| `alerta_id` | Alerta |
| `regla_id` | Regla que se cumplió en ese análisis |

El detalle de una alerta carga esta relación dentro de la transacción del servicio (`open-in-view` está desactivado, así que la pantalla no abre la conexión por su cuenta).

## Relaciones

| Desde | Hacia | Cardinalidad | Columna | Si falta el padre |
| --- | --- | --- | --- | --- |
| `eventos` | `dispositivos` | muchos a uno, opcional | `dispositivo_id` | El evento sigue existiendo con su IP |
| `alertas` | `eventos` | muchos a uno, obligatoria | `evento_id` | La alerta no se puede crear |
| `alertas` | `dispositivos` | muchos a uno, opcional | `dispositivo_id` | La alerta queda sin equipo |
| `alerta_regla` | `alertas` y `reglas` | muchos a muchos | `alerta_id`, `regla_id` | La unión exige las dos filas |

Hibernate crea las claves foráneas. Las entidades no declaran borrado en cascada: borrar un dispositivo que ya tiene eventos, o un evento que ya tiene alerta, lo impide la restricción de MySQL.

`usuarios` no apunta a ninguna otra tabla.

## Qué se consulta con más frecuencia

Las consultas viven en los repositorios de Spring Data.

- Dispositivos, por IP exacta (también para saber si una IP ya está inventariada).
- Eventos de una IP desde una fecha, ordenados de más antiguo a más reciente. Eso es el historial que miran las reglas.
- Eventos filtrados por IP parcial, tipo y rango de fechas.
- Alertas filtradas por nivel, estado y rango de fechas, con las reglas, el evento y el dispositivo ya cargados.
- Reglas activas, que son las únicas que evalúa el motor.
- Usuario por nombre de inicio de sesión.

Las columnas únicas (`usuario`, `correo`, `direccion_ip`, `codigo`) llevan índice único. El resto de filtros usa las columnas tal como están; no hay índices extra declarados en las entidades.

## Datos de demostración

`DataInitializer` corre al arrancar y solo escribe si la tabla correspondiente está vacía. Reiniciar la aplicación no duplica filas.

1. Tres usuarios: `admin`, `analista` y `consulta`, activos, con contraseña cifrada.
2. Las seis reglas de la tabla de arriba, todas activas.
3. Tres dispositivos: `PC-Laboratorio` (`192.168.10.11`), `Servidor-Academico` (`192.168.10.20`) y `Camara-Pasillo` (`192.168.10.30`).
4. Una serie de eventos de ejemplo, registrados con `EventoService`. Pasan por el mismo análisis que un registro manual, así que la primera visita ya tiene alertas.

## Dónde está cada pieza

| Pieza | Ruta |
| --- | --- |
| Entidades | `src/main/java/com/cun/cyberguard/domain` |
| Enums | `src/main/java/com/cun/cyberguard/domain/enums` |
| Consultas | `src/main/java/com/cun/cyberguard/repository` |
| Conexión y dialecto | `src/main/resources/application.properties` |
| Base de pruebas | `src/test/resources/application-test.properties` |
| Carga inicial | `src/main/java/com/cun/cyberguard/config/DataInitializer.java` |
