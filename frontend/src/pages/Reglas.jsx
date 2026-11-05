import { Link } from 'react-router-dom';
import { api } from '../api.js';
import { useAuth } from '../auth.jsx';
import { AvisoNavegacion, Encabezado, Estado } from '../components/ui.jsx';
import { useConsulta } from '../util.js';

export default function Reglas() {
  const { esAdmin } = useAuth();
  const { datos: reglas, error, cargando } = useConsulta(() => api.get('/api/reglas'), []);

  return (
    <>
      <AvisoNavegacion />
      <Encabezado
        titulo="Reglas de detección"
        subtitulo="El administrador puede ajustar puntuación, umbral, ventana y si la regla está activa."
      />
      <Estado cargando={cargando} error={error} />
      {reglas && (
        <section className="panel">
          <table>
            <thead>
              <tr><th>Regla</th><th>Puntos</th><th>Umbral</th><th>Ventana</th><th>Estado</th><th></th></tr>
            </thead>
            <tbody>
              {reglas.map((r) => (
                <tr key={r.idReglas}>
                  <td>
                    <strong>{r.nombre}</strong>
                    <div className="ayuda">{r.descripcion}</div>
                  </td>
                  <td>{r.puntuacion}</td>
                  <td>{r.umbral}</td>
                  <td>{`${r.ventanaMinutos} min`}</td>
                  <td>{r.activa ? 'Activa' : 'Inactiva'}</td>
                  <td>{esAdmin && <Link to={`/reglas/${r.idReglas}/editar`}>Editar</Link>}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>
      )}
    </>
  );
}
