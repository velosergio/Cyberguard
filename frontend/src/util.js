import { useCallback, useEffect, useState } from 'react';

const dos = (n) => String(n).padStart(2, '0');

export function formatFecha(iso) {
  if (!iso) {
    return '—';
  }
  const d = new Date(iso);
  return `${dos(d.getDate())}/${dos(d.getMonth() + 1)}/${d.getFullYear()} ${dos(d.getHours())}:${dos(d.getMinutes())}`;
}

/** Valor para un input datetime-local con la hora local actual. */
export function ahoraLocal() {
  const d = new Date();
  return `${d.getFullYear()}-${dos(d.getMonth() + 1)}-${dos(d.getDate())}T${dos(d.getHours())}:${dos(d.getMinutes())}`;
}

/**
 * Ejecuta una consulta a la API y expone {datos, error, cargando}.
 * `deps` decide cuándo se vuelve a consultar.
 */
export function useConsulta(consulta, deps) {
  const [estado, setEstado] = useState({ datos: null, error: null, cargando: true });
  // eslint-disable-next-line react-hooks/exhaustive-deps
  const ejecutar = useCallback(consulta, deps);

  useEffect(() => {
    let vigente = true;
    setEstado((previo) => ({ ...previo, cargando: true, error: null }));
    ejecutar()
      .then((datos) => vigente && setEstado({ datos, error: null, cargando: false }))
      .catch((e) => vigente && setEstado({ datos: null, error: e.message, cargando: false }));
    return () => {
      vigente = false;
    };
  }, [ejecutar]);

  return estado;
}
