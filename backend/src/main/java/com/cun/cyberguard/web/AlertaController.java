package com.cun.cyberguard.web;

import com.cun.cyberguard.domain.enums.EstadoAlerta;
import com.cun.cyberguard.domain.enums.NivelRiesgo;
import com.cun.cyberguard.service.AlertaService;
import com.cun.cyberguard.web.dto.AlertaDto;
import com.cun.cyberguard.web.form.AlertaEstadoForm;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/alertas")
public class AlertaController {

    private final AlertaService alertaService;

    public AlertaController(AlertaService alertaService) {
        this.alertaService = alertaService;
    }

    @GetMapping
    public List<AlertaDto> lista(
            @RequestParam(required = false) NivelRiesgo nivel,
            @RequestParam(required = false) EstadoAlerta estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return alertaService.buscar(nivel, estado, desde, hasta).stream().map(AlertaDto::de).toList();
    }

    @GetMapping("/{id}")
    public AlertaDto detalle(@PathVariable Long id) {
        return AlertaDto.de(alertaService.obtener(id));
    }

    @PutMapping("/{id}/estado")
    public AlertaDto estado(@PathVariable @NonNull Long id, @Valid @RequestBody AlertaEstadoForm form) {
        return AlertaDto.de(alertaService.actualizarEstado(id, form));
    }
}
