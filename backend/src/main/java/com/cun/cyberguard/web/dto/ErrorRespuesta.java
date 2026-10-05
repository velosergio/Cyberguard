package com.cun.cyberguard.web.dto;

import java.util.Map;

/** Cuerpo de error de la API. {@code errores} va indexado por nombre de campo cuando la validación falla. */
public record ErrorRespuesta(String mensaje, Map<String, String> errores) {

    public static ErrorRespuesta de(String mensaje) {
        return new ErrorRespuesta(mensaje, Map.of());
    }
}
