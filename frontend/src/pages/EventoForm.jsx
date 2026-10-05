import { useNavigate } from 'react-router-dom';
import { api } from '../api.js';
import { useCatalogos } from '../catalogos.jsx';
import { aNumeroONulo, useFormulario } from '../components/useFormulario.js';
import { Campo, Encabezado, EnlaceBoton, MensajeError, Estado, Selector } from '../components/ui.jsx';
import { ahoraLocal, useConsulta } from '../util.js';

export default function EventoForm() {
  const navegar = useNavigate();
  const { catalogos } = useCatalogos();
  const { datos: dispositivos, error, cargando } = useConsulta(() => api.get('/api/dispositivos'), []);
  const { valores, setValores, errores, global, enviando, cambiar, enviar } = useFormulario({
    dispositivoId: '',
    direccionIp: '',
    fechaHora: ahoraLocal(),
    tipo: '',
    puerto: '',
    importancia: 'MEDIA',
    detalle: '',
  });

  // Al elegir un dispositivo registrado se rellena su IP, como hacía la pantalla anterior.
  const elegirDispositivo = (evento) => {
    const id = evento.target.value;
    const dispositivo = dispositivos.find((d) => String(d.id) === id);
    setValores((previos) => ({
      ...previos,
      dispositivoId: id,
      direccionIp: dispositivo ? dispositivo.direccionIp : previos.direccionIp,
    }));
  };

  const guardar = (evento) => {
    evento.preventDefault();
    enviar(async () => {
      await api.post('/api/eventos', {
        ...valores,
        dispositivoId: aNumeroONulo(valores.dispositivoId),
        puerto: aNumeroONulo(valores.puerto),
        tipo: valores.tipo || null,
      });
      navegar('/eventos', {
        state: { mensaje: 'Evento registrado. Si cumplió alguna regla, quedó en alertas.' },
      });
    });
  };

  return (
    <>
      <Encabezado
        titulo="Registrar evento"
        subtitulo="CyberGuard no escanea la red: aquí se cargan eventos de un entorno autorizado."
      />
      <Estado cargando={cargando} error={error} />
      {dispositivos && (
        <section className="panel">
          <form className="datos" onSubmit={guardar}>
            <MensajeError mensaje={global} />
            <Campo etiqueta="Dispositivo" error={errores.dispositivoId}>
              <select name="dispositivoId" value={valores.dispositivoId} onChange={elegirDispositivo}>
                <option value="">Sin dispositivo registrado</option>
                {dispositivos.map((d) => (
                  <option key={d.id} value={d.id}>{`${d.nombre} · ${d.direccionIp}`}</option>
                ))}
              </select>
            </Campo>
            <div className="dos">
              <Campo etiqueta="Dirección IP" error={errores.direccionIp}>
                <input className="mono" name="direccionIp" value={valores.direccionIp} onChange={cambiar} placeholder="192.168.10.11" />
              </Campo>
              <Campo etiqueta="Fecha y hora" error={errores.fechaHora}>
                <input type="datetime-local" name="fechaHora" value={valores.fechaHora} onChange={cambiar} />
              </Campo>
            </div>
            <div className="dos">
              <Campo etiqueta="Tipo" error={errores.tipo}>
                <Selector name="tipo" value={valores.tipo} onChange={cambiar} vacio="Selecciona" opciones={catalogos.tiposEvento} />
              </Campo>
              <Campo etiqueta="Puerto" error={errores.puerto}>
                <input type="number" min="1" max="65535" name="puerto" value={valores.puerto} onChange={cambiar} />
              </Campo>
            </div>
            <Campo etiqueta="Importancia" error={errores.importancia}>
              <Selector name="importancia" value={valores.importancia} onChange={cambiar} opciones={catalogos.importancias} />
            </Campo>
            <Campo etiqueta="Detalle" error={errores.detalle}>
              <textarea name="detalle" value={valores.detalle} onChange={cambiar} maxLength={500} />
            </Campo>
            <div className="acciones">
              <button className="boton" type="submit" disabled={enviando}>Analizar y guardar</button>
              <EnlaceBoton to="/eventos" fantasma>Cancelar</EnlaceBoton>
            </div>
          </form>
        </section>
      )}
    </>
  );
}
