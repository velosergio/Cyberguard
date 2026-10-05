package com.cun.cyberguard.web.dto;

import com.cun.cyberguard.domain.Evento;
import com.cun.cyberguard.domain.enums.Importancia;
import com.cun.cyberguard.domain.enums.TipoEvento;

import java.time.LocalDateTime;

public record EventoDto(
        Long id,
        Long dispositivoId,
        String dispositivoNombre,
        String direccionIp,
        TipoEvento tipo,
        Integer puerto,
        LocalDateTime fechaHora,
        String detalle,
        Importancia importancia) {

    public static EventoDto de(Evento evento) {
        var dispositivo = evento.getDispositivo();
        return new EventoDto(
                evento.getId(),
                dispositivo == null ? null : dispositivo.getId(),
                dispositivo == null ? null : dispositivo.getNombre(),
                evento.getDireccionIp(),
                evento.getTipo(),
                evento.getPuerto(),
                evento.getFechaHora(),
                evento.getDetalle(),
                evento.getImportancia());
    }
}
