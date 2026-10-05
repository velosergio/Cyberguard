package com.cun.cyberguard.domain.enums;

public enum NivelRiesgo {
    BAJO("Bajo"),
    MEDIO("Medio"),
    ALTO("Alto"),
    CRITICO("Crítico");

    private final String etiqueta;

    NivelRiesgo(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
