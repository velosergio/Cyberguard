package com.cun.cyberguard.web;

import com.cun.cyberguard.domain.enums.TipoEvento;
import com.cun.cyberguard.service.EventoService;
import com.cun.cyberguard.web.dto.EventoDto;
import com.cun.cyberguard.web.form.EventoForm;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @GetMapping
    public List<EventoDto> lista(
            @RequestParam(required = false) String ip,
            @RequestParam(required = false) TipoEvento tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return eventoService.buscar(ip, tipo, desde, hasta).stream().map(EventoDto::de).toList();
    }

    /** Registra el evento y lo analiza: si cumple alguna regla, queda una alerta en /api/alertas. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventoDto registrar(@Valid @RequestBody EventoForm form) {
        return EventoDto.de(eventoService.registrar(form));
    }
}
