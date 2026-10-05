package com.cun.cyberguard.domain.enums;

public enum TipoEvento {
    INTENTO_CONEXION("Intento de conexión"),
    ACCESO_PUERTO("Acceso a puerto"),
    ACTIVIDAD_INUSUAL("Actividad inusual"),
    DISPOSITIVO_DESCONOCIDO("Dispositivo desconocido"),
    OTRO("Otro");

    private final String etiqueta;

    TipoEvento(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
