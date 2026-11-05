const BASE = import.meta.env.VITE_API_URL ?? '';
const CLAVE_SESION = 'cyberguard.sesion';

export function leerSesion() {
  try {
    return JSON.parse(sessionStorage.getItem(CLAVE_SESION));
  } catch {
    return null;
  }
}

export function guardarSesion(sesion) {
  sessionStorage.setItem(CLAVE_SESION, JSON.stringify(sesion));
}

export function borrarSesion() {
  sessionStorage.removeItem(CLAVE_SESION);
}

let alExpirar = () => {};

/** El proveedor de autenticación se registra aquí para cerrar la sesión ante un 401. */
export function registrarAlExpirar(funcion) {
  alExpirar = funcion;
}

export class ApiError extends Error {
  constructor(status, mensaje, errores = {}) {
    super(mensaje);
    this.status = status;
    this.mensaje = mensaje;
    this.errores = errores;
  }
}

const MENSAJES_POR_ESTADO = {
  401: 'La sesión expiró. Ingresa de nuevo.',
  403: 'No tienes permiso para esta acción.',
  404: 'No se encontró lo que buscas.',
};

function construirUrl(ruta, params) {
  const consulta = new URLSearchParams();
  Object.entries(params ?? {}).forEach(([clave, valor]) => {
    if (valor !== undefined && valor !== null && valor !== '') {
      consulta.set(clave, valor);
    }
  });
  const texto = consulta.toString();
  return `${BASE}${ruta}${texto ? `?${texto}` : ''}`;
}

async function enviar(ruta, { method = 'GET', body, params } = {}) {
  const sesion = leerSesion();
  const cabeceras = {};
  if (body !== undefined) {
    cabeceras['Content-Type'] = 'application/json';
  }
  if (sesion?.token) {
    cabeceras.Authorization = `Bearer ${sesion.token}`;
  }

  let respuesta;
  try {
    respuesta = await fetch(construirUrl(ruta, params), {
      method,
      headers: cabeceras,
      body: body === undefined ? undefined : JSON.stringify(body),
    });
  } catch {
    throw new ApiError(0, 'No se pudo conectar con el servidor.');
  }

  if (!respuesta.ok) {
    if (respuesta.status === 401 && sesion) {
      alExpirar();
    }
    let datos = {};
    try {
      datos = await respuesta.json();
    } catch {
      // El cuerpo puede venir vacío (401 y 403 de Spring Security).
    }
    throw new ApiError(
      respuesta.status,
      datos.mensaje ?? MENSAJES_POR_ESTADO[respuesta.status] ?? 'Ocurrió un error inesperado.',
      datos.errores ?? {},
    );
  }
  return respuesta;
}

export const api = {
  get: (ruta, params) => enviar(ruta, { params }).then((r) => r.json()),
  post: (ruta, body) => enviar(ruta, { method: 'POST', body }).then((r) => r.json()),
  put: (ruta, body) => enviar(ruta, { method: 'PUT', body }).then((r) => r.json()),
};

/** Descarga un archivo protegido: un enlace directo no podría enviar el token. */
export async function descargar(ruta, params, nombreArchivo) {
  const respuesta = await enviar(ruta, { params });
  const url = URL.createObjectURL(await respuesta.blob());
  const enlace = document.createElement('a');
  enlace.href = url;
  enlace.download = nombreArchivo;
  enlace.click();
  URL.revokeObjectURL(url);
}
