package com.cun.cyberguard.analysis;

import com.cun.cyberguard.domain.enums.NivelRiesgo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClasificadorRiesgoTest {

    private final ClasificadorRiesgo clasificador = new ClasificadorRiesgo();

    @Test
    void clasificaLosLimitesDelDocumento() {
        assertEquals(NivelRiesgo.BAJO, clasificador.clasificar(0));
        assertEquals(NivelRiesgo.BAJO, clasificador.clasificar(20));
        assertEquals(NivelRiesgo.MEDIO, clasificador.clasificar(21));
        assertEquals(NivelRiesgo.MEDIO, clasificador.clasificar(50));
        assertEquals(NivelRiesgo.ALTO, clasificador.clasificar(51));
        assertEquals(NivelRiesgo.ALTO, clasificador.clasificar(80));
        assertEquals(NivelRiesgo.CRITICO, clasificador.clasificar(81));
        assertEquals(NivelRiesgo.CRITICO, clasificador.clasificar(100));
    }

    @Test
    void limitaLaPuntuacionEntreCeroYCien() {
        assertEquals(0, clasificador.limitar(-5));
        assertEquals(100, clasificador.limitar(150));
        assertEquals(NivelRiesgo.CRITICO, clasificador.clasificar(150));
    }
}
