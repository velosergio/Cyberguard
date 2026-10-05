package com.cun.cyberguard.analysis;

import java.util.Optional;

public interface ReglaDeteccion {

    String codigo();

    Optional<ResultadoDeteccion> evaluar(ContextoAnalisis contexto);
}
