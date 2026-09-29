package com.cun.cyberguard.domain.enums;

public enum Rol {
    ADMINISTRADOR("Administrador"),
    ANALISTA("Analista"),
    CONSULTA("Consulta");

    private final String etiqueta;

    Rol(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
