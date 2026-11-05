# Informe de pendientes — CyberGuard

Revisión del 5 de noviembre de 2026. Compara `PROYECTO CYBERGUARD.docx.md` (segunda entrega) con el código actual del repositorio.

## Estado de la primera revisión

Los 7 ítems ausentes y los 5 parciales de la primera entrega quedaron cubiertos en la segunda: actores y matriz de permisos (sección 7), diagramas (8), funcionalidades (12), avance (13), evidencias (14), pruebas (15), problemas (16) y pendientes (17).

## Actualización del documento a la arquitectura actual

El commit `299aacf` separó la aplicación en una API REST (Spring Boot + JWT) y un cliente React. El 5 de noviembre se actualizó `PROYECTO CYBERGUARD.docx.md` para describir esa versión:

- Introducción, justificación, objetivos (4.2), alcance y limitaciones (5): API y cliente, JWT, pruebas H2 y Vitest; limitación sobre la vigencia del token.
- Requerimientos (6.1 y 6.2): endpoints `/api/**`, fecha no futura en RF04, seguridad con JWT y CORS, Node.js y `.env` en portabilidad.
- Actores (7): matriz de permisos por endpoint de la API, respuestas 401/403 y protección de rutas del cliente.
- Diseño (8.1, 8.5, 8.8, 9.2, 9.3, 9.5, 9.6): cierre de sesión en el cliente, despliegue en dos procesos, identificadores `id_<tabla>`, ventana del historial con límite superior, decisiones de diseño (cliente y API separados, JWT, DTO) y rutas del cliente.
- Tecnologías (10) y arquitectura (11): React, React Router, Vite, Node.js, JWT, spring-dotenv; capas y estructura de `backend/` y `frontend/`.
- Funcionalidades (12.1, 12.4, 12.9), avance (13), pruebas (15) y conclusiones (18).
- Problemas (16): descarga de CSV con token, CORS, respuestas vacías de 401/403, ventana del historial, configuración en `.env` y corrección de `ultima_actividad` (D5).
- Pendientes (17): cobertura JaCoCo, ampliación del cliente, auditoría y calibración.
- Referencias (19): React, Vite, Vitest y RFC 7519.
- Evidencias (14): se mantiene `screenshots/` como evidencia del recorrido.

Las fuentes Mermaid de las figuras 2, 3, 5, 6, 9 y 10 también se actualizaron.

## Alineado el 5 de noviembre de 2026

- **Conteo de pruebas.** Documento actualizado a **62** del backend y **24** del cliente (secciones 4.2, 5, 11, 13, 15, 17 y 18).
- **Pruebas del cliente.** Suite Vitest documentada en 15.1.1; el pendiente de la sección 17 pasa a «ampliar» pantallas de negocio.
- **Capturas.** Se mantiene `screenshots/` sin nota de retoma.

## Pendiente para cerrar la segunda entrega

- **Imágenes de las figuras.** Ya regeneradas: los PNG de las figuras 2, 3, 5, 6, 9 y 10 corresponden a sus fuentes actualizadas. Para regenerarlas sin descargar el navegador de puppeteer, se puede pasar `-p` con un JSON que apunte a Edge (`{"executablePath":"C:/Program Files (x86)/Microsoft/Edge/Application/msedge.exe"}`).
- **Archivos exportados.** `PROYECTO CYBERGUARD.docx.pdf`, `Proyecto_CyberGuard.pdf` y las presentaciones `.pptx` se generaron con el texto anterior. Hay que volver a exportarlos desde el documento actualizado.

## Pendientes técnicos para la tercera entrega

Además de la tabla de la sección 17:

- **Vigencia del token.** Ya está documentada en 5.2 y en la sección 17. Falta decidir si se corrige: comprobar el estado del usuario en cada petición o acortar la vida del token.
- **Una consulta por evento.** `MotorAnalisis` consulta el historial una vez por cada regla activa; bastaría una consulta con la ventana más amplia.
- **Cobertura.** Medir con JaCoCo el backend y ampliar pruebas del cliente a formularios y listados.

## Ajustes de forma

El índice (CONTENIDO) y el Anexo A siguen coincidiendo con las secciones y figuras: no se agregaron ni quitaron secciones ni figuras.
