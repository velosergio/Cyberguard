import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { api } from '../api.js';
import { aNumeroONulo, useFormulario } from '../components/useFormulario.js';
import { Campo, Encabezado, EnlaceBoton, MensajeError } from '../components/ui.jsx';

export default function ReglaForm() {
  const { id } = useParams();
  const navegar = useNavigate();
  const [regla, setRegla] = useState(null);
  const { valores, setValores, errores, global, enviando, cambiar, enviar } = useFormulario({
    puntuacion: '',
    umbral: '',
    ventanaMinutos: '',
    activa: true,
  });

  useEffect(() => {
    api.get(`/api/reglas/${id}`)
      .then((r) => {
        setRegla(r);
        setValores({
          puntuacion: r.puntuacion,
          umbral: r.umbral,
          ventanaMinutos: r.ventanaMinutos,
          activa: r.activa,
        });
      })
      .catch((e) => navegar('/reglas', { replace: true, state: { mensaje: e.message } }));
  }, [id, navegar, setValores]);

  const guardar = (evento) => {
    evento.preventDefault();
    enviar(async () => {
      await api.put(`/api/reglas/${id}`, {
        puntuacion: aNumeroONulo(valores.puntuacion),
        umbral: aNumeroONulo(valores.umbral),
        ventanaMinutos: aNumeroONulo(valores.ventanaMinutos),
        activa: valores.activa,
      });
      navegar('/reglas', { state: { mensaje: 'Regla actualizada' } });
    });
  };

  if (!regla) {
    return <p className="vacio">Cargando…</p>;
  }

  return (
    <>
      <Encabezado titulo={regla.nombre} subtitulo={regla.descripcion} />
      <section className="panel">
        <form className="datos" onSubmit={guardar}>
          <MensajeError mensaje={global} />
          <div className="dos">
            <Campo etiqueta="Puntuación" error={errores.puntuacion}>
              <input type="number" min="1" max="100" name="puntuacion" value={valores.puntuacion} onChange={cambiar} />
            </Campo>
            <Campo etiqueta="Umbral" error={errores.umbral}>
              <input type="number" min="1" name="umbral" value={valores.umbral} onChange={cambiar} />
            </Campo>
          </div>
          <Campo etiqueta="Ventana en minutos" error={errores.ventanaMinutos}>
            <input type="number" min="0" max="1440" name="ventanaMinutos" value={valores.ventanaMinutos} onChange={cambiar} />
          </Campo>
          <label className="casilla">
            <input type="checkbox" name="activa" checked={valores.activa} onChange={cambiar} /> Regla activa
          </label>
          <div className="acciones">
            <button className="boton" type="submit" disabled={enviando}>Guardar</button>
            <EnlaceBoton to="/reglas" fantasma>Cancelar</EnlaceBoton>
          </div>
        </form>
      </section>
    </>
  );
}
