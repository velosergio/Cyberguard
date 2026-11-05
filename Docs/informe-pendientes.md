# Informe de pendientes — CyberGuard

Revisión del 5 de noviembre de 2026. Compara `PROYECTO CYBERGUARD.docx.md` (segunda entrega) con el código actual del repositorio.

## Estado de la primera revisión

Los 7 ítems ausentes y los 5 parciales de la primera entrega quedaron cubiertos en la segunda: actores y matriz de permisos (sección 7), diagramas (8), funcionalidades (12), avance (13), evidencias (14), pruebas (15), problemas (16) y pendientes (17).

## Actualización del documento a la arquitectura actual

El commit `299aacf` separó la aplicación en una API REST (Spring Boot + JWT) y un cliente React. El 5 de noviembre se actualizó `PROYECTO CYBERGUARD.docx.md` para describir esa versión:

- Introducción, justificación, objetivos (4.2), alcance y limitaciones (5): API y cliente, JWT en lugar de CSRF, 20 pruebas; nueva limitación sobre la vigencia del token.
- Requerimientos (6.1 y 6.2): endpoints `/api/**`, fecha no futura en RF04, seguridad con JWT y CORS, Node.js y `.env` en portabilidad.
- Actores (7): matriz de permisos por endpoint de la API, respuestas 401/403 y protección de rutas del cliente.
- Diseño (8.1, 8.5, 8.8, 9.2, 9.3, 9.5, 9.6): cierre de sesión en el cliente, despliegue en dos procesos, identificadores `id_<tabla>`, ventana del historial con límite superior, decisiones de diseño (cliente y API separados, JWT, DTO) y rutas del cliente.
- Tecnologías (10) y arquitectura (11): React, React Router, Vite, Node.js, JWT, spring-dotenv; capas y estructura de `backend/` y `frontend/`.
- Funcionalidades (12.1, 12.4, 12.9), avance (13), pruebas (15) y conclusiones (18).
- Problemas (16): el problema de Thymeleaf se reemplazó por el de la descarga de CSV con token, y se agregaron CORS, respuestas vacías de 401/403, la ventana del historial y la configuración en `.env`.
- Pendientes (17): pruebas del cliente y revocación de sesiones.
- Referencias (19): sin Thymeleaf; con React, Vite y RFC 7519.

Las fuentes Mermaid de las figuras 2, 3, 5, 6, 9 y 10 también se actualizaron.

## Pendiente para cerrar la segunda entrega

- **Capturas de pantalla.** Las 21 capturas de `screenshots/` son del 28 de septiembre, de la interfaz anterior. Hay que volver a tomarlas con el cliente React. La sección 14 del documento tiene una nota que lo indica; al reemplazarlas, borrar la nota. La Evidencia 4 pasa a ser el mensaje «Acceso denegado» del cliente y la Evidencia 21, la pantalla «Página no encontrada».
- **Imágenes de las figuras.** Ya regeneradas: los PNG de las figuras 2, 3, 5, 6, 9 y 10 corresponden a sus fuentes actualizadas. Para regenerarlas sin descargar el navegador de puppeteer, se puede pasar `-p` con un JSON que apunte a Edge (`{"executablePath":"C:/Program Files (x86)/Microsoft/Edge/Application/msedge.exe"}`).
- **Conteo de pruebas.** Después de esta actualización se agregó `EventoServiceTest` (5 pruebas), que cubre la corrección de la última actividad del dispositivo: un evento con fecha pasada ya no la hace retroceder. Ahora hay **25 pruebas**, no 20. Hay que actualizar las secciones 4.2, 5, 13, 15 y 18 del documento y agregar el problema a la sección 16 (ver D5 en `NORMALIZACION.md`).
- **Archivos exportados.** `PROYECTO CYBERGUARD.docx.pdf`, `Proyecto_CyberGuard.pdf` y las presentaciones `.pptx` se generaron con el texto anterior. Hay que volver a exportarlos desde el documento actualizado.

## Pendientes técnicos para la tercera entrega

Además de la tabla de la sección 17:

- **Vigencia del token.** Ya está documentada en 5.2 y en la sección 17. Falta decidir si se corrige: comprobar el estado del usuario en cada petición o acortar la vida del token.
- **Una consulta por evento.** `MotorAnalisis` consulta el historial una vez por cada regla activa; bastaría una consulta con la ventana más amplia.
- **Pruebas del cliente.** El frontend no tiene pruebas; considerar Vitest y Testing Library.

## Ajustes de forma

El índice (CONTENIDO) y el Anexo A siguen coincidiendo con las secciones y figuras: no se agregaron ni quitaron secciones ni figuras.
