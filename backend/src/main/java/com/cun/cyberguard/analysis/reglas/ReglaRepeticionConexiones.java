package com.cun.cyberguard.analysis.reglas;

import com.cun.cyberguard.analysis.CodigosRegla;
import com.cun.cyberguard.analysis.ContextoAnalisis;
import com.cun.cyberguard.analysis.ReglaDeteccion;
import com.cun.cyberguard.analysis.ResultadoDeteccion;
import com.cun.cyberguard.domain.Evento;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ReglaRepeticionConexiones implements ReglaDeteccion {

    @Override
    public String codigo() {
        return CodigosRegla.REPETICION_CONEXIONES;
    }

    @Override
    public Optional<ResultadoDeteccion> evaluar(ContextoAnalisis contexto) {
        long repeticiones = contexto.historial().stream()
                .filter(evento -> evento.getTipo() == contexto.evento().getTipo())
                .count();
        if (repeticiones < contexto.regla().getUmbral()) {
            return Optional.empty();
        }
        Evento evento = contexto.evento();
        return Optional.of(new ResultadoDeteccion(
                contexto.regla(),
                "La IP " + evento.getDireccionIp() + " repitió el evento "
                        + evento.getTipo().getEtiqueta() + " " + repeticiones + " veces."));
    }
}
