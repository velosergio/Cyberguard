# AGENTS.md

Instrucciones para cualquier asistente de IA que trabaje en este repositorio (Claude Code, Cursor, Codex, Copilot u otro). Las reglas del equipo sobre IA están en `GOBERNANZA.md` (sección 8).

## Fuente de verdad

- **`CLAUDE.md`** es la guía completa para agentes: alcance, comandos, arquitectura del backend y del frontend, principios SOLID/DRY y qué actualizar al cambiar algo. Léelo antes de modificar código; vale para todos los asistentes, no solo para Claude.
- **`GOBERNANZA.md`**: ramas, PR, definición de terminado y límites de alcance.
- **`Docs/GUIA.md`**: API, arquitectura y decisiones de diseño.

Resumen de lo que no se negocia:

- CyberGuard registra a mano eventos de una red autorizada. **No** agregues captura de tráfico, escaneo ni bloqueo de conexiones.
- Código, identificadores, mensajes, documentación y commits en **español**.
- Verifica con `.\mvnw.cmd test` en `backend/` y `npm run build` en `frontend/`.

## Skills del proyecto

El repositorio trae skills instaladas con la CLI de [skills.sh](https://skills.sh/) (`npx skills`). Están versionadas para que todo el equipo use las mismas:

- `.claude/skills/` — las lee Claude Code.
- `.agents/skills/` — las leen Cursor, GitHub Copilot, Codex y los demás agentes compatibles.
- `skills-lock.json` — origen y hash de cada skill; permite restaurarlas.

| Skill | Origen | Cuándo usarla en CyberGuard |
| --- | --- | --- |
| `find-skills` | `vercel-labs/skills` | Buscar e instalar otras skills (`npx skills find <tema>`). |
| `java-springboot` | `github/awesome-copilot` | Controladores, servicios, JPA y configuración en `backend/`. |
| `springboot-security` | `affaan-m/ecc` | Cambios en `config/` (JWT, `SecurityConfig`, CORS), validación de entradas y secretos. |
| `springboot-tdd` | `affaan-m/ecc` | Escribir pruebas JUnit 5 / Mockito / MockMvc en `backend/src/test/`. |
| `vercel-react-best-practices` | `vercel-labs/agent-skills` | Componentes, renders y carga de datos en `frontend/src/`. |
| `mysql` | `planetscale/database-skills` | Esquema, índices y consultas de MySQL/MariaDB (`repository/`, `Docs/base-de-datos.md`). |

### Las convenciones del proyecto mandan sobre las skills

Las skills son guías genéricas de terceros. Si una contradice `CLAUDE.md`, gana `CLAUDE.md`. Conflictos conocidos:

| La skill sugiere | En CyberGuard se hace |
| --- | --- |
| Errores con `ProblemDetail` (`java-springboot`) | `ApiExceptionHandler` + `ErrorRespuesta` con `{"mensaje", "errores"}` |
| Paquetes por funcionalidad (`java-springboot`) | Paquetes por capa: `web/`, `service/`, `analysis/`, `repository/`, `domain/` |
| `@PreAuthorize` en los controladores (`springboot-security`) | Permisos centralizados en `SecurityConfig` (`requestMatchers`) |
| Cookies `httpOnly` para la sesión (`springboot-security`) | JWT en `sessionStorage`, sin sesión ni cookies (ver «Decisiones de diseño» en `Docs/GUIA.md`) |
| Rate limiting, Vault, escaneo de CVE (`springboot-security`) | Fuera del alcance actual; se propone en el backlog, no se agrega de paso |
| Testcontainers y cobertura JaCoCo del 80 % (`springboot-tdd`) | H2 en memoria con el perfil `test`; sin Docker ni umbral de cobertura |
| Tablas de catálogo en lugar de `ENUM`; migraciones (`mysql`) | Enums Java persistidos como `STRING` y `ddl-auto=update` |
| Hosting en PlanetScale (`mysql`) | MySQL/MariaDB local de Laragon o XAMPP |
| Next.js, Server Components, `next/dynamic` (`vercel-react-best-practices`) | React 19 + Vite sin SSR; aplica solo las reglas de cliente (`rerender-`, `rendering-`, `js-`, `client-`) |

Ninguna skill autoriza agregar dependencias ni infraestructura nueva: eso es una decisión del equipo (`GOBERNANZA.md`, sección 4).

### Administrar skills

Desde la raíz del repositorio:

```bat
npx skills list                                  :: ver las instaladas
npx skills find spring boot                      :: buscar en skills.sh
npx skills add <dueño/repo> -s <skill> -a claude-code -a cursor --copy -y
npx skills update -p                             :: actualizar las del proyecto
npx skills remove <skill>                        :: quitar una
npx skills experimental_install                  :: restaurar desde skills-lock.json
```

Usa `--copy` (no enlaces simbólicos, que fallan en Windows sin permisos de administrador) e instala para `claude-code` y `cursor` a la vez para que las dos carpetas queden iguales. Agregar, actualizar o quitar una skill se hace en su propio PR, revisando antes su contenido (ver `GOBERNANZA.md`, sección 8).
