import { createContext, useContext, useEffect, useState } from 'react';
import { api } from './api.js';

const CatalogosContext = createContext(null);

/** Carga una vez las listas de valores (tipos, estados, roles...) con sus etiquetas. */
export function CatalogosProvider({ children }) {
  const [catalogos, setCatalogos] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    api.get('/api/catalogos').then(setCatalogos).catch((e) => setError(e.message));
  }, []);

  if (error) {
    return <p className="aviso error">{error}</p>;
  }
  if (!catalogos) {
    return <p className="vacio">Cargando…</p>;
  }

  const etiqueta = (catalogo, valor) =>
    catalogos[catalogo]?.find((opcion) => opcion.valor === valor)?.etiqueta ?? valor ?? '—';

  return <CatalogosContext.Provider value={{ catalogos, etiqueta }}>{children}</CatalogosContext.Provider>;
}

export function useCatalogos() {
  return useContext(CatalogosContext);
}
