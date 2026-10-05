import { Link } from 'react-router-dom';
import { api } from '../api.js';
import { useCatalogos } from '../catalogos.jsx';
import { Encabezado, Estado, Nivel } from '../components/ui.jsx';
import { formatFecha, useConsulta } from '../util.js';

export default function Panel() {
  const { etiqueta } = useCatalogos();
  const { datos: resumen, error, cargando } = useConsulta(() => api.get('/api/panel'), []);

  return (
    <>
      <Encabezado titulo="Panel" subtitulo="Indicadores de la red autorizada de laboratorio." />
      <Estado cargando={cargando} error={error} />
      {resumen && (
        <>
          <section className="rejilla">
            <article className="tarjeta"><span>Dispositivos</span><strong>{resumen.dispositivos}</strong></article>
            <article className="tarjeta"><span>Eventos</span><strong>{resumen.eventos}</strong></article>
            <article className="tarjeta"><span>Alertas</span><strong>{resumen.alertas}</strong></article>
            <article className="tarjeta alta"><span>Riesgo alto</span><strong>{resumen.alertasAltas}</strong></article>
            <article className="tarjeta critica"><span>Críticas</span><strong>{resumen.alertasCriticas}</strong></article>
          </section>
          <section className="columnas">
            <article className="panel">
              <h2>Eventos recientes</h2>
              {resumen.eventosRecientes.length === 0 ? (
                <p className="vacio">Aún no hay eventos registrados.</p>
              ) : (
                <table>
                  <thead>
                    <tr><th>Fecha</th><th>IP</th><th>Tipo</th><th>Importancia</th></tr>
                  </thead>
                  <tbody>
                    {resumen.eventosRecientes.map((evento) => (
                      <tr key={evento.id}>
                        <td>{formatFecha(evento.fechaHora)}</td>
                        <td className="mono">{evento.direccionIp}</td>
                        <td>{etiqueta('tiposEvento', evento.tipo)}</td>
                        <td>{etiqueta('importancias', evento.importancia)}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}
            </article>
            <article className="panel">
              <h2>Alertas recientes</h2>
              {resumen.alertasRecientes.length === 0 ? (
                <p className="vacio">No hay alertas.</p>
              ) : (
                <table>
                  <thead>
                    <tr><th>Nivel</th><th>IP</th><th></th></tr>
                  </thead>
                  <tbody>
                    {resumen.alertasRecientes.map((alerta) => (
                      <tr key={alerta.id}>
                        <td><Nivel nivel={alerta.nivel} /></td>
                        <td className="mono">{alerta.evento.direccionIp}</td>
                        <td><Link to={`/alertas/${alerta.id}`}>Ver</Link></td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}
            </article>
          </section>
        </>
      )}
    </>
  );
}
