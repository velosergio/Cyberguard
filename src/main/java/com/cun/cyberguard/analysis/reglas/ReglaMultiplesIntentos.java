package com.cun.cyberguard.analysis.reglas;

import com.cun.cyberguard.analysis.CodigosRegla;
import com.cun.cyberguard.analysis.ContextoAnalisis;
import com.cun.cyberguard.analysis.ReglaDeteccion;
import com.cun.cyberguard.analysis.ResultadoDeteccion;
import com.cun.cyberguard.domain.enums.TipoEvento;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ReglaMultiplesIntentos implements ReglaDeteccion {

    @Override
    public String codigo() {
        return CodigosRegla.MULTIPLES_INTENTOS;
    }

    @Override
    public Optional<ResultadoDeteccion> evaluar(ContextoAnalisis contexto) {
        if (contexto.evento().getTipo() != TipoEvento.INTENTO_CONEXION) {
            return Optional.empty();
        }
        long intentos = contexto.historial().stream()
                .filter(evento -> evento.getTipo() == TipoEvento.INTENTO_CONEXION)
                .count();
        if (intentos < contexto.regla().getUmbral()) {
            return Optional.empty();
        }
        return Optional.of(new ResultadoDeteccion(
                contexto.regla(),
                "La IP " + contexto.evento().getDireccionIp()
                        + " acumuló " + intentos + " intentos de conexión."));
    }
}
