package com.cun.cyberguard.web.dto;

import com.cun.cyberguard.domain.Dispositivo;
import com.cun.cyberguard.domain.enums.EstadoDispositivo;
import com.cun.cyberguard.domain.enums.TipoDispositivo;

import java.time.LocalDateTime;

public record DispositivoDto(
        Long idDispositivos,
        String nombre,
        String direccionIp,
        String direccionMac,
        TipoDispositivo tipo,
        EstadoDispositivo estado,
        LocalDateTime fechaRegistro,
        LocalDateTime ultimaActividad) {

    public static DispositivoDto de(Dispositivo dispositivo) {
        return new DispositivoDto(
                dispositivo.getIdDispositivos(),
                dispositivo.getNombre(),
                dispositivo.getDireccionIp(),
                dispositivo.getDireccionMac(),
                dispositivo.getTipo(),
                dispositivo.getEstado(),
                dispositivo.getFechaRegistro(),
                dispositivo.getUltimaActividad());
    }
}
