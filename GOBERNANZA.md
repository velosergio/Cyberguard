# Gobernanza de CyberGuard

Este documento fija cómo se toman las decisiones, cómo se trabaja con el repositorio y qué límites no se negocian en CyberGuard. Aplica a todos los integrantes y a cualquier herramienta, humana o asistida por IA, que modifique el código o la documentación.

Proyecto académico de la asignatura **Programación avanzada**, programa de Ingeniería de Sistemas, Corporación Unificada Nacional de Educación Superior (CUN). Docente: Faber Alemán.

## 1. Alcance y límites que no se negocian

CyberGuard registra a mano eventos de una red **autorizada**, los analiza con reglas y genera alertas. Ninguna contribución puede cambiar eso:

- **No** se agregan funciones que capturen tráfico, escaneen redes o equipos, o bloqueen conexiones.
- Solo se usan redes, dispositivos y datos sobre los que el equipo tiene autorización.
- Los datos de prueba y de demostración son ficticios. No se cargan IP, MAC, nombres ni correos reales de terceros.
- Una alerta es una señal para que una persona la revise, no un dictamen de ataque. Los textos de la interfaz y de la documentación lo dejan claro.

Una propuesta que toque estos límites se rechaza en la revisión, aunque funcione.

## 2. Equipo y roles

El trabajo se reparte **por capa técnica**: cada integrante responde por una capa del sistema.

| Integrante | Rol Scrum | Área | Archivos que le corresponden |
| --- | --- | --- | --- |
| Sergio Esteban Veloza Gonzalez | Product Owner | Backend y motor de análisis | `backend/.../service/`, `analysis/`, `domain/`, `repository/`, `web/` (salvo seguridad), `DataInitializer` |
| Luifer Andres Hernandez Lambraño | Scrum Master | Seguridad | `backend/.../config/` (`SecurityConfig`, `JwtConfig`, `TokenService`, `UsuarioDetailsService`), `AuthController`, CORS, `frontend/src/auth.jsx`, `RutaProtegida.jsx` |
| Marcos Antonio Pacheco Bautista | Desarrollador | Frontend | `frontend/src/` (salvo los archivos de seguridad anteriores) |
| Rafael Antonio Rodríguez Gamarra | Desarrollador | Documentación y pruebas | `Docs/`, `README.md`, `backend/src/test/`, plan de pruebas y pruebas manuales por rol |

### Reglas del reparto

- **Responsable no es exclusivo.** Cualquiera puede proponer un cambio en cualquier capa; el responsable de esa capa lo revisa.
- **Quién revisa un PR:** el responsable de la capa que toca el cambio. Si el autor es el propio responsable, revisa otro integrante. Si el cambio toca varias capas, aprueban todos los responsables involucrados.
- **Cambios de contrato API** (rutas, campos de DTO o form, códigos de respuesta): los aprueban Sergio (backend) y Marcos (frontend). Si además cambian permisos, también Luifer.
- **Pruebas:** cada desarrollador escribe las pruebas de su propio cambio. Rafael define qué hay que probar, mantiene la estrategia de pruebas y revisa que la cobertura acompañe cada entrega.
- **Documentación:** quien cambia el código actualiza el documento afectado (ver sección 9). Rafael revisa la coherencia y redacta el documento de cada entrega.
- **Conocimiento compartido:** antes de cada entrega, cada responsable explica su capa al resto en la reunión de revisión, para que todos puedan sustentar el sistema completo (ver sección 8).

El reparto se revisa en la planeación de cada entrega; un cambio se registra editando esta tabla en un PR.

### Qué hace cada rol Scrum

- **Product Owner:** mantiene el backlog priorizado según los requerimientos (RF/RNF) del documento del proyecto y los criterios del docente.
- **Scrum Master:** convoca las ceremonias, sigue los impedimentos y vigila que se cumpla este documento.
- **Desarrolladores:** todos los integrantes, incluidos los dos anteriores. Cada uno se asigna tareas del tablero.
- **Docente:** es el interesado principal. Sus observaciones en cada entrega se convierten en elementos del backlog.

## 3. Metodología

- **Scrum por sprints.** Cada entrega académica cierra uno o más sprints.
- **Tablero:** Trello o GitHub Projects. Toda tarea tiene una tarjeta antes de empezar a programarla.
- **Ceremonias:** planeación al inicio del sprint, seguimiento corto durante el sprint, revisión con demostración del sistema funcionando y retrospectiva al cierre.

## 4. Toma de decisiones

| Tipo de decisión | Quién decide | Cómo queda registrada |
| --- | --- | --- |
| Alcance, requerimientos, prioridades | Product Owner con el equipo; el docente tiene la última palabra | Backlog y documento del proyecto |
| Arquitectura, tecnologías, dependencias nuevas | Consenso del equipo; si no hay acuerdo, mayoría simple | Sección «Decisiones de diseño» de `Docs/GUIA.md` |
| Implementación dentro de una tarea | Quien la desarrolla, sujeta a revisión | Pull request |
| Calibración de reglas (puntos, umbrales, ventanas) | Equipo, con datos de prueba que la justifiquen | PR con el escenario de prueba en `MotorAnalisisTest` |
| Cambios a este documento | Todo el equipo | PR aprobado por la mayoría de los integrantes |

Una decisión de arquitectura se documenta con el problema, la opción elegida y por qué, siguiendo el estilo de las entradas que ya hay en `Docs/GUIA.md`.

