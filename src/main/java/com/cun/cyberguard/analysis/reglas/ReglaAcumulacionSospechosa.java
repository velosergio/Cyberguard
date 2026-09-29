package com.cun.cyberguard.analysis.reglas;

import com.cun.cyberguard.analysis.CodigosRegla;
import com.cun.cyberguard.analysis.ContextoAnalisis;
import com.cun.cyberguard.analysis.ReglaDeteccion;
import com.cun.cyberguard.analysis.ResultadoDeteccion;
import com.cun.cyberguard.domain.enums.Importancia;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ReglaAcumulacionSospechosa implements ReglaDeteccion {

    @Override
    public String codigo() {
        return CodigosRegla.ACUMULACION_SOSPECHOSA;
    }

    @Override
    public Optional<ResultadoDeteccion> evaluar(ContextoAnalisis contexto) {
        if (contexto.evento().getImportancia() != Importancia.ALTA) {
            return Optional.empty();
        }
        long altos = contexto.historial().stream()
                .filter(evento -> evento.getImportancia() == Importancia.ALTA)
                .count();
        if (altos < contexto.regla().getUmbral()) {
            return Optional.empty();
        }
        return Optional.of(new ResultadoDeteccion(
                contexto.regla(),
                "La IP " + contexto.evento().getDireccionIp()
                        + " acumuló " + altos + " eventos de importancia alta."));
    }
}
