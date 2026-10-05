package com.cun.cyberguard.analysis;

import com.cun.cyberguard.domain.enums.NivelRiesgo;

import java.util.List;

public record ResultadoAnalisis(List<ResultadoDeteccion> detecciones, int puntuacion, NivelRiesgo nivel) {

    public boolean hayDetecciones() {
        return !detecciones.isEmpty();
    }
}
