# Informe de pendientes — primera entrega CyberGuard

El documento cubre el planteamiento. Falta el diseño gráfico y el reporte de lo que ya está construido en el código. De 17 ítems: 5 cubiertos, 5 parciales y 7 ausentes.

## Cubiertos

Problema, justificación, objetivos, alcance y requerimientos (funcionales y no funcionales).

## Parciales

- **Actores.** Están nombrados (administrador, analista y consulta). Falta una sección propia, casos de uso y matriz de permisos.
- **Diseño de la solución.** Hay módulos, flujo y puntuación. El modelo de datos está en `base-de-datos.md` y no entra en este documento.
- **Tecnologías.** Siguen como propuestas y dejan «PostgreSQL o MySQL». El sistema usa MySQL, Spring Security, Thymeleaf y JUnit.
- **Arquitectura.** Solo hay capas en texto. Falta nombrar los paquetes reales: `web`, `service`, `domain`, `analysis` y `repository`.
- **Conclusiones.** Cierran la propuesta. No concluyen sobre lo construido, las pruebas ni el avance.

## Ausentes

1. **Diagramas:** casos de uso, clases, secuencia, entidad-relación y arquitectura.
2. **Funcionalidades desarrolladas:** login con roles, dispositivos, eventos, seis reglas, clasificación de riesgo, alertas, panel y reportes.
3. **Avance del desarrollo:** la sección 16 es un plan, no un estado por módulo.
4. **Evidencias:** capturas del sistema con los tres roles.
5. **Pruebas realizadas:** ya existen `MotorAnalisisTest`, `ClasificadorRiesgoTest` y `SeguridadAccesoTest`, sin casos ni resultados en el documento.
6. **Problemas y solución:** no hay sección (por ejemplo, el dialecto de MariaDB en XAMPP).
7. **Pendientes para la tercera entrega.**

## Ajustes de forma

El texto está en futuro y la conclusión dice que el código y las pruebas quedan para después. Corregir el título 5.2 duplicado y el salto de la sección 18 a la 20.
