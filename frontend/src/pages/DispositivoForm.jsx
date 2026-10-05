import { useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { api } from '../api.js';
import { useCatalogos } from '../catalogos.jsx';
import { useFormulario } from '../components/useFormulario.js';
import { Campo, Encabezado, EnlaceBoton, MensajeError, Selector } from '../components/ui.jsx';

export default function DispositivoForm() {
  const { id } = useParams();
  const nuevo = id === undefined;
  const navegar = useNavigate();
  const { catalogos } = useCatalogos();
  const { valores, setValores, errores, global, enviando, cambiar, enviar } = useFormulario({
    nombre: '',
    direccionIp: '',
    direccionMac: '',
    tipo: catalogos.tiposDispositivo[0].valor,
    estado: catalogos.estadosDispositivo[0].valor,
  });

  useEffect(() => {
    if (!nuevo) {
      api.get(`/api/dispositivos/${id}`)
        .then((d) =>
          setValores({
            nombre: d.nombre,
            direccionIp: d.direccionIp,
            direccionMac: d.direccionMac ?? '',
            tipo: d.tipo,
            estado: d.estado,
          }),
        )
        .catch((e) => navegar('/dispositivos', { replace: true, state: { mensaje: e.message } }));
    }
  }, [id, nuevo, navegar, setValores]);

  const guardar = (evento) => {
    evento.preventDefault();
    enviar(async () => {
      await (nuevo ? api.post('/api/dispositivos', valores) : api.put(`/api/dispositivos/${id}`, valores));
      navegar('/dispositivos', { state: { mensaje: 'Dispositivo guardado' } });
    });
  };

  return (
    <>
      <Encabezado
        titulo={nuevo ? 'Registrar dispositivo' : 'Editar dispositivo'}
        subtitulo="Solo equipos de la red sobre la que existe autorización."
      />
      <section className="panel">
        <form className="datos" onSubmit={guardar}>
          <MensajeError mensaje={global} />
          <Campo etiqueta="Nombre" error={errores.nombre}>
            <input name="nombre" value={valores.nombre} onChange={cambiar} />
          </Campo>
          <div className="dos">
            <Campo etiqueta="Dirección IP" error={errores.direccionIp}>
              <input className="mono" name="direccionIp" value={valores.direccionIp} onChange={cambiar} placeholder="192.168.1.10" />
            </Campo>
            <Campo etiqueta="Dirección MAC" error={errores.direccionMac}>
              <input className="mono" name="direccionMac" value={valores.direccionMac} onChange={cambiar} placeholder="AA:BB:CC:DD:EE:FF" />
            </Campo>
          </div>
          <div className="dos">
            <Campo etiqueta="Tipo" error={errores.tipo}>
              <Selector name="tipo" value={valores.tipo} onChange={cambiar} opciones={catalogos.tiposDispositivo} />
            </Campo>
            <Campo etiqueta="Estado" error={errores.estado}>
              <Selector name="estado" value={valores.estado} onChange={cambiar} opciones={catalogos.estadosDispositivo} />
            </Campo>
          </div>
          <div className="acciones">
            <button className="boton" type="submit" disabled={enviando}>Guardar</button>
            <EnlaceBoton to="/dispositivos" fantasma>Cancelar</EnlaceBoton>
          </div>
        </form>
      </section>
    </>
  );
}
