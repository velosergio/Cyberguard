import { Link } from 'react-router-dom';
import { api } from '../api.js';
import { useCatalogos } from '../catalogos.jsx';
import { AvisoNavegacion, Encabezado, EnlaceBoton, Estado } from '../components/ui.jsx';
import { useConsulta } from '../util.js';

export default function Usuarios() {
  const { etiqueta } = useCatalogos();
  const { datos: usuarios, error, cargando } = useConsulta(() => api.get('/api/usuarios'), []);

  return (
    <>
      <AvisoNavegacion />
      <Encabezado titulo="Usuarios" subtitulo="Administrador, analista y consulta.">
        <EnlaceBoton to="/usuarios/nuevo">Nuevo usuario</EnlaceBoton>
      </Encabezado>
      <Estado cargando={cargando} error={error} />
      {usuarios && (
        <section className="panel">
          <table>
            <thead>
              <tr><th>Nombre</th><th>Usuario</th><th>Correo</th><th>Rol</th><th>Estado</th><th></th></tr>
            </thead>
            <tbody>
              {usuarios.map((u) => (
                <tr key={u.id}>
                  <td>{u.nombre}</td>
                  <td className="mono">{u.usuario}</td>
                  <td>{u.correo}</td>
                  <td>{etiqueta('roles', u.rol)}</td>
                  <td>{u.activo ? 'Activo' : 'Inactivo'}</td>
                  <td><Link to={`/usuarios/${u.id}/editar`}>Editar</Link></td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>
      )}
    </>
  );
}
