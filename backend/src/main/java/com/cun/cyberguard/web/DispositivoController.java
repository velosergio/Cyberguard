package com.cun.cyberguard.web;

import com.cun.cyberguard.service.DispositivoService;
import com.cun.cyberguard.web.dto.DispositivoDto;
import com.cun.cyberguard.web.form.DispositivoForm;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dispositivos")
public class DispositivoController {

    private final DispositivoService dispositivoService;

    public DispositivoController(DispositivoService dispositivoService) {
        this.dispositivoService = dispositivoService;
    }

    @GetMapping
    public List<DispositivoDto> lista() {
        return dispositivoService.listar().stream().map(DispositivoDto::de).toList();
    }

    @GetMapping("/{id}")
    public DispositivoDto detalle(@PathVariable @NonNull Long id) {
        return DispositivoDto.de(dispositivoService.obtener(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DispositivoDto crear(@Valid @RequestBody DispositivoForm form) {
        form.setId(null);
        return DispositivoDto.de(dispositivoService.guardar(form));
    }

    @PutMapping("/{id}")
    public DispositivoDto actualizar(@PathVariable @NonNull Long id, @Valid @RequestBody DispositivoForm form) {
        form.setId(id);
        return DispositivoDto.de(dispositivoService.guardar(form));
    }
}
