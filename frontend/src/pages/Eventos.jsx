import { useState } from 'react';
import { api } from '../api.js';
import { useAuth } from '../auth.jsx';
import { useCatalogos } from '../catalogos.jsx';
import { AvisoNavegacion, Encabezado, EnlaceBoton, Estado, Selector } from '../components/ui.jsx';
import { formatFecha, useConsulta } from '../util.js';

const SIN_FILTROS = { ip: '', tipo: '', desde: '', hasta: '' };

export default function Eventos() {
  const { puedeEscribir } = useAuth();
  const { catalogos, etiqueta } = useCatalogos();
  const [formulario, setFormulario] = useState(SIN_FILTROS);
  const [filtros, setFiltros] = useState(SIN_FILTROS);
  const { datos: eventos, error, cargando } = useConsulta(() => api.get('/api/eventos', filtros), [filtros]);

  const cambiar = (e) => setFormulario({ ...formulario, [e.target.name]: e.target.value });
  const filtrar = (e) => {
    e.preventDefault();
    setFiltros(formulario);
  };

  return (
    <>
      <AvisoNavegacion />
      <Encabezado titulo="Historial de eventos" subtitulo="Eventos registrados manualmente con datos de laboratorio.">
        {puedeEscribir && <EnlaceBoton to="/eventos/nuevo">Registrar evento</EnlaceBoton>}
      </Encabezado>
      <form className="filtros" onSubmit={filtrar}>
        <label>IP<input className="mono" name="ip" value={formulario.ip} onChange={cambiar} /></label>
        <label>Tipo<Selector name="tipo" value={formulario.tipo} onChange={cambiar} vacio="Todos" opciones={catalogos.tiposEvento} /></label>
        <label>Desde<input type="date" name="desde" value={formulario.desde} onChange={cambiar} /></label>
        <label>Hasta<input type="date" name="hasta" value={formulario.hasta} onChange={cambiar} /></label>
        <button className="boton" type="submit">Filtrar</button>
      </form>
      <Estado cargando={cargando} error={error} />
      {eventos && (
        <section className="panel">
          {eventos.length === 0 ? (
            <p className="vacio">No hay eventos con esos filtros.</p>
          ) : (
            <table>
              <thead>
                <tr><th>Fecha</th><th>IP</th><th>Dispositivo</th><th>Tipo</th><th>Puerto</th><th>Importancia</th><th>Detalle</th></tr>
              </thead>
              <tbody>
                {eventos.map((e) => (
                  <tr key={e.id}>
                    <td>{formatFecha(e.fechaHora)}</td>
                    <td className="mono">{e.direccionIp}</td>
                    <td>{e.dispositivoNombre ?? 'No registrado'}</td>
                    <td>{etiqueta('tiposEvento', e.tipo)}</td>
                    <td>{e.puerto ?? '—'}</td>
                    <td>{etiqueta('importancias', e.importancia)}</td>
                    <td>{e.detalle ?? '—'}</td>
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
