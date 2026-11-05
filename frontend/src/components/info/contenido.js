// Contenido de la página Info. Es documentación para leer en pantalla: resume
// Docs/GUIA.md, Docs/base-de-datos.md, Docs/NORMALIZACION.md, GOBERNANZA.md y
// AGENTS.md. Si cambia alguno de esos documentos, revisa también este archivo.
// Los números de las reglas son los valores iniciales de DataInitializer; los
// vigentes los ajusta el administrador en la pantalla Reglas.

export const SECCIONES = [
  { id: 'inicio', titulo: 'Qué es' },
  { id: 'funcionamiento', titulo: 'Cómo funciona' },
  { id: 'arquitectura', titulo: 'Arquitectura' },
  { id: 'datos', titulo: 'Base de datos' },
  { id: 'cardinalidad', titulo: 'Cardinalidad' },
  { id: 'normalizacion', titulo: 'Normalización' },
  { id: 'motor', titulo: 'Motor de reglas' },
  { id: 'seguridad', titulo: 'Seguridad y roles' },
  { id: 'gobernanza', titulo: 'Gobernanza' },
  { id: 'skills', titulo: 'Skills de IA' },
];

export const PASOS_FLUJO = [
  {
    titulo: 'Registrar',
    simple: 'Una persona anota lo que vio en la red: una IP, qué pasó y cuándo.',
    tecnico: 'El formulario envía POST /api/eventos. EventoController valida EventoForm con Jakarta Validation (@PastOrPresent rechaza fechas futuras).',
  },
  {
    titulo: 'Guardar',
    simple: 'El evento se guarda y, si la IP es de un equipo conocido, se enlaza con él.',
    tecnico: 'EventoService.registrar persiste el evento, lo asocia al dispositivo por id o por IP y avanza ultima_actividad con Dispositivo.registrarActividad (nunca retrocede).',
  },
  {
    titulo: 'Analizar',
    simple: 'El sistema revisa el historial reciente de esa IP con seis reglas.',
    tecnico: 'MotorAnalisis evalúa cada ReglaDeteccion activa con un ContextoAnalisis: evento, regla, historial [fechaHora − ventana, fechaHora] de la IP y si está inventariada.',
  },
  {
    titulo: 'Clasificar',
    simple: 'Suma los puntos de las reglas que se cumplieron y decide si el riesgo es bajo, medio, alto o crítico.',
    tecnico: 'ClasificadorRiesgo suma las puntuaciones, limita el total a 0–100 y lo convierte en NivelRiesgo con los cortes 20 / 50 / 80.',
  },
  {
    titulo: 'Alertar',
    simple: 'Si saltó al menos una regla, nace una alerta con todas las razones.',
    tecnico: 'AlertaService crea una sola Alerta por evento, ligada a todas las reglas que se cumplieron (tabla alerta_regla), en estado PENDIENTE.',
  },
  {
    titulo: 'Revisar',
    simple: 'Un analista mira la alerta, deja una nota y la va cerrando.',
    tecnico: 'PUT /api/alertas/{id}/estado con AlertaEstadoForm: PENDIENTE → EN_REVISION → ATENDIDA → CERRADA. Solo ANALISTA o ADMINISTRADOR.',
  },
];

export const CAPAS = [
  { nombre: 'Navegador · React 19', ruta: 'frontend/src/pages', simple: 'Lo que ves: pantallas y formularios.', tecnico: 'Una página por archivo; api.js es el único punto HTTP y agrega el token JWT.' },
  { nombre: 'Proxy de Vite', ruta: 'vite.config.js', simple: 'Un puente que lleva las peticiones /api al servidor.', tecnico: 'En desarrollo reenvía /api del puerto 5173 al 8080.' },
  { nombre: 'Spring Security', ruta: 'config/SecurityConfig', simple: 'El portero: revisa quién eres y qué puedes hacer.', tecnico: 'Filtro JWT HS256 sin sesión; permisos por ruta y método en requestMatchers.' },
  { nombre: 'web/', ruta: 'controladores, form/, dto/', simple: 'Recibe la petición, revisa los datos y responde en JSON.', tecnico: '@Valid sobre Forms; salida con DTO record.de(entidad); errores vía ApiExceptionHandler.' },
  { nombre: 'service/', ruta: 'EventoService, AlertaService…', simple: 'Aplica las reglas del negocio.', tecnico: 'Transacciones; carga lo perezoso antes de mapear (open-in-view=false).' },
  { nombre: 'analysis/', ruta: 'MotorAnalisis, reglas/*', simple: 'Decide si algo es sospechoso.', tecnico: 'Patrón estrategia: List<ReglaDeteccion> inyectada e indexada por codigo().' },
  { nombre: 'repository/ + domain/', ruta: 'Spring Data JPA', simple: 'Guarda y busca datos.', tecnico: 'Entidades JPA y consultas derivadas o JPQL (EventoRepository.historial).' },
  { nombre: 'MySQL / MariaDB', ruta: 'base cyberguard', simple: 'El archivador donde todo queda guardado.', tecnico: 'ddl-auto=update, dialecto MySQL fijo, zona America/Bogota.' },
];

