package com.cun.cyberguard.analysis.reglas;

import com.cun.cyberguard.analysis.CodigosRegla;
import com.cun.cyberguard.analysis.ContextoAnalisis;
import com.cun.cyberguard.analysis.ReglaDeteccion;
import com.cun.cyberguard.analysis.ResultadoDeteccion;
import com.cun.cyberguard.domain.enums.TipoEvento;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ReglaActividadInusual implements ReglaDeteccion {

    @Override
    public String codigo() {
        return CodigosRegla.ACTIVIDAD_INUSUAL;
    }

    @Override
    public Optional<ResultadoDeteccion> evaluar(ContextoAnalisis contexto) {
        if (contexto.evento().getTipo() != TipoEvento.ACTIVIDAD_INUSUAL) {
            return Optional.empty();
        }
        return Optional.of(new ResultadoDeteccion(
                contexto.regla(),
                "Se registró una actividad marcada como inusual."));
    }
}
