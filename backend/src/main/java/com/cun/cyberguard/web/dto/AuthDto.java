package com.cun.cyberguard.web.dto;

import com.cun.cyberguard.domain.Usuario;
import com.cun.cyberguard.domain.enums.Rol;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

public final class AuthDto {

    private AuthDto() {
    }

    public record LoginSolicitud(
            @NotBlank(message = "El usuario es obligatorio") String usuario,
            @NotBlank(message = "La contraseña es obligatoria") String contrasena) {
    }

    public record SesionUsuario(String nombre, String usuario, Rol rol) {

        public static SesionUsuario de(Usuario usuario) {
            return new SesionUsuario(usuario.getNombre(), usuario.getUsuario(), usuario.getRol());
        }
    }

    public record LoginRespuesta(String token, String tipo, Instant expiraEn, SesionUsuario usuario) {
    }
}
