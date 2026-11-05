# Normalización de la base de datos de CyberGuard

Este documento revisa el esquema actual de `cyberguard` frente a las formas normales: qué cumple, dónde se aparta a propósito y qué riesgos deja cada decisión. Describe el esquema **tal como lo genera Hibernate** a partir de las entidades de `backend/src/main/java/com/cun/cyberguard/domain`, comprobado con `SHOW CREATE TABLE` sobre la base local. El detalle de cada columna está en `base-de-datos.md`.

## Resumen

| Tabla | 1FN | 2FN | 3FN | FNBC | Observación |
| --- | --- | --- | --- | --- | --- |
| `usuarios` | Sí | Sí | Sí | Sí | Dos claves candidatas extra: `usuario` y `correo` |
| `dispositivos` | Sí | Sí | Sí* | Sí* | *`ultima_actividad` es un dato derivado de `eventos` |
| `eventos` | Sí | Sí | Sí* | Sí* | *`direccion_ip` repite la IP del dispositivo cuando hay uno, como dato histórico |
| `reglas` | Sí | Sí | Sí | Sí | Clave candidata extra: `codigo` |
| `alertas` | Sí | Sí | **No** | **No** | Copia `dispositivo_id` y `fecha_hora` del evento, y guarda `nivel`, que se deduce de `puntuacion` |
| `alerta_regla` | Sí | Sí | Sí | Sí | Tabla de unión; también cumple 4FN |

En conjunto, el esquema está en **tercera forma normal**, salvo desnormalizaciones puntuales y deliberadas en `alertas` y dos datos derivados en `dispositivos` y `eventos`. Todas se explican abajo.

## Dependencias funcionales por tabla

Notación: `A → B` significa «conocer A determina B». La clave primaria de cada tabla es un `BIGINT AUTO_INCREMENT` sin significado (clave sustituta).

| Tabla | Clave primaria | Otras claves candidatas | Dependencias relevantes |
| --- | --- | --- | --- |
| `usuarios` | `id_usuarios` | `usuario`, `correo` (únicas) | `id_usuarios → nombre, usuario, correo, contrasena, rol, activo, creado_en` |
| `dispositivos` | `id_dispositivos` | `direccion_ip` (única) | `id_dispositivos → direccion_ip, direccion_mac, nombre, tipo, estado, fecha_registro, ultima_actividad` |
| `eventos` | `id_eventos` | — | `id_eventos → dispositivo_id, direccion_ip, tipo, puerto, fecha_hora, detalle, importancia` |
| `reglas` | `id_reglas` | `codigo` (única) | `id_reglas → codigo, nombre, descripcion, puntuacion, umbral, ventana_minutos, activa` |
| `alertas` | `id_alertas` | — | `id_alertas → evento_id, dispositivo_id, fecha_hora, puntuacion, nivel, estado, nota`; además `evento_id → dispositivo_id, fecha_hora` y `puntuacion → nivel` |
| `alerta_regla` | (`alerta_id`, `regla_id`) | — | Sin atributos fuera de la clave |

## Primera forma normal (1FN)

**Se cumple en todas las tablas.**

- **Valores atómicos:** cada columna guarda un solo valor. Ninguna guarda listas separadas por comas ni JSON.
- **Sin grupos repetidos:** una alerta puede citar varias reglas, pero no hay columnas `regla1`, `regla2`, … ni un campo con códigos concatenados. Esa relación de muchos a muchos se sacó a la tabla `alerta_regla`.
- **Clave primaria en todas las tablas.** `alerta_regla` tiene la clave compuesta `PRIMARY KEY (alerta_id, regla_id)`. Hibernate la crea porque la colección es un `Set` (`Alerta.reglas`), y eso impide citar dos veces la misma regla en una alerta.
- **Dominios definidos:** los enums se guardan por nombre (`EnumType.STRING`). En MySQL, Hibernate los crea como columnas `ENUM(...)` nativas, por ejemplo `nivel enum('ALTO','BAJO','CRITICO','MEDIO')`. Así la base rechaza valores fuera del dominio.

## Segunda forma normal (2FN)

**Se cumple en todas las tablas.**

La 2FN prohíbe que un atributo dependa solo de una parte de una clave compuesta. Las cinco tablas de entidades tienen una clave de una sola columna, así que no puede haber dependencias parciales. La única clave compuesta es la de `alerta_regla`, y esa tabla no tiene columnas fuera de la clave.

