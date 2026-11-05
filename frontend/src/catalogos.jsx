import { createContext, useContext, useEffect, useMemo, useState } from 'react';
import { api } from './api.js';

const CatalogosContext = createContext(null);

/** Carga una vez las listas de valores (tipos, estados, roles...) con sus etiquetas. */
export function CatalogosProvider({ children }) {
  const [catalogos, setCatalogos] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    api.get('/api/catalogos').then(setCatalogos).catch((e) => setError(e.message));
  }, []);

  // Valor estable: solo cambia cuando llegan los catálogos, así los lectores no se redibujan
  // cada vez que el Layout se vuelve a pintar (p. ej. en cada navegación).
  // Va antes de los return tempranos para no romper el orden de los hooks.
  const contexto = useMemo(
    () => ({
      catalogos,
      etiqueta: (catalogo, valor) =>
        catalogos?.[catalogo]?.find((opcion) => opcion.valor === valor)?.etiqueta ?? valor ?? '—',
    }),
    [catalogos],
  );

  if (error) {
    return <p className="aviso error">{error}</p>;
  }
  if (!catalogos) {
    return <p className="vacio">Cargando…</p>;
  }

  return <CatalogosContext.Provider value={contexto}>{children}</CatalogosContext.Provider>;
}

export function useCatalogos() {
  return useContext(CatalogosContext);
}
