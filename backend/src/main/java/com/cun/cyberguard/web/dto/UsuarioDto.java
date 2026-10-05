package com.cun.cyberguard.web.dto;

import com.cun.cyberguard.domain.Usuario;
import com.cun.cyberguard.domain.enums.Rol;

import java.time.LocalDateTime;

public record UsuarioDto(
        Long id,
        String nombre,
        String usuario,
        String correo,
        Rol rol,
        boolean activo,
        LocalDateTime creadoEn) {

    public static UsuarioDto de(Usuario usuario) {
        return new UsuarioDto(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getUsuario(),
                usuario.getCorreo(),
                usuario.getRol(),
                usuario.isActivo(),
                usuario.getCreadoEn());
    }
}