## Tercera forma normal (3FN)

La 3FN prohíbe las dependencias transitivas: que un atributo dependa de otro atributo que no es clave.

### Lo que cumple

- **`usuarios`:** el rol se guarda como valor de un enum. Sus etiquetas («Administrador») viven en Java (`Rol.getEtiqueta()`) y no se repiten en cada fila.
- **`reglas`:** guarda solo los parámetros de cada regla. La condición de detección vive en código (`ReglaDeteccion`) y se une por `codigo`, sin duplicar lógica en la tabla.
- **Etiquetas fuera de la base:** ninguna tabla guarda textos como «Acceso a puerto» o «En revisión». Se guarda el código del enum, y la etiqueta la sirve `GET /api/catalogos`.
- **Datos de otra entidad por referencia:** `eventos` no copia el nombre, el tipo ni la MAC del dispositivo; se llega a ellos por `dispositivo_id`.

### Desnormalizaciones deliberadas

| # | Dónde | Dependencia transitiva | Por qué se mantiene | Riesgo |
| --- | --- | --- | --- | --- |
| D1 | `alertas.dispositivo_id` | `id_alertas → evento_id → dispositivo_id` | `AlertaService` lo copia del evento al crear la alerta. Así los filtros y el detalle de la alerta llegan al equipo sin pasar por el evento. | Bajo. Los eventos no se editan (no hay `PUT /api/eventos`), así que la copia no puede quedar desfasada. |
| D2 | `alertas.fecha_hora` | `id_alertas → evento_id → fecha_hora` | Se copia del evento para filtrar y ordenar las alertas por fecha sin un `JOIN`. | Bajo, por la misma razón. |
| D3 | `alertas.nivel` | `id_alertas → puntuacion → nivel` | El nivel se calcula con los cortes de `ClasificadorRiesgo` (0–20, 21–50, 51–80, 81–100). Se guarda para filtrar por nivel en SQL y para conservar el resultado del análisis aunque los cortes cambien en el código. | Medio. Si se cambian los cortes, las alertas viejas conservan el nivel anterior. Es el comportamiento buscado (es una foto del análisis), pero conviene saberlo. |

`alertas.puntuacion` **no** es una desnormalización: es la suma de los puntos de las reglas **en el momento del análisis**. Como el administrador puede cambiar después la puntuación de una regla, no se podría recalcular con los datos actuales. Es un dato histórico propio de la alerta.

### Datos derivados o repetidos fuera de `alertas`

| # | Dónde | Qué repite | Por qué se mantiene | Riesgo |
| --- | --- | --- | --- | --- |
| D4 | `eventos.direccion_ip` | Cuando `dispositivo_id` no es nulo, es la IP de ese dispositivo (`EventoService` la toma de él). | Es la IP **en el momento del hecho**. Hace falta cuando el evento no tiene dispositivo y la consulta del historial del motor filtra por IP. Si el dispositivo cambia de IP después, el evento debe conservar la original. | Ninguno funcional; es un hecho histórico, no una copia. |
| D5 | `dispositivos.ultima_actividad` | Se puede deducir de `eventos` (la fecha del evento más reciente de ese dispositivo). | Evita una subconsulta `MAX(fecha_hora)` en cada listado del inventario. | Bajo. Antes había una anomalía de actualización: un evento con fecha pasada hacía retroceder `ultima_actividad`. Se corrigió con `Dispositivo.registrarActividad`, que solo deja avanzar la fecha (lo cubre `EventoServiceTest`). Sigue siendo un dato derivado: un evento borrado o cargado directo por SQL no la recalcula. |

## Forma normal de Boyce-Codd (FNBC)

La FNBC exige que todo determinante sea una clave candidata.

- **Cumplen:** `usuarios`, `dispositivos`, `eventos`, `reglas` y `alerta_regla`. Sus determinantes son la clave primaria o una clave candidata única (`usuario`, `correo`, `direccion_ip`, `codigo`).
- **No cumple:** `alertas`, por D1, D2 y D3, ya que `evento_id` y `puntuacion` determinan otras columnas sin ser claves.
- **Dependencia no declarada:** `dispositivos.direccion_mac` no es única. Si en la realidad una MAC identifica un solo equipo, existiría `direccion_mac → id_dispositivos` sin que la base lo garantice. Hoy no se trata como clave porque la MAC es opcional.

## Cuarta y quinta forma normal (4FN, 5FN)

