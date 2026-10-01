package com.cun.cyberguard.analysis.reglas;

import com.cun.cyberguard.analysis.CodigosRegla;
import com.cun.cyberguard.analysis.ContextoAnalisis;
import com.cun.cyberguard.analysis.ReglaDeteccion;
import com.cun.cyberguard.analysis.ResultadoDeteccion;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class ReglaEscaneoPuertos implements ReglaDeteccion {

    @Override
    public String codigo() {
        return CodigosRegla.ESCANEO_PUERTOS;
    }

    @Override
    public Optional<ResultadoDeteccion> evaluar(ContextoAnalisis contexto) {
        if (contexto.evento().getPuerto() == null) {
            return Optional.empty();
        }
        long puertos = contexto.historial().stream()
                .map(evento -> evento.getPuerto())
                .filter(Objects::nonNull)
                .distinct()
                .count();
        if (puertos < contexto.regla().getUmbral()) {
            return Optional.empty();
        }
        return Optional.of(new ResultadoDeteccion(
                contexto.regla(),
                "La IP " + contexto.evento().getDireccionIp()
                        + " accedió a " + puertos + " puertos distintos."));
    }
}
