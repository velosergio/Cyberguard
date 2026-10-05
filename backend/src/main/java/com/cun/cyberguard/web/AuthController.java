package com.cun.cyberguard.web;

import com.cun.cyberguard.config.TokenService;
import com.cun.cyberguard.config.TokenService.TokenEmitido;
import com.cun.cyberguard.service.UsuarioService;
import com.cun.cyberguard.web.dto.AuthDto.LoginRespuesta;
import com.cun.cyberguard.web.dto.AuthDto.LoginSolicitud;
import com.cun.cyberguard.web.dto.AuthDto.SesionUsuario;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UsuarioService usuarioService;

    public AuthController(
            AuthenticationManager authenticationManager, TokenService tokenService, UsuarioService usuarioService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public LoginRespuesta login(@Valid @RequestBody LoginSolicitud solicitud) {
        Authentication autenticacion = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(solicitud.usuario().trim(), solicitud.contrasena()));
        TokenEmitido emitido = tokenService.emitir(autenticacion);
        SesionUsuario usuario = SesionUsuario.de(usuarioService.obtenerPorUsuario(autenticacion.getName()));
        return new LoginRespuesta(emitido.token(), "Bearer", emitido.expiraEn(), usuario);
    }

    @GetMapping("/me")
    public SesionUsuario yo(Authentication autenticacion) {
        return SesionUsuario.de(usuarioService.obtenerPorUsuario(autenticacion.getName()));
    }
}
