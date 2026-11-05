package com.cun.cyberguard.web.dto;

import com.cun.cyberguard.domain.Alerta;
import com.cun.cyberguard.domain.enums.EstadoAlerta;
import com.cun.cyberguard.domain.enums.NivelRiesgo;

import java.time.LocalDateTime;
import java.util.List;

public record AlertaDto(
        Long idAlertas,
        LocalDateTime fechaHora,
        int puntuacion,
        NivelRiesgo nivel,
        EstadoAlerta estado,
        String nota,
        EventoDto evento,
        DispositivoDto dispositivo,
        List<ReglaDto> reglas) {

    /** Incluye las reglas activadas. Exige que la colección ya esté cargada. */
    public static AlertaDto de(Alerta alerta) {
        return crear(alerta, alerta.getReglas().stream().map(ReglaDto::de).toList());
    }

    /** Versión para el panel: no toca la colección perezosa de reglas. */
    public static AlertaDto resumen(Alerta alerta) {
        return crear(alerta, List.of());
    }

    private static AlertaDto crear(Alerta alerta, List<ReglaDto> reglas) {
        return new AlertaDto(
                alerta.getIdAlertas(),
                alerta.getFechaHora(),
                alerta.getPuntuacion(),
                alerta.getNivel(),
                alerta.getEstado(),
                alerta.getNota(),
                EventoDto.de(alerta.getEvento()),
                alerta.getDispositivo() == null ? null : DispositivoDto.de(alerta.getDispositivo()),
                reglas);
    }
}
