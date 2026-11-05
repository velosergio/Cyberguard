import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { api } from '../api.js';
import { useAuth } from '../auth.jsx';
import { useCatalogos } from '../catalogos.jsx';
import { useFormulario } from '../components/useFormulario.js';
import { Campo, Encabezado, EnlaceBoton, MensajeError, Estado, Nivel, Selector } from '../components/ui.jsx';
import { formatFecha, useConsulta } from '../util.js';

export default function AlertaDetalle() {
  const { id } = useParams();
  const { puedeEscribir } = useAuth();
  const { catalogos, etiqueta } = useCatalogos();
  const [alerta, setAlerta] = useState(null);
  const [mensaje, setMensaje] = useState(null);
  const { error, cargando } = useConsulta(
    () => api.get(`/api/alertas/${id}`).then((datos) => { setAlerta(datos); return datos; }),
    [id],
  );
  const { valores, setValores, errores, global, enviando, cambiar, enviar } = useFormulario({ estado: '', nota: '' });

  useEffect(() => {
    if (alerta) {
      setValores({ estado: alerta.estado, nota: alerta.nota ?? '' });
    }
  }, [alerta, setValores]);

  const guardar = (evento) => {
    evento.preventDefault();
    setMensaje(null);
    enviar(async () => {
      setAlerta(await api.put(`/api/alertas/${id}/estado`, valores));
      setMensaje('Estado de la alerta actualizado');
    });
  };

  return (
    <>
      <Estado cargando={cargando} error={error} />
      {alerta && (
        <>
          {mensaje && <p className="aviso">{mensaje}</p>}
          <Encabezado
            titulo={`Alerta ${alerta.idAlertas}`}
            subtitulo="La puntuación acumulada no afirma que exista un ataque. Indica que conviene revisarla."
          >
            <EnlaceBoton to="/alertas" fantasma>Volver</EnlaceBoton>
          </Encabezado>
          <section className="columnas">
            <article className="panel">
              <h2>Datos</h2>
              <table>
                <tbody>
                  <tr><th>Fecha</th><td>{formatFecha(alerta.fechaHora)}</td></tr>
                  <tr><th>IP</th><td className="mono">{alerta.evento.direccionIp}</td></tr>
                  <tr><th>Dispositivo</th><td>{alerta.dispositivo?.nombre ?? 'No registrado'}</td></tr>
                  <tr><th>Evento</th><td>{etiqueta('tiposEvento', alerta.evento.tipo)}</td></tr>
                  <tr><th>Detalle</th><td>{alerta.evento.detalle ?? '—'}</td></tr>
                  <tr><th>Puntuación</th><td>{alerta.puntuacion}</td></tr>
                  <tr><th>Nivel</th><td><Nivel nivel={alerta.nivel} /></td></tr>
                  <tr><th>Estado</th><td>{etiqueta('estadosAlerta', alerta.estado)}</td></tr>
                </tbody>
              </table>
              <h2>Reglas activadas</h2>
              <ul className="lista-reglas">
                {alerta.reglas.map((regla) => (
                  <li key={regla.idReglas}>
                    <strong>{regla.nombre}</strong>
                    <span>{` · ${regla.puntuacion} pts`}</span>
                    <div className="ayuda">{regla.descripcion}</div>
                  </li>
                ))}
              </ul>
            </article>
            {puedeEscribir && (
              <article className="panel">
                <h2>Seguimiento</h2>
                <form className="datos" onSubmit={guardar}>
                  <MensajeError mensaje={global} />
                  <Campo etiqueta="Estado" error={errores.estado}>
                    {(atributos) => (
                      <Selector {...atributos} name="estado" value={valores.estado} onChange={cambiar} opciones={catalogos.estadosAlerta} />
                    )}
                  </Campo>
                  <Campo etiqueta="Nota" error={errores.nota}>
                    {(atributos) => (
                      <textarea {...atributos} name="nota" value={valores.nota} onChange={cambiar} maxLength={500} />
                    )}
                  </Campo>
                  <button className="boton" type="submit" disabled={enviando}>Guardar estado</button>
                </form>
              </article>
            )}
          </section>
        </>
      )}
    </>
  );
}
