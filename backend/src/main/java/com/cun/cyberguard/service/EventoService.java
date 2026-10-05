package com.cun.cyberguard.service;

import com.cun.cyberguard.analysis.MotorAnalisis;
import com.cun.cyberguard.analysis.ResultadoAnalisis;
import com.cun.cyberguard.domain.Dispositivo;
import com.cun.cyberguard.domain.Evento;
import com.cun.cyberguard.domain.enums.EstadoDispositivo;
import com.cun.cyberguard.domain.enums.TipoEvento;
import com.cun.cyberguard.repository.DispositivoRepository;
import com.cun.cyberguard.repository.EventoRepository;
import com.cun.cyberguard.web.form.EventoForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final DispositivoRepository dispositivoRepository;
    private final MotorAnalisis motorAnalisis;
    private final AlertaService alertaService;

    public EventoService(
            EventoRepository eventoRepository,
            DispositivoRepository dispositivoRepository,
            MotorAnalisis motorAnalisis,
            AlertaService alertaService) {
        this.eventoRepository = eventoRepository;
        this.dispositivoRepository = dispositivoRepository;
        this.motorAnalisis = motorAnalisis;
        this.alertaService = alertaService;
    }

    @Transactional
    public Evento registrar(EventoForm form) {
        if (form.getTipo() == TipoEvento.ACCESO_PUERTO && form.getPuerto() == null) {
            throw new IllegalArgumentException("El acceso a puerto requiere un número de puerto");
        }

        Dispositivo dispositivo = resolverDispositivo(form);
        String ip = dispositivo != null ? dispositivo.getDireccionIp() : form.getDireccionIp().trim();

        Evento evento = new Evento();
        evento.setDispositivo(dispositivo);
        evento.setDireccionIp(ip);
        evento.setTipo(form.getTipo());
        evento.setPuerto(form.getPuerto());
        evento.setFechaHora(form.getFechaHora());
        evento.setDetalle(form.getDetalle() == null ? null : form.getDetalle().trim());
        evento.setImportancia(form.getImportancia());
        Evento guardado = eventoRepository.save(evento);

        if (dispositivo != null && dispositivo.getEstado() != EstadoDispositivo.DESCONOCIDO) {
            dispositivo.setUltimaActividad(guardado.getFechaHora());
            dispositivoRepository.save(dispositivo);
        }

        ResultadoAnalisis analisis = motorAnalisis.analizar(guardado);
        if (analisis.hayDetecciones()) {
            alertaService.crear(guardado, analisis);
        }
        return guardado;
    }

    @Transactional(readOnly = true)
    public List<Evento> buscar(String ip, TipoEvento tipo, LocalDate desde, LocalDate hasta) {
        String filtroIp = ip == null ? null : ip.trim();
        return eventoRepository.buscar(filtroIp, tipo, Fechas.inicio(desde), Fechas.finExclusivo(hasta));
    }

    private Dispositivo resolverDispositivo(EventoForm form) {
        Long dispositivoId = form.getDispositivoId();
        if (dispositivoId != null) {
            return dispositivoRepository.findById(dispositivoId)
                    .orElseThrow(() -> new IllegalArgumentException("El dispositivo seleccionado no existe"));
        }
        return dispositivoRepository.findByDireccionIp(form.getDireccionIp().trim()).orElse(null);
    }
}
