package com.cun.cyberguard.service;

import com.cun.cyberguard.domain.Alerta;
import com.cun.cyberguard.domain.Evento;
import com.cun.cyberguard.domain.enums.EstadoAlerta;
import com.cun.cyberguard.domain.enums.NivelRiesgo;
import com.cun.cyberguard.domain.enums.TipoEvento;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReporteService {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final EventoService eventoService;
    private final AlertaService alertaService;

    public ReporteService(EventoService eventoService, AlertaService alertaService) {
        this.eventoService = eventoService;
        this.alertaService = alertaService;
    }

    @Transactional(readOnly = true)
    public List<Evento> eventos(String ip, TipoEvento tipo, LocalDate desde, LocalDate hasta) {
        return eventoService.buscar(ip, tipo, desde, hasta);
    }

    @Transactional(readOnly = true)
    public List<Alerta> alertas(NivelRiesgo nivel, EstadoAlerta estado, LocalDate desde, LocalDate hasta) {
        return alertaService.buscar(nivel, estado, desde, hasta);
    }

    public String eventosCsv(List<Evento> eventos) {
        StringBuilder csv = new StringBuilder();
        csv.append('\uFEFF');
        csv.append("id;fecha;ip;dispositivo;tipo;puerto;importancia;detalle\n");
        for (Evento evento : eventos) {
            csv.append(evento.getId()).append(';')
                    .append(evento.getFechaHora().format(FECHA)).append(';')
                    .append(csv(evento.getDireccionIp())).append(';')
                    .append(csv(evento.getDispositivo() == null ? "" : evento.getDispositivo().getNombre())).append(';')
                    .append(csv(evento.getTipo().getEtiqueta())).append(';')
                    .append(evento.getPuerto() == null ? "" : evento.getPuerto()).append(';')
                    .append(csv(evento.getImportancia().getEtiqueta())).append(';')
                    .append(csv(evento.getDetalle()))
                    .append('\n');
        }
        return csv.toString();
    }

    public String alertasCsv(List<Alerta> alertas) {
        StringBuilder csv = new StringBuilder();
        csv.append('\uFEFF');
        csv.append("id;fecha;ip;nivel;puntuacion;estado;reglas\n");
        for (Alerta alerta : alertas) {
            String reglas = alerta.getReglas().stream()
                    .map(regla -> regla.getNombre())
                    .reduce((a, b) -> a + " | " + b)
                    .orElse("");
            csv.append(alerta.getId()).append(';')
                    .append(alerta.getFechaHora().format(FECHA)).append(';')
                    .append(csv(alerta.getEvento().getDireccionIp())).append(';')
                    .append(csv(alerta.getNivel().getEtiqueta())).append(';')
                    .append(alerta.getPuntuacion()).append(';')
                    .append(csv(alerta.getEstado().getEtiqueta())).append(';')
                    .append(csv(reglas))
                    .append('\n');
        }
        return csv.toString();
    }

    private String csv(String valor) {
        if (valor == null || valor.isBlank()) {
            return "";
        }
        String limpio = valor.replace("\"", "\"\"").replace('\r', ' ').replace('\n', ' ');
        if (limpio.contains(";") || limpio.contains("\"") || limpio.contains(",")) {
            return "\"" + limpio + "\"";
        }
        return limpio;
    }
}
