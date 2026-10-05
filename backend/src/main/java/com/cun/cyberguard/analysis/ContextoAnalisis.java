package com.cun.cyberguard.analysis;

import com.cun.cyberguard.domain.Evento;
import com.cun.cyberguard.domain.Regla;

import java.util.List;

public record ContextoAnalisis(
        Evento evento,
        Regla regla,
        List<Evento> historial,
        boolean dispositivoRegistrado) {
}
