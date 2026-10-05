import { useState } from 'react';
import { ApiError } from '../api.js';

/**
 * Estado común de los formularios: valores, errores por campo del servidor y error global.
 * `enviar` recibe una función que hace la llamada a la API; si falla con 400 reparte los errores.
 */
export function useFormulario(inicial) {
  const [valores, setValores] = useState(inicial);
  const [errores, setErrores] = useState({});
  const [global, setGlobal] = useState(null);
  const [enviando, setEnviando] = useState(false);

  const cambiar = (evento) => {
    const { name, type, value, checked } = evento.target;
    setValores((previos) => ({ ...previos, [name]: type === 'checkbox' ? checked : value }));
  };

  const enviar = async (accion) => {
    setErrores({});
    setGlobal(null);
    setEnviando(true);
    try {
      return await accion();
    } catch (e) {
      if (!(e instanceof ApiError)) {
        throw e;
      }
      setErrores(e.errores);
      // Con errores por campo el mensaje genérico sobra; sin ellos es la regla de negocio que falló.
      setGlobal(Object.keys(e.errores).length === 0 ? e.message : null);
      return undefined;
    } finally {
      setEnviando(false);
    }
  };

  return { valores, setValores, errores, global, enviando, cambiar, enviar };
}

export const aNumeroONulo = (texto) => (texto === '' || texto === null || texto === undefined ? null : Number(texto));
