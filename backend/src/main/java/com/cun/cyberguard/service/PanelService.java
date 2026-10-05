package com.cun.cyberguard.service;

import com.cun.cyberguard.domain.Alerta;
import com.cun.cyberguard.domain.Evento;
import com.cun.cyberguard.domain.enums.NivelRiesgo;
import com.cun.cyberguard.repository.AlertaRepository;
import com.cun.cyberguard.repository.DispositivoRepository;
import com.cun.cyberguard.repository.EventoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PanelService {

    private final DispositivoRepository dispositivoRepository;
    private final EventoRepository eventoRepository;
    private final AlertaRepository alertaRepository;

    public PanelService(
            DispositivoRepository dispositivoRepository,
            EventoRepository eventoRepository,
            AlertaRepository alertaRepository) {
        this.dispositivoRepository = dispositivoRepository;
        this.eventoRepository = eventoRepository;
        this.alertaRepository = alertaRepository;
    }

    @Transactional(readOnly = true)
    public PanelResumen resumir() {
        return new PanelResumen(
                dispositivoRepository.count(),
                eventoRepository.count(),
                alertaRepository.count(),
                alertaRepository.countByNivel(NivelRiesgo.ALTO),
                alertaRepository.countByNivel(NivelRiesgo.CRITICO),
                eventoRepository.findTop8ByOrderByFechaHoraDesc(),
                alertaRepository.findTop5ByOrderByFechaHoraDesc());
    }

    public record PanelResumen(
            long dispositivos,
            long eventos,
            long alertas,
            long alertasAltas,
            long alertasCriticas,
            List<Evento> eventosRecientes,
            List<Alerta> alertasRecientes) {
    }
}
