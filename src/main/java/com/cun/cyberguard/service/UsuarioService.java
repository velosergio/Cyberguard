package com.cun.cyberguard.service;

import com.cun.cyberguard.domain.Usuario;
import com.cun.cyberguard.domain.enums.Rol;
import com.cun.cyberguard.repository.UsuarioRepository;
import com.cun.cyberguard.web.form.UsuarioForm;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Usuario obtener(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El usuario no existe"));
    }

    @Transactional
    public void guardar(UsuarioForm form) {
        boolean nuevo = form.getId() == null;
        String nombreUsuario = form.getUsuario().trim().toLowerCase();
        String correo = form.getCorreo().trim().toLowerCase();
        validarClave(form, nuevo);
        validarUnicos(nombreUsuario, correo, form.getId(), nuevo);

        Usuario usuario = nuevo ? new Usuario() : obtener(form.getId());
        validarAdministradorRestante(usuario, form, nuevo);

        usuario.setNombre(form.getNombre().trim());
        usuario.setUsuario(nombreUsuario);
        usuario.setCorreo(correo);
        usuario.setRol(form.getRol());
        usuario.setActivo(form.isActivo());
        if (form.getContrasena() != null && !form.getContrasena().isBlank()) {
            usuario.setContrasena(passwordEncoder.encode(form.getContrasena()));
        }
        usuarioRepository.save(usuario);
    }

    public UsuarioForm aFormulario(Usuario usuario) {
        UsuarioForm form = new UsuarioForm();
        form.setId(usuario.getId());
        form.setNombre(usuario.getNombre());
        form.setUsuario(usuario.getUsuario());
        form.setCorreo(usuario.getCorreo());
        form.setRol(usuario.getRol());
        form.setActivo(usuario.isActivo());
        return form;
    }

    private void validarClave(UsuarioForm form, boolean nuevo) {
        boolean vacia = form.getContrasena() == null || form.getContrasena().isBlank();
        if (nuevo && vacia) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }
        if (!vacia && form.getContrasena().length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
        }
    }

    private void validarUnicos(String usuario, String correo, Long id, boolean nuevo) {
        boolean usuarioRepetido = nuevo
                ? usuarioRepository.existsByUsuario(usuario)
                : usuarioRepository.existsByUsuarioAndIdNot(usuario, id);
        if (usuarioRepetido) {
            throw new IllegalArgumentException("Ese nombre de usuario ya está registrado");
        }
        boolean correoRepetido = nuevo
                ? usuarioRepository.existsByCorreo(correo)
                : usuarioRepository.existsByCorreoAndIdNot(correo, id);
        if (correoRepetido) {
            throw new IllegalArgumentException("Ese correo ya está registrado");
        }
    }

    private void validarAdministradorRestante(Usuario usuario, UsuarioForm form, boolean nuevo) {
        if (nuevo) {
            return;
        }
        Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacion != null && usuario.getUsuario().equals(autenticacion.getName()) && !form.isActivo()) {
            throw new IllegalArgumentException("No puedes desactivar tu propio usuario");
        }
        boolean dejaDeSerAdminActivo = usuario.getRol() == Rol.ADMINISTRADOR
                && usuario.isActivo()
                && (form.getRol() != Rol.ADMINISTRADOR || !form.isActivo());
        if (dejaDeSerAdminActivo && usuarioRepository.countByRolAndActivo(Rol.ADMINISTRADOR, true) <= 1) {
            throw new IllegalArgumentException("Debe quedar al menos un administrador activo");
        }
    }
}
