import { Link } from 'react-router-dom';
import { api } from '../api.js';
import { useAuth } from '../auth.jsx';
import { useCatalogos } from '../catalogos.jsx';
import { AvisoNavegacion, CabeceraTabla, Encabezado, EnlaceBoton, Estado } from '../components/ui.jsx';
import { formatFecha, useConsulta } from '../util.js';

export default function Dispositivos() {
  const { puedeEscribir } = useAuth();
  const { etiqueta } = useCatalogos();
  const { datos: dispositivos, error, cargando } = useConsulta(() => api.get('/api/dispositivos'), []);

  return (
    <>
      <AvisoNavegacion />
      <Encabezado titulo="Dispositivos" subtitulo="Equipos registrados en la red autorizada.">
        {puedeEscribir && <EnlaceBoton to="/dispositivos/nuevo">Registrar</EnlaceBoton>}
      </Encabezado>
      <Estado cargando={cargando} error={error} />
      {dispositivos && (
        <section className="panel">
          {dispositivos.length === 0 ? (
            <p className="vacio">No hay dispositivos registrados.</p>
          ) : (
            <table>
              <CabeceraTabla columnas={['Nombre', 'IP', 'MAC', 'Tipo', 'Estado', 'Última actividad', '']} />
              <tbody>
                {dispositivos.map((d) => (
                  <tr key={d.idDispositivos}>
                    <td>{d.nombre}</td>
                    <td className="mono">{d.direccionIp}</td>
                    <td className="mono">{d.direccionMac ?? '—'}</td>
                    <td>{etiqueta('tiposDispositivo', d.tipo)}</td>
                    <td>{etiqueta('estadosDispositivo', d.estado)}</td>
                    <td>{formatFecha(d.ultimaActividad)}</td>
                    <td>{puedeEscribir && <Link to={`/dispositivos/${d.idDispositivos}/editar`}>Editar</Link>}</td>
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
