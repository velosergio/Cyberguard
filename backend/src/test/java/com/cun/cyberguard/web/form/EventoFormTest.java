package com.cun.cyberguard.web.form;

import com.cun.cyberguard.domain.enums.TipoEvento;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventoFormTest {

    private final Validator validador = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void rechazaFechaFutura() {
        Set<ConstraintViolation<EventoForm>> errores = validador.validate(form(LocalDateTime.now().plusDays(1)));

        assertEquals(1, errores.size());
        assertEquals("fechaHora", errores.iterator().next().getPropertyPath().toString());
    }

    @Test
    void aceptaFechaPasada() {
        assertTrue(validador.validate(form(LocalDateTime.now().minusHours(1))).isEmpty());
    }

    private EventoForm form(LocalDateTime fechaHora) {
        EventoForm form = new EventoForm();
        form.setDireccionIp("192.168.1.50");
        form.setTipo(TipoEvento.INTENTO_CONEXION);
        form.setFechaHora(fechaHora);
        return form;
    }
}