## 5. Flujo de trabajo con Git

- `main` siempre compila y pasa las pruebas. Nadie hace push directo a `main`.
- Se trabaja en ramas cortas que salen de `main`:
  - `feature/<descripcion-corta>` para funciones nuevas
  - `fix/<descripcion-corta>` para correcciones
  - `docs/<descripcion-corta>` para documentación
- Cada rama se integra con un **pull request** aprobado por al menos **otro integrante** distinto del autor, según las reglas de revisión de la sección 2.
- **Commits** en español, en modo imperativo, y cada uno con un solo propósito. Ejemplo: `Agregar regla de horario no laboral`.
- Antes de abrir el PR, la rama se actualiza con `main` y se resuelven los conflictos.
- No se suben al repositorio archivos generados (`target/`, `node_modules/`, `dist/`), credenciales ni archivos `.env`.

## 6. Definición de terminado

Una tarea está terminada cuando se cumple todo esto:

1. `.\mvnw.cmd test` pasa en `backend/`.
2. `npm run build` termina sin errores en `frontend/`.
3. El cambio respeta la arquitectura por capas y los principios SOLID y DRY descritos en `CLAUDE.md`.
4. Si cambió el contrato HTTP o los permisos, `SeguridadAccesoTest` lo cubre. Si cambió el análisis o la clasificación, lo cubren `MotorAnalisisTest` o `ClasificadorRiesgoTest`.
5. La documentación afectada está al día (ver sección 9).
6. Otro integrante revisó y aprobó el PR.
7. Se probó a mano en el navegador con el rol que corresponde.

## 7. Revisión de código

Quien revisa comprueba como mínimo:

- **Alcance:** que el cambio no viole los límites de la sección 1.
- **Seguridad:** que todo endpoint nuevo tenga su regla de rol en `SecurityConfig`, que ningún controlador devuelva una entidad JPA y que las entradas se validen con un `Form`.
- **Capas:** que los controladores no tengan lógica de negocio y que el motor no consulte repositorios desde una `ReglaDeteccion`.
- **DRY:** que no se repitan enums, etiquetas, permisos ni manejo de errores que ya tienen una fuente única.
- **Pruebas y documentación:** lo que pide la definición de terminado.

Los comentarios del PR se responden o se resuelven antes de integrar. Un desacuerdo que no se cierra en el PR pasa a la siguiente reunión del equipo.

## 8. Uso de asistentes de IA

El equipo usa Claude Code y Cursor como apoyo, con estas reglas:

- **La responsabilidad es de quien integra el cambio.** El código generado se lee, se entiende y se prueba como cualquier otro, y pasa por la misma revisión.
- `CLAUDE.md` contiene las instrucciones para el asistente. Si cambia la arquitectura o una convención, se actualiza en el mismo PR.
- Los commits hechos con ayuda de IA llevan la línea `Co-Authored-By` que corresponda.
- No se pegan en un asistente credenciales, secretos ni datos reales de terceros.
- Lo que se entrega al docente debe explicarlo cualquier integrante del equipo.

## 9. Documentación

| Documento | Se actualiza cuando… |
| --- | --- |
| `README.md` | cambian los requisitos, los comandos para ejecutar o la configuración |
| `Docs/conexion.md` y `backend/.env.example` | cambia una variable de `.env` o la forma de conectarse a la base |
| `Docs/GUIA.md` | cambian la API, las pantallas, los roles, el motor o una decisión de diseño |
| `Docs/base-de-datos.md` | cambian las entidades o las columnas |
| `Docs/NORMALIZACION.md` | se agrega o quita una columna derivada o copiada, una clave o una restricción |
| `Docs/instalacion.md` | cambia el procedimiento de instalación o aparece un problema frecuente |
| `Docs/img/fuentes/*.mmd` | cambia lo que muestra un diagrama; se regenera el PNG |
| `CLAUDE.md` | cambia la arquitectura, un comando o una convención |
| Documento del proyecto | en cada entrega académica |

La documentación describe el sistema **tal como está construido**. Lo pendiente se marca como pendiente.

## 10. Seguridad

- Las credenciales y secretos de cada máquina van en `backend/.env`, que nunca se sube al repositorio. `backend/.env.example` se versiona y solo lleva valores de laboratorio.
- El secreto JWT por defecto (en `application.properties` y `.env.example`) sirve solo para el laboratorio. Fuera del equipo local se define un `CYBERGUARD_JWT_SECRET` propio.
- Las cuentas de demostración (`admin`, `analista`, `consulta`) existen solo para el laboratorio y la evaluación.
- Una vulnerabilidad encontrada se reporta al equipo por un canal privado, no en un issue público, y se corrige en una rama `fix/` antes de documentarla.
- Las dependencias se actualizan a propósito, en su propio PR. Las versiones de Spring las fija `spring-boot-starter-parent` y las del frontend, `package-lock.json`.

## 11. Entregas y versiones

- Cada entrega académica se marca con una etiqueta en `main`: `entrega-1`, `entrega-2`, `entrega-3`.
- Antes de etiquetar se cumple la definición de terminado sobre `main` y se actualiza el documento del proyecto.
- Lo que queda pendiente se registra en el backlog y en la sección de pendientes del documento del proyecto.

## 12. Cambios a este documento

Cualquier integrante puede proponer un cambio con un PR `docs/gobernanza-...`. Se integra cuando lo aprueba la mayoría del equipo.
