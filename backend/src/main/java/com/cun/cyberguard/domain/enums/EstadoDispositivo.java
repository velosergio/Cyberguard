package com.cun.cyberguard.domain.enums;

public enum EstadoDispositivo {
    ACTIVO("Activo"),
    INACTIVO("Inactivo"),
    DESCONOCIDO("Desconocido");

    private final String etiqueta;

    EstadoDispositivo(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
