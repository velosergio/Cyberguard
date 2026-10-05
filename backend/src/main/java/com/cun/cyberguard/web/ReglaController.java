package com.cun.cyberguard.web;

import com.cun.cyberguard.service.ReglaService;
import com.cun.cyberguard.web.dto.ReglaDto;
import com.cun.cyberguard.web.form.ReglaForm;
import jakarta.validation.Valid;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reglas")
public class ReglaController {

    private final ReglaService reglaService;

    public ReglaController(ReglaService reglaService) {
        this.reglaService = reglaService;
    }

    @GetMapping
    public List<ReglaDto> lista() {
        return reglaService.listar().stream().map(ReglaDto::de).toList();
    }

    @GetMapping("/{id}")
    public ReglaDto detalle(@PathVariable @NonNull Long id) {
        return ReglaDto.de(reglaService.obtener(id));
    }

    @PutMapping("/{id}")
    public ReglaDto actualizar(@PathVariable @NonNull Long id, @Valid @RequestBody ReglaForm form) {
        return ReglaDto.de(reglaService.actualizar(id, form));
    }
}
