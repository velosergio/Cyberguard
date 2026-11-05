package com.cun.cyberguard.domain;

import com.cun.cyberguard.domain.enums.EstadoDispositivo;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DispositivoTest {

    private static final LocalDateTime BASE = LocalDateTime.of(2026, 11, 5, 10, 0);

    @Test
    void desconocidoNuncaRegistraActividad() {
        Dispositivo dispositivo = new Dispositivo();
        dispositivo.setEstado(EstadoDispositivo.DESCONOCIDO);

        assertFalse(dispositivo.registrarActividad(BASE));
    }

    @Test
    void avanzaSoloConFechaPosterior() {
        Dispositivo dispositivo = new Dispositivo();
        dispositivo.setEstado(EstadoDispositivo.ACTIVO);
        dispositivo.setUltimaActividad(BASE);

        assertFalse(dispositivo.registrarActividad(BASE.minusHours(1)));
        assertEquals(BASE, dispositivo.getUltimaActividad());

        assertTrue(dispositivo.registrarActividad(BASE.plusHours(1)));
        assertEquals(BASE.plusHours(1), dispositivo.getUltimaActividad());
    }
}