**Se cumplen.** La única relación de varios valores es alerta ↔ reglas, y está aislada en `alerta_regla`. Ninguna tabla mezcla dos hechos de varios valores independientes entre sí. Tampoco hay relaciones ternarias que se puedan descomponer.

## Integridad referencial

| Relación | Restricción en la base |
| --- | --- |
| `eventos.dispositivo_id → dispositivos` | Clave foránea; acepta nulos |
| `alertas.evento_id → eventos` | Clave foránea; `NOT NULL` |
| `alertas.dispositivo_id → dispositivos` | Clave foránea; acepta nulos |
| `alerta_regla.alerta_id → alertas`, `alerta_regla.regla_id → reglas` | Claves foráneas que forman la clave primaria |

Ninguna relación declara borrado en cascada. Un dispositivo con eventos, o un evento con alerta, no se puede borrar mientras tenga hijos.

## Reglas de negocio que no garantiza la base

Estas reglas existen, pero solo las hace cumplir el código Java. Una inserción directa por SQL podría romperlas.

| Regla | Dónde se hace cumplir hoy | Cómo se podría llevar a la base |
| --- | --- | --- |
| Una sola alerta por evento | `EventoService.registrar` llama una sola vez a `AlertaService.crear`, al registrar el evento; no hay ninguna comprobación explícita | `UNIQUE (evento_id)` en `alertas` |
| `puerto` obligatorio si `tipo = 'ACCESO_PUERTO'` | `EventoService.registrar` | `CHECK (tipo <> 'ACCESO_PUERTO' OR puerto IS NOT NULL)` |
| `puerto` entre 1 y 65535 | `@Min`/`@Max` en `EventoForm` y `min`/`max` en el cliente | `CHECK (puerto BETWEEN 1 AND 65535)` |
| `puntuacion` entre 0 y 100 | `ClasificadorRiesgo.limitar` | `CHECK (puntuacion BETWEEN 0 AND 100)` |
| `alertas.dispositivo_id` igual al del evento | `AlertaService` | Eliminar la columna (ver D1) |
| Al menos un administrador activo | `UsuarioService.validarAdministradorRestante` | No se expresa con una restricción simple |

## Decisiones de modelado relacionadas

- **Claves sustitutas:** todas las tablas usan un identificador autoincremental en lugar de una clave natural (la IP, el usuario o el código de regla). Las claves naturales se protegen con índices únicos, así que se pueden corregir (por ejemplo, la IP de un dispositivo) sin tocar las claves foráneas.
- **Enums en lugar de tablas de catálogo:** tipos, estados, niveles, importancias y roles son enums de Java guardados como `ENUM` de MySQL, no tablas aparte con su propia clave. Es válido en 3FN porque cada valor es atómico y su etiqueta no se repite en las filas. A cambio, agregar un valor exige cambiar el código. Además, **`ddl-auto=update` no modifica una columna `ENUM` que ya existe**: en una base existente hay que ejecutar un `ALTER TABLE ... MODIFY COLUMN` a mano o recrear la base.
- **Usuarios sin relación con los datos:** ninguna tabla apunta a `usuarios`, así que no hay forma de saber quién registró un evento o cambió una alerta. No es un problema de normalización sino de alcance: la auditoría figura como pendiente de la tercera entrega.

## Mejoras posibles

Ordenadas de menor a mayor impacto:

1. ~~**Corregir D5**~~ (hecho): `ultima_actividad` solo avanza si la fecha del evento nuevo es posterior a la que ya tiene el dispositivo.
2. **Llevar a la base las reglas de la tabla anterior:** `UNIQUE (evento_id)` en `alertas` y las restricciones `CHECK` de `puerto` y `puntuacion`.
3. **Normalizar `alertas` del todo:** quitar `dispositivo_id` y `fecha_hora` y obtenerlos del evento con un `JOIN`. Hay que revisar las consultas de `AlertaRepository`, `PanelService` y `ReporteService`.
4. **Auditoría:** agregar `registrado_por` en `eventos` y `actualizado_por` en `alertas`, como claves foráneas a `usuarios`.

Cada mejora cambia el esquema. Como `ddl-auto=update` no elimina columnas ni agrega todas las restricciones, aplicarlas en una base existente exige recrearla (ver `conexion.md`) o ejecutar el SQL a mano. Además, hay que actualizar `base-de-datos.md`, este documento y la Figura 5.
