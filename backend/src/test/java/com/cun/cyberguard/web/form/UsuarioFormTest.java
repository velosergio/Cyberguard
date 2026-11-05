package com.cun.cyberguard.web.form;

import com.cun.cyberguard.domain.enums.Rol;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UsuarioFormTest {

    private final Validator validador = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void aceptaUsuarioValido() {
        assertTrue(validador.validate(formValido()).isEmpty());
    }

    @Test
    void rechazaUsuarioConCaracteresInvalidos() {
        UsuarioForm form = formValido();
        form.setUsuario("malo espacio");

        Set<String> campos = validador.validate(form).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .collect(Collectors.toSet());

        assertTrue(campos.contains("usuario"));
    }

    @Test
    void rechazaCorreoInvalido() {
        UsuarioForm form = formValido();
        form.setCorreo("sin-arroba");

        assertTrue(validador.validate(form).stream()
                .anyMatch(e -> e.getPropertyPath().toString().equals("correo")));
    }

    private UsuarioForm formValido() {
        UsuarioForm form = new UsuarioForm();
        form.setNombre("Usuario de prueba");
        form.setUsuario("prueba.01");
        form.setCorreo("prueba@cyberguard.local");
        form.setRol(Rol.CONSULTA);
        form.setActivo(true);
        return form;
    }
}
