package com.cun.cyberguard.web.dto;

import java.util.List;

public record ReporteDto(List<EventoDto> eventos, List<AlertaDto> alertas) {
}
