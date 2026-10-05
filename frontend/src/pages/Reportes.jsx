import { useState } from 'react';
import { api, descargar } from '../api.js';
import { useCatalogos } from '../catalogos.jsx';
import { Encabezado, MensajeError, Estado, Selector } from '../components/ui.jsx';
import { formatFecha, useConsulta } from '../util.js';

const SIN_FILTROS = { ip: '', tipo: '', nivel: '', desde: '', hasta: '' };

export default function Reportes() {
  const { catalogos, etiqueta } = useCatalogos();
  const [formulario, setFormulario] = useState(SIN_FILTROS);
  const [filtros, setFiltros] = useState(SIN_FILTROS);
  const [errorDescarga, setErrorDescarga] = useState(null);
  const { datos, error, cargando } = useConsulta(() => api.get('/api/reportes', filtros), [filtros]);

  const cambiar = (e) => setFormulario({ ...formulario, [e.target.name]: e.target.value });
  const generar = (e) => {
    e.preventDefault();
    setFiltros(formulario);
  };

  const bajarCsv = async (ruta, parametros, nombre) => {
    setErrorDescarga(null);
    try {
      await descargar(ruta, parametros, nombre);
    } catch (e) {
      setErrorDescarga(e.message);
    }
  };

  return (
    <>
      <Encabezado
        titulo="Reportes"
        subtitulo="Consulta imprimible y descarga CSV. El separador es punto y coma para Excel."
      >
        <button className="boton boton-linea no-imprimir" type="button" onClick={() => window.print()}>
          Imprimir
        </button>
      </Encabezado>
      <form className="filtros no-imprimir" onSubmit={generar}>
        <label>IP del evento<input className="mono" name="ip" value={formulario.ip} onChange={cambiar} /></label>
        <label>Tipo de evento<Selector name="tipo" value={formulario.tipo} onChange={cambiar} vacio="Todos" opciones={catalogos.tiposEvento} /></label>
        <label>Nivel de alerta<Selector name="nivel" value={formulario.nivel} onChange={cambiar} vacio="Todos" opciones={catalogos.nivelesRiesgo} /></label>
        <label>Desde<input type="date" name="desde" value={formulario.desde} onChange={cambiar} /></label>
        <label>Hasta<input type="date" name="hasta" value={formulario.hasta} onChange={cambiar} /></label>
        <button className="boton" type="submit">Generar</button>
      </form>
      <div className="acciones no-imprimir">
        <button
          className="boton boton-fantasma"
          type="button"
          onClick={() => bajarCsv('/api/reportes/eventos.csv', { ip: filtros.ip, tipo: filtros.tipo, desde: filtros.desde, hasta: filtros.hasta }, 'eventos.csv')}
        >
          CSV de eventos
        </button>
        <button
          className="boton boton-fantasma"
          type="button"
          onClick={() => bajarCsv('/api/reportes/alertas.csv', { nivel: filtros.nivel, desde: filtros.desde, hasta: filtros.hasta }, 'alertas.csv')}
        >
          CSV de alertas
        </button>
      </div>
      <MensajeError mensaje={errorDescarga} />
      <Estado cargando={cargando} error={error} />
      {datos && (
        <>
          <section className="panel">
            <h2>Eventos</h2>
            {datos.eventos.length === 0 ? (
              <p className="vacio">Sin eventos para el filtro.</p>
            ) : (
              <table>
                <thead>
                  <tr><th>Fecha</th><th>IP</th><th>Tipo</th><th>Importancia</th><th>Detalle</th></tr>
                </thead>
                <tbody>
                  {datos.eventos.map((e) => (
                    <tr key={e.id}>
                      <td>{formatFecha(e.fechaHora)}</td>
                      <td className="mono">{e.direccionIp}</td>
                      <td>{etiqueta('tiposEvento', e.tipo)}</td>
                      <td>{etiqueta('importancias', e.importancia)}</td>
                      <td>{e.detalle ?? '—'}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </section>
          <section className="panel" style={{ marginTop: '1rem' }}>
            <h2>Alertas</h2>
            {datos.alertas.length === 0 ? (
              <p className="vacio">Sin alertas para el filtro.</p>
            ) : (
              <table>
                <thead>
                  <tr><th>Fecha</th><th>IP</th><th>Nivel</th><th>Puntos</th><th>Estado</th></tr>
                </thead>
                <tbody>
                  {datos.alertas.map((a) => (
                    <tr key={a.id}>
                      <td>{formatFecha(a.fechaHora)}</td>
                      <td className="mono">{a.evento.direccionIp}</td>
                      <td>{etiqueta('nivelesRiesgo', a.nivel)}</td>
                      <td>{a.puntuacion}</td>
                      <td>{etiqueta('estadosAlerta', a.estado)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </section>
        </>
      )}
    </>
  );
}