export const TABLAS = {
  usuarios: {
    simple: 'Las cuentas para entrar al sistema y el rol de cada persona.',
    columnas: [
      ['id_usuarios', 'PK'], ['nombre', ''], ['usuario', 'UQ'], ['correo', 'UQ'],
      ['contrasena', 'BCrypt'], ['rol', 'ENUM'], ['activo', ''], ['creado_en', ''],
    ],
  },
  dispositivos: {
    simple: 'El inventario: los equipos autorizados de la red, cada uno con su IP.',
    columnas: [
      ['id_dispositivos', 'PK'], ['direccion_ip', 'UQ'], ['direccion_mac', ''], ['nombre', ''],
      ['tipo', 'ENUM'], ['estado', 'ENUM'], ['fecha_registro', ''], ['ultima_actividad', 'derivado'],
    ],
  },
  eventos: {
    simple: 'El historial: cada cosa que alguien anotó, con su IP y su fecha.',
    columnas: [
      ['id_eventos', 'PK'], ['dispositivo_id', 'FK ∅'], ['direccion_ip', ''], ['tipo', 'ENUM'],
      ['puerto', ''], ['fecha_hora', ''], ['detalle', ''], ['importancia', 'ENUM'],
    ],
  },
  alertas: {
    simple: 'El resultado del análisis: puntaje, nivel y en qué va la revisión.',
    columnas: [
      ['id_alertas', 'PK'], ['evento_id', 'FK'], ['dispositivo_id', 'FK ∅'], ['fecha_hora', 'copia'],
      ['puntuacion', ''], ['nivel', 'ENUM'], ['estado', 'ENUM'], ['nota', ''],
    ],
  },
  alerta_regla: {
    simple: 'Une cada alerta con todas las reglas que la hicieron saltar.',
    columnas: [['alerta_id', 'PK FK'], ['regla_id', 'PK FK']],
  },
  reglas: {
    simple: 'Los números ajustables de cada regla: puntos, umbral, ventana y si está activa.',
    columnas: [
      ['id_reglas', 'PK'], ['codigo', 'UQ'], ['nombre', ''], ['descripcion', ''],
      ['puntuacion', ''], ['umbral', ''], ['ventana_minutos', ''], ['activa', ''],
    ],
  },
};

export const RELACIONES = [
  {
    desde: 'dispositivos', hacia: 'eventos', notacion: '1 : 0..N', opcional: true,
    simple: 'Un equipo puede tener muchos eventos, pero un evento puede no tener equipo (una IP desconocida).',
    tecnico: 'eventos.dispositivo_id → dispositivos, FK que acepta NULL. Muchos a uno opcional.',
  },
  {
    desde: 'eventos', hacia: 'alertas', notacion: '1 : 0..1',
    simple: 'Cada evento produce como mucho una alerta; si no saltó ninguna regla, ninguna.',
    tecnico: 'alertas.evento_id NOT NULL. El esquema permite N; la regla «una alerta por evento» la cumple EventoService (UNIQUE pendiente).',
  },
  {
    desde: 'alertas', hacia: 'reglas', notacion: 'N : M',
    simple: 'Una alerta puede citar varias reglas y una regla aparece en muchas alertas.',
    tecnico: 'Tabla de unión alerta_regla con PRIMARY KEY (alerta_id, regla_id); en Java es un Set<Regla>.',
  },
  {
    desde: 'dispositivos', hacia: 'alertas', notacion: '1 : 0..N', opcional: true,
    simple: 'La alerta recuerda el equipo del evento para filtrar más rápido.',
    tecnico: 'alertas.dispositivo_id, copiado del evento al crear la alerta (desnormalización D1).',
  },
  {
    desde: 'usuarios', hacia: '—', notacion: 'sin relación',
    simple: 'Las cuentas no se enlazan con los datos: hoy no se registra quién cargó cada evento.',
    tecnico: 'Ninguna FK apunta a usuarios. La auditoría (registrado_por) está pendiente.',
  },
];

