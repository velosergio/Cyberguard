package com.cun.cyberguard.web;

import com.cun.cyberguard.service.UsuarioService;
import com.cun.cyberguard.web.dto.UsuarioDto;
import com.cun.cyberguard.web.form.UsuarioForm;
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
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<UsuarioDto> lista() {
        return usuarioService.listar().stream().map(UsuarioDto::de).toList();
    }

    @GetMapping("/{id}")
    public UsuarioDto detalle(@PathVariable @NonNull Long id) {
        return UsuarioDto.de(usuarioService.obtener(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioDto crear(@Valid @RequestBody UsuarioForm form) {
        form.setIdUsuarios(null);
        return UsuarioDto.de(usuarioService.guardar(form));
    }

    @PutMapping("/{id}")
    public UsuarioDto actualizar(@PathVariable @NonNull Long id, @Valid @RequestBody UsuarioForm form) {
        form.setIdUsuarios(id);
        return UsuarioDto.de(usuarioService.guardar(form));
    }
}
