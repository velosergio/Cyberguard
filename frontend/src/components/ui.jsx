import { Link, useLocation } from 'react-router-dom';
import { useCatalogos } from '../catalogos.jsx';

/** Mensaje de éxito que una pantalla anterior dejó en el estado de navegación. */
export function AvisoNavegacion() {
  const { state } = useLocation();
  return state?.mensaje ? <p className="aviso">{state.mensaje}</p> : null;
}

export function MensajeError({ mensaje }) {
  return mensaje ? <p className="aviso error">{mensaje}</p> : null;
}

export function Encabezado({ titulo, subtitulo, children }) {
  return (
    <header className="encabezado">
      <div>
        <h1>{titulo}</h1>
        {subtitulo && <p className="subtitulo">{subtitulo}</p>}
      </div>
      {children}
    </header>
  );
}

export function Estado({ cargando, error }) {
  if (cargando) {
    return <p className="vacio">Cargando…</p>;
  }
  return <MensajeError mensaje={error} />;
}

export function Nivel({ nivel }) {
  const { etiqueta } = useCatalogos();
  return <span className={`nivel nivel-${nivel}`}>{etiqueta('nivelesRiesgo', nivel)}</span>;
}

/** Campo de formulario con su mensaje de validación del servidor. */
export function Campo({ etiqueta, error, children }) {
  return (
    <label>
      {etiqueta}
      {children}
      {error && <span className="error">{error}</span>}
    </label>
  );
}

export function Selector({ opciones, vacio, ...props }) {
  return (
    <select {...props}>
      {vacio !== undefined && <option value="">{vacio}</option>}
      {opciones.map((opcion) => (
        <option key={opcion.valor} value={opcion.valor}>
          {opcion.etiqueta}
        </option>
      ))}
    </select>
  );
}

export function EnlaceBoton({ to, children, fantasma }) {
  return (
    <Link className={fantasma ? 'boton boton-fantasma' : 'boton'} to={to}>
      {children}
    </Link>
  );
}