export const FORMAS = ['1FN', '2FN', '3FN', 'FNBC', '4FN'];

// 'si' cumple, 'casi' cumple con un dato derivado deliberado, 'no' no cumple.
export const MATRIZ_NORMALIZACION = [
  { tabla: 'usuarios', valores: ['si', 'si', 'si', 'si', 'si'] },
  { tabla: 'dispositivos', valores: ['si', 'si', 'casi', 'casi', 'si'] },
  { tabla: 'eventos', valores: ['si', 'si', 'casi', 'casi', 'si'] },
  { tabla: 'reglas', valores: ['si', 'si', 'si', 'si', 'si'] },
  { tabla: 'alertas', valores: ['si', 'si', 'no', 'no', 'si'] },
  { tabla: 'alerta_regla', valores: ['si', 'si', 'si', 'si', 'si'] },
];

export const ESCALONES = [
  { forma: '1FN', simple: 'Una casilla, un dato. Nada de listas separadas por comas.', tecnico: 'Valores atómicos, sin grupos repetidos y con clave primaria. Las reglas de una alerta van en alerta_regla, no en columnas regla1, regla2…' },
  { forma: '2FN', simple: 'Todo depende de la llave completa, no de un pedazo.', tecnico: 'Sin dependencias parciales. Las claves son de una columna salvo alerta_regla, que no tiene atributos fuera de la clave.' },
  { forma: '3FN', simple: 'Un dato no se deduce de otro dato que no sea la llave.', tecnico: 'Sin dependencias transitivas. Etiquetas fuera de la base (GET /api/catalogos); eventos no copia nombre ni MAC del equipo.' },
  { forma: 'FNBC', simple: 'Quien determina algo tiene que ser una llave.', tecnico: 'Todo determinante es clave candidata. alertas no cumple: evento_id → fecha_hora y puntuacion → nivel.' },
  { forma: '4FN', simple: 'No mezclar dos listas independientes en una tabla.', tecnico: 'Sin dependencias multivaluadas: la única relación de varios valores está aislada en alerta_regla.' },
];

export const DESNORMALIZACIONES = [
  { id: 'D1', donde: 'alertas.dispositivo_id', motivo: 'Copia del evento para filtrar alertas por equipo sin JOIN.', riesgo: 'Bajo' },
  { id: 'D2', donde: 'alertas.fecha_hora', motivo: 'Copia del evento para ordenar y filtrar por fecha.', riesgo: 'Bajo' },
  { id: 'D3', donde: 'alertas.nivel', motivo: 'Foto del análisis: se conserva aunque cambien los cortes.', riesgo: 'Medio' },
  { id: 'D4', donde: 'eventos.direccion_ip', motivo: 'Es la IP en el momento del hecho, aunque el equipo cambie.', riesgo: 'Ninguno' },
  { id: 'D5', donde: 'dispositivos.ultima_actividad', motivo: 'Evita MAX(fecha_hora) en cada listado; solo avanza.', riesgo: 'Bajo' },
];

export const REGLAS_INICIALES = [
  { codigo: 'DISPOSITIVO_NO_REGISTRADO', nombre: 'Dispositivo no registrado', puntos: 10, umbral: 1, ventana: 0, cuando: 'La IP no está en el inventario o el equipo está como desconocido.' },
  { codigo: 'ACTIVIDAD_INUSUAL', nombre: 'Actividad inusual', puntos: 10, umbral: 1, ventana: 60, cuando: 'El evento es de tipo «actividad inusual».' },
  { codigo: 'REPETICION_CONEXIONES', nombre: 'Repetición de conexiones', puntos: 15, umbral: 5, ventana: 10, cuando: 'La IP repite el mismo tipo de evento al menos el umbral de veces.' },
  { codigo: 'MULTIPLES_INTENTOS', nombre: 'Múltiples intentos', puntos: 20, umbral: 8, ventana: 5, cuando: 'Hay al menos el umbral de intentos de conexión en la ventana.' },
  { codigo: 'ESCANEO_PUERTOS', nombre: 'Escaneo de puertos', puntos: 25, umbral: 5, ventana: 2, cuando: 'La IP tocó al menos el umbral de puertos distintos.' },
  { codigo: 'ACUMULACION_SOSPECHOSA', nombre: 'Acumulación sospechosa', puntos: 30, umbral: 4, ventana: 30, cuando: 'Hay al menos el umbral de eventos de importancia alta.' },
];

