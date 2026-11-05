package com.cun.cyberguard.web.dto;

import com.cun.cyberguard.domain.Regla;

public record ReglaDto(
        Long idReglas,
        String codigo,
        String nombre,
        String descripcion,
        int puntuacion,
        int umbral,
        int ventanaMinutos,
        boolean activa) {

    public static ReglaDto de(Regla regla) {
        return new ReglaDto(
                regla.getIdReglas(),
                regla.getCodigo(),
                regla.getNombre(),
                regla.getDescripcion(),
                regla.getPuntuacion(),
                regla.getUmbral(),
                regla.getVentanaMinutos(),
                regla.isActiva());
    }
}
