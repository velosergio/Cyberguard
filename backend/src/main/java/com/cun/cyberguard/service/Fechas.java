package com.cun.cyberguard.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class Fechas {

    private Fechas() {
    }

    public static LocalDateTime inicio(LocalDate fecha) {
        return fecha == null ? null : fecha.atStartOfDay();
    }

    public static LocalDateTime finExclusivo(LocalDate fecha) {
        return fecha == null ? null : fecha.plusDays(1).atStartOfDay();
    }
}