// Mismos cortes que ClasificadorRiesgo (fuente de verdad); aquí solo se ilustran.
export const NIVELES = [
  { nivel: 'BAJO', etiqueta: 'Bajo', desde: 0, hasta: 20 },
  { nivel: 'MEDIO', etiqueta: 'Medio', desde: 21, hasta: 50 },
  { nivel: 'ALTO', etiqueta: 'Alto', desde: 51, hasta: 80 },
  { nivel: 'CRITICO', etiqueta: 'Crítico', desde: 81, hasta: 100 },
];

export const PERMISOS = [
  { accion: 'Ver panel, dispositivos, eventos, alertas e Info', roles: [true, true, true] },
  { accion: 'Registrar dispositivos y eventos', roles: [true, true, false] },
  { accion: 'Cambiar estado de alertas y dejar nota', roles: [true, true, false] },
  { accion: 'Ver reglas y descargar reportes CSV', roles: [true, true, false] },
  { accion: 'Ajustar puntos, umbral y ventana de reglas', roles: [true, false, false] },
  { accion: 'Crear y editar usuarios', roles: [true, false, false] },
];

export const EQUIPO = [
  { nombre: 'Sergio Esteban Veloza Gonzalez', rol: 'Product Owner', area: 'Backend y motor de análisis' },
  { nombre: 'Luifer Andres Hernandez Lambraño', rol: 'Scrum Master', area: 'Seguridad' },
  { nombre: 'Marcos Antonio Pacheco Bautista', rol: 'Desarrollador', area: 'Frontend' },
  { nombre: 'Rafael Antonio Rodríguez Gamarra', rol: 'Desarrollador', area: 'Documentación y pruebas' },
];

export const LIMITES = [
  'No se captura tráfico, no se escanean redes ni se bloquean conexiones.',
  'Solo redes, equipos y datos sobre los que el equipo tiene autorización.',
  'Datos de prueba ficticios: nada de IP, MAC ni correos reales de terceros.',
  'Una alerta es una señal para revisar, no un dictamen de ataque.',
];

export const TERMINADO = [
  '.\\mvnw.cmd test pasa en backend/',
  'npm run build termina sin errores en frontend/',
  'Respeta capas, SOLID y DRY (CLAUDE.md)',
  'Las pruebas cubren permisos, contrato o análisis si cambiaron',
  'La documentación afectada está al día',
  'Otro integrante revisó y aprobó el PR',
  'Se probó a mano en el navegador con el rol que corresponde',
];

export const DECISIONES = [
  ['Alcance y prioridades', 'Product Owner con el equipo; el docente tiene la última palabra'],
  ['Arquitectura y dependencias', 'Consenso del equipo; si no, mayoría simple'],
  ['Agregar o quitar una skill de IA', 'Consenso, igual que una dependencia'],
  ['Calibrar reglas', 'Equipo, con un escenario en MotorAnalisisTest'],
  ['Cambiar la gobernanza', 'PR aprobado por la mayoría'],
];

export const SKILLS = [
  { nombre: 'find-skills', origen: 'vercel-labs/skills', uso: 'Buscar e instalar otras skills con npx skills find.', icono: '⌕' },
  { nombre: 'java-springboot', origen: 'github/awesome-copilot', uso: 'Controladores, servicios, JPA y configuración del backend.', icono: '☕' },
  { nombre: 'springboot-security', origen: 'affaan-m/ecc', uso: 'JWT, SecurityConfig, CORS, validación de entradas y secretos.', icono: '🛡' },
  { nombre: 'springboot-tdd', origen: 'affaan-m/ecc', uso: 'Pruebas JUnit 5, Mockito y MockMvc en backend/src/test.', icono: '✓' },
  { nombre: 'vercel-react-best-practices', origen: 'vercel-labs/agent-skills', uso: 'Componentes, renders y carga de datos en el frontend.', icono: '⚛' },
  { nombre: 'mysql', origen: 'planetscale/database-skills', uso: 'Esquema, índices y consultas de MySQL/MariaDB.', icono: '⛁' },
];

export const CONFLICTOS_SKILLS = [
  ['Errores con ProblemDetail', 'ApiExceptionHandler + {"mensaje", "errores"}'],
  ['Paquetes por funcionalidad', 'Paquetes por capa: web, service, analysis…'],
  ['@PreAuthorize en controladores', 'Permisos centralizados en SecurityConfig'],
  ['Cookies httpOnly', 'JWT en sessionStorage, sin sesión'],
  ['Testcontainers y JaCoCo 80 %', 'H2 en memoria con el perfil test'],
  ['Tablas de catálogo y migraciones', 'Enums Java como STRING y ddl-auto=update'],
  ['Next.js y Server Components', 'React 19 + Vite, solo reglas de cliente'],
];
