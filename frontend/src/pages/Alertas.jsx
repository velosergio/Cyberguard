import { useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../api.js';
import { useCatalogos } from '../catalogos.jsx';
import { AvisoNavegacion, Encabezado, Estado, Nivel, Selector } from '../components/ui.jsx';
import { formatFecha, useConsulta } from '../util.js';

const SIN_FILTROS = { nivel: '', estado: '', desde: '', hasta: '' };

export default function Alertas() {
  const { catalogos, etiqueta } = useCatalogos();
  const [formulario, setFormulario] = useState(SIN_FILTROS);
  const [filtros, setFiltros] = useState(SIN_FILTROS);
  const { datos: alertas, error, cargando } = useConsulta(() => api.get('/api/alertas', filtros), [filtros]);

  const cambiar = (e) => setFormulario({ ...formulario, [e.target.name]: e.target.value });
  const filtrar = (e) => {
    e.preventDefault();
    setFiltros(formulario);
  };

  return (
    <>
      <AvisoNavegacion />
      <Encabezado
        titulo="Historial de alertas"
        subtitulo="Situaciones que cumplieron al menos una regla y requieren revisión."
      />
      <form className="filtros" onSubmit={filtrar}>
        <label>Nivel<Selector name="nivel" value={formulario.nivel} onChange={cambiar} vacio="Todos" opciones={catalogos.nivelesRiesgo} /></label>
        <label>Estado<Selector name="estado" value={formulario.estado} onChange={cambiar} vacio="Todos" opciones={catalogos.estadosAlerta} /></label>
        <label>Desde<input type="date" name="desde" value={formulario.desde} onChange={cambiar} /></label>
        <label>Hasta<input type="date" name="hasta" value={formulario.hasta} onChange={cambiar} /></label>
        <button className="boton" type="submit">Filtrar</button>
      </form>
      <Estado cargando={cargando} error={error} />
      {alertas && (
        <section className="panel">
          {alertas.length === 0 ? (
            <p className="vacio">No hay alertas con esos filtros.</p>
          ) : (
            <table>
              <thead>
                <tr><th>Fecha</th><th>IP</th><th>Nivel</th><th>Puntos</th><th>Estado</th><th>Reglas</th><th></th></tr>
              </thead>
              <tbody>
                {alertas.map((a) => (
                  <tr key={a.id}>
                    <td>{formatFecha(a.fechaHora)}</td>
                    <td className="mono">{a.evento.direccionIp}</td>
                    <td><Nivel nivel={a.nivel} /></td>
                    <td>{a.puntuacion}</td>
                    <td className="estado">{etiqueta('estadosAlerta', a.estado)}</td>
                    <td>{a.reglas.map((r) => r.nombre).join(', ')}</td>
                    <td><Link to={`/alertas/${a.id}`}>Detalle</Link></td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </section>
      )}
    </>
  );
}
