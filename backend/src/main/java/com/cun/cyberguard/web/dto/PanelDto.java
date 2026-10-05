package com.cun.cyberguard.web.dto;

import com.cun.cyberguard.service.PanelService.PanelResumen;

import java.util.List;

public record PanelDto(
        long dispositivos,
        long eventos,
        long alertas,
        long alertasAltas,
        long alertasCriticas,
        List<EventoDto> eventosRecientes,
        List<AlertaDto> alertasRecientes) {

    public static PanelDto de(PanelResumen resumen) {
        return new PanelDto(
                resumen.dispositivos(),
                resumen.eventos(),
                resumen.alertas(),
                resumen.alertasAltas(),
                resumen.alertasCriticas(),
                resumen.eventosRecientes().stream().map(EventoDto::de).toList(),
                resumen.alertasRecientes().stream().map(AlertaDto::resumen).toList());
    }
}
