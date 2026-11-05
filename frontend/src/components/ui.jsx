import { useId } from 'react';
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

/**
 * Campo de formulario con su mensaje de validación del servidor.
 * `children` es una función que recibe los atributos de accesibilidad (`id`,
 * `aria-invalid`, `aria-describedby`) para esparcirlos en el control: así la
 * etiqueta queda asociada por `htmlFor` y el error se anuncia como descripción,
 * no como parte del nombre del campo.
 */
export function Campo({ etiqueta, error, children }) {
  const id = useId();
  const idError = `${id}-error`;
  return (
    <div className="campo">
      <label htmlFor={id}>{etiqueta}</label>
      {children({
        id,
        'aria-invalid': error ? true : undefined,
        'aria-describedby': error ? idError : undefined,
      })}
      {error && <span className="error" id={idError}>{error}</span>}
    </div>
  );
}

/** Fila de encabezados de una tabla de datos; '' deja la columna de acciones sin título. */
export function CabeceraTabla({ columnas }) {
  return (
    <thead>
      <tr>
        {columnas.map((columna) => (
          <th key={columna}>{columna}</th>
        ))}
      </tr>
    </thead>
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
