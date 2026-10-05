import { useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { api } from '../api.js';
import { useCatalogos } from '../catalogos.jsx';
import { useFormulario } from '../components/useFormulario.js';
import { Campo, Encabezado, EnlaceBoton, MensajeError, Selector } from '../components/ui.jsx';

export default function UsuarioForm() {
  const { id } = useParams();
  const nuevo = id === undefined;
  const navegar = useNavigate();
  const { catalogos } = useCatalogos();
  const { valores, setValores, errores, global, enviando, cambiar, enviar } = useFormulario({
    nombre: '',
    usuario: '',
    correo: '',
    contrasena: '',
    rol: catalogos.roles[0].valor,
    activo: true,
  });

  useEffect(() => {
    if (!nuevo) {
      api.get(`/api/usuarios/${id}`)
        .then((u) =>
          setValores({
            nombre: u.nombre,
            usuario: u.usuario,
            correo: u.correo,
            contrasena: '',
            rol: u.rol,
            activo: u.activo,
          }),
        )
        .catch((e) => navegar('/usuarios', { replace: true, state: { mensaje: e.message } }));
    }
  }, [id, nuevo, navegar, setValores]);

  const guardar = (evento) => {
    evento.preventDefault();
    enviar(async () => {
      await (nuevo ? api.post('/api/usuarios', valores) : api.put(`/api/usuarios/${id}`, valores));
      navegar('/usuarios', { state: { mensaje: 'Usuario guardado' } });
    });
  };

  return (
    <>
      <Encabezado
        titulo={nuevo ? 'Nuevo usuario' : 'Editar usuario'}
        subtitulo={nuevo ? undefined : 'Deja la contraseña en blanco para conservar la actual.'}
      />
      <section className="panel">
        <form className="datos" onSubmit={guardar}>
          <MensajeError mensaje={global} />
          <Campo etiqueta="Nombre" error={errores.nombre}>
            <input name="nombre" value={valores.nombre} onChange={cambiar} />
          </Campo>
          <div className="dos">
            <Campo etiqueta="Usuario" error={errores.usuario}>
              <input name="usuario" value={valores.usuario} onChange={cambiar} autoComplete="off" />
            </Campo>
            <Campo etiqueta="Correo" error={errores.correo}>
              <input type="email" name="correo" value={valores.correo} onChange={cambiar} />
            </Campo>
          </div>
          <div className="dos">
            <Campo etiqueta="Contraseña" error={errores.contrasena}>
              <input type="password" name="contrasena" value={valores.contrasena} onChange={cambiar} autoComplete="new-password" />
            </Campo>
            <Campo etiqueta="Rol" error={errores.rol}>
              <Selector name="rol" value={valores.rol} onChange={cambiar} opciones={catalogos.roles} />
            </Campo>
          </div>
          <label className="casilla">
            <input type="checkbox" name="activo" checked={valores.activo} onChange={cambiar} /> Cuenta activa
          </label>
          <div className="acciones">
            <button className="boton" type="submit" disabled={enviando}>Guardar</button>
            <EnlaceBoton to="/usuarios" fantasma>Cancelar</EnlaceBoton>
          </div>
        </form>
      </section>
    </>
  );
}
