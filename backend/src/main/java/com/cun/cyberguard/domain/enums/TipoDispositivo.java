package com.cun.cyberguard.domain.enums;

public enum TipoDispositivo {
    COMPUTADOR("Computador"),
    TELEFONO("Teléfono"),
    SERVIDOR("Servidor"),
    IMPRESORA("Impresora"),
    CAMARA("Cámara"),
    OTRO("Otro");

    private final String etiqueta;

    TipoDispositivo(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
