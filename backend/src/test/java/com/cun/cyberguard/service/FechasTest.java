package com.cun.cyberguard.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FechasTest {

    @Test
    void inicioDevuelveMedianocheDelDia() {
        assertEquals(LocalDateTime.of(2026, 11, 5, 0, 0), Fechas.inicio(LocalDate.of(2026, 11, 5)));
    }

    @Test
    void finExclusivoDevuelveMedianocheDelDiaSiguiente() {
        assertEquals(LocalDateTime.of(2026, 11, 6, 0, 0), Fechas.finExclusivo(LocalDate.of(2026, 11, 5)));
    }

    @Test
    void nulosSePropagan() {
        assertNull(Fechas.inicio(null));
        assertNull(Fechas.finExclusivo(null));
    }
}
