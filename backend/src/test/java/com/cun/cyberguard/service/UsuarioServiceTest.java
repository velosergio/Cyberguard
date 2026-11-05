package com.cun.cyberguard.service;

import com.cun.cyberguard.domain.Usuario;
import com.cun.cyberguard.domain.enums.Rol;
import com.cun.cyberguard.repository.UsuarioRepository;
import com.cun.cyberguard.web.form.UsuarioForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private UsuarioService servicio;

    @BeforeEach
    void preparar() {
        servicio = new UsuarioService(usuarioRepository, passwordEncoder);
    }

    @Test
    void crearUsuarioNuevoCifraLaContrasena() {
        when(usuarioRepository.existsByUsuario("nuevo")).thenReturn(false);
        when(usuarioRepository.existsByCorreo("nuevo@cyberguard.local")).thenReturn(false);
        when(passwordEncoder.encode("Clave123*")).thenReturn("hash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario guardado = servicio.guardar(formNuevo());

        assertEquals("nuevo", guardado.getUsuario());
        assertEquals("hash", guardado.getContrasena());
        assertEquals(Rol.CONSULTA, guardado.getRol());
    }

    @Test
    void crearSinContrasenaFalla() {
        UsuarioForm form = formNuevo();
        form.setContrasena(" ");

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> servicio.guardar(form));
        assertEquals("La contraseña es obligatoria", error.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void editarSinContrasenaConservaElHash() {
        Usuario existente = usuarioExistente();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.existsByUsuarioAndIdUsuariosNot("analista", 1L)).thenReturn(false);
        when(usuarioRepository.existsByCorreoAndIdUsuariosNot("analista@cyberguard.local", 1L)).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioForm form = servicio.aFormulario(existente);
        form.setNombre("Analista actualizado");
        form.setContrasena("");

        Usuario guardado = servicio.guardar(form);

        assertEquals("Analista actualizado", guardado.getNombre());
        assertEquals("hash-previo", guardado.getContrasena());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void noPermiteQuitarAlUltimoAdministrador() {
        Usuario admin = usuarioExistente();
        admin.setRol(Rol.ADMINISTRADOR);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(usuarioRepository.existsByUsuarioAndIdUsuariosNot("analista", 1L)).thenReturn(false);
        when(usuarioRepository.existsByCorreoAndIdUsuariosNot("analista@cyberguard.local", 1L)).thenReturn(false);
        when(usuarioRepository.countByRolAndActivo(Rol.ADMINISTRADOR, true)).thenReturn(1L);

        UsuarioForm form = servicio.aFormulario(admin);
        form.setRol(Rol.CONSULTA);

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> servicio.guardar(form));
        assertTrue(error.getMessage().contains("administrador"));
    }

    @Test
    void rechazaUsuarioDuplicado() {
        when(usuarioRepository.existsByUsuario("nuevo")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> servicio.guardar(formNuevo()));
    }

    @Test
    void obtenerInexistenteLanza404DeNegocio() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> servicio.obtener(99L));
    }

    @Test
    void aFormularioNoExponeLaContrasena() {
        UsuarioForm form = servicio.aFormulario(usuarioExistente());

        assertNull(form.getContrasena());
        assertEquals("analista", form.getUsuario());
    }

    private static UsuarioForm formNuevo() {
        UsuarioForm form = new UsuarioForm();
        form.setNombre("Usuario nuevo");
        form.setUsuario("nuevo");
        form.setCorreo("nuevo@cyberguard.local");
        form.setContrasena("Clave123*");
        form.setRol(Rol.CONSULTA);
        form.setActivo(true);
        return form;
    }

    private static Usuario usuarioExistente() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuarios(1L);
        usuario.setNombre("Analista");
        usuario.setUsuario("analista");
        usuario.setCorreo("analista@cyberguard.local");
        usuario.setContrasena("hash-previo");
        usuario.setRol(Rol.ANALISTA);
        usuario.setActivo(true);
        return usuario;
    }
}
