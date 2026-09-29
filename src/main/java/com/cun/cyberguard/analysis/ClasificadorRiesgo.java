package com.cun.cyberguard.analysis;

import com.cun.cyberguard.domain.enums.NivelRiesgo;
import org.springframework.stereotype.Component;

@Component
public class ClasificadorRiesgo {

    public int limitar(int puntuacion) {
        return Math.min(100, Math.max(0, puntuacion));
    }

    public NivelRiesgo clasificar(int puntuacion) {
        int valor = limitar(puntuacion);
        if (valor <= 20) {
            return NivelRiesgo.BAJO;
        }
        if (valor <= 50) {
            return NivelRiesgo.MEDIO;
        }
        if (valor <= 80) {
            return NivelRiesgo.ALTO;
        }
        return NivelRiesgo.CRITICO;
    }
}
