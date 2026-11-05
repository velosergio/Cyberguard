package com.cun.cyberguard.web.form;

import com.cun.cyberguard.domain.enums.EstadoDispositivo;
import com.cun.cyberguard.domain.enums.TipoDispositivo;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DispositivoFormTest {

    private final Validator validador = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void aceptaDispositivoValido() {
        assertTrue(validador.validate(formValido()).isEmpty());
    }

    @Test
    void rechazaIpInvalida() {
        DispositivoForm form = formValido();
        form.setDireccionIp("192.168.1");

        Set<ConstraintViolation<DispositivoForm>> errores = validador.validate(form);

        assertFalse(errores.isEmpty());
        assertTrue(errores.stream().anyMatch(e -> e.getPropertyPath().toString().equals("direccionIp")));
    }

    @Test
    void aceptaMacVaciaOValida() {
        DispositivoForm sinMac = formValido();
        sinMac.setDireccionMac("");
        assertTrue(validador.validate(sinMac).isEmpty());

        DispositivoForm conMac = formValido();
        conMac.setDireccionMac("AA:BB:CC:DD:EE:FF");
        assertTrue(validador.validate(conMac).isEmpty());
    }

    @Test
    void rechazaMacInvalida() {
        DispositivoForm form = formValido();
        form.setDireccionMac("AA-BB");

        assertTrue(validador.validate(form).stream()
                .anyMatch(e -> e.getPropertyPath().toString().equals("direccionMac")));
    }

    private DispositivoForm formValido() {
        DispositivoForm form = new DispositivoForm();
        form.setNombre("PC-Prueba");
        form.setDireccionIp("192.168.10.50");
        form.setTipo(TipoDispositivo.COMPUTADOR);
        form.setEstado(EstadoDispositivo.ACTIVO);
        return form;
    }
}
