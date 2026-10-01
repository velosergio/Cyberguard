package com.cun.cyberguard.service;

import com.cun.cyberguard.analysis.ResultadoAnalisis;
import com.cun.cyberguard.domain.Alerta;
import com.cun.cyberguard.domain.Evento;
import com.cun.cyberguard.domain.Regla;
import com.cun.cyberguard.domain.enums.EstadoAlerta;
import com.cun.cyberguard.domain.enums.NivelRiesgo;
import com.cun.cyberguard.repository.AlertaRepository;
import com.cun.cyberguard.web.form.AlertaEstadoForm;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AlertaService {

    private final AlertaRepository alertaRepository;

    public AlertaService(AlertaRepository alertaRepository) {
        this.alertaRepository = alertaRepository;
    }

    @Transactional
    public Alerta crear(Evento evento, ResultadoAnalisis analisis) {
        Alerta alerta = new Alerta();
        alerta.setEvento(evento);
        alerta.setDispositivo(evento.getDispositivo());
        alerta.setFechaHora(evento.getFechaHora());
        alerta.setPuntuacion(analisis.puntuacion());
        alerta.setNivel(analisis.nivel());
        alerta.setEstado(EstadoAlerta.PENDIENTE);
        Set<Regla> reglas = analisis.detecciones().stream()
                .map(deteccion -> deteccion.regla())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        alerta.setReglas(reglas);
        return alertaRepository.save(alerta);
    }

    @Transactional(readOnly = true)
    public List<Alerta> buscar(NivelRiesgo nivel, EstadoAlerta estado, LocalDate desde, LocalDate hasta) {
        return alertaRepository.buscar(nivel, estado, Fechas.inicio(desde), Fechas.finExclusivo(hasta));
    }

    @Transactional(readOnly = true)
    public Alerta obtener(Long id) {
        Alerta alerta = alertaRepository.findConDetalle(id)
                .orElseThrow(() -> new IllegalArgumentException("La alerta no existe"));
        alerta.getReglas().size();
        return alerta;
    }

    @Transactional
    public void actualizarEstado(@NonNull Long id, AlertaEstadoForm form) {
        Alerta alerta = alertaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La alerta no existe"));
        alerta.setEstado(form.getEstado());
        alerta.setNota(form.getNota() == null ? null : form.getNota().trim());
    }
}
