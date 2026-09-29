package com.cun.cyberguard.analysis.reglas;

import com.cun.cyberguard.analysis.CodigosRegla;
import com.cun.cyberguard.analysis.ContextoAnalisis;
import com.cun.cyberguard.analysis.ReglaDeteccion;
import com.cun.cyberguard.analysis.ResultadoDeteccion;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ReglaDispositivoNoRegistrado implements ReglaDeteccion {

    @Override
    public String codigo() {
        return CodigosRegla.DISPOSITIVO_NO_REGISTRADO;
    }

    @Override
    public Optional<ResultadoDeteccion> evaluar(ContextoAnalisis contexto) {
        if (contexto.dispositivoRegistrado()) {
            return Optional.empty();
        }
        return Optional.of(new ResultadoDeteccion(
                contexto.regla(),
                "La dirección IP no corresponde a un dispositivo registrado en la red autorizada."));
    }
}
