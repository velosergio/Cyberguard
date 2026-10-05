package com.cun.cyberguard.domain.enums;

public enum EstadoAlerta {
    PENDIENTE("Pendiente"),
    EN_REVISION("En revisión"),
    ATENDIDA("Atendida"),
    CERRADA("Cerrada");

    private final String etiqueta;

    EstadoAlerta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
