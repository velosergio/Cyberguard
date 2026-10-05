package com.cun.cyberguard.analysis;

import com.cun.cyberguard.domain.Regla;

public record ResultadoDeteccion(Regla regla, String detalle) {
}
