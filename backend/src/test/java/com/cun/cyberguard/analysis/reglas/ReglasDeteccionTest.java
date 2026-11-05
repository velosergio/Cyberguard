package com.cun.cyberguard.analysis.reglas;

import com.cun.cyberguard.analysis.ContextoAnalisis;
import com.cun.cyberguard.analysis.ReglaDeteccion;
import com.cun.cyberguard.analysis.ResultadoDeteccion;
import com.cun.cyberguard.domain.Evento;
import com.cun.cyberguard.domain.Regla;
import com.cun.cyberguard.domain.enums.Importancia;
import com.cun.cyberguard.domain.enums.TipoEvento;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Cada regla: un caso que cumple y otro que no. */
class ReglasDeteccionTest {

    private static final LocalDateTime FECHA = LocalDateTime.of(2026, 11, 5, 10, 0);

    @Test
    void dispositivoNoRegistradoDetectaIpDesconocida() {
        ReglaDeteccion regla = new ReglaDispositivoNoRegistrado();
        Evento evento = evento("10.0.0.9", TipoEvento.DISPOSITIVO_DESCONOCIDO, null, Importancia.MEDIA);

        Optional<ResultadoDeteccion> resultado = regla.evaluar(contexto(evento, regla(1), List.of(evento), false));

        assertTrue(resultado.isPresent());
    }

    @Test
    void dispositivoNoRegistradoIgnoraIpInventariada() {
        ReglaDeteccion regla = new ReglaDispositivoNoRegistrado();
        Evento evento = evento("192.168.10.11", TipoEvento.INTENTO_CONEXION, null, Importancia.MEDIA);

        Optional<ResultadoDeteccion> resultado = regla.evaluar(contexto(evento, regla(1), List.of(evento), true));

        assertTrue(resultado.isEmpty());
    }

    @Test
    void actividadInusualDetectaTipoMarcado() {
        ReglaDeteccion regla = new ReglaActividadInusual();
        Evento evento = evento("192.168.10.11", TipoEvento.ACTIVIDAD_INUSUAL, null, Importancia.MEDIA);

        assertTrue(regla.evaluar(contexto(evento, regla(1), List.of(evento), true)).isPresent());
    }

    @Test
    void actividadInusualIgnoraOtrosTipos() {
        ReglaDeteccion regla = new ReglaActividadInusual();
        Evento evento = evento("192.168.10.11", TipoEvento.INTENTO_CONEXION, null, Importancia.MEDIA);

        assertTrue(regla.evaluar(contexto(evento, regla(1), List.of(evento), true)).isEmpty());
    }

    @Test
    void repeticionConexionesCumpleUmbral() {
        ReglaDeteccion regla = new ReglaRepeticionConexiones();
        List<Evento> historial = historial(TipoEvento.INTENTO_CONEXION, 5, null, Importancia.MEDIA);
        Evento actual = historial.getLast();

        assertTrue(regla.evaluar(contexto(actual, regla(5), historial, true)).isPresent());
    }

    @Test
    void repeticionConexionesBajoUmbralNoDispara() {
        ReglaDeteccion regla = new ReglaRepeticionConexiones();
        List<Evento> historial = historial(TipoEvento.INTENTO_CONEXION, 4, null, Importancia.MEDIA);
        Evento actual = historial.getLast();

        assertTrue(regla.evaluar(contexto(actual, regla(5), historial, true)).isEmpty());
    }

    @Test
    void multiplesIntentosCumpleUmbral() {
        ReglaDeteccion regla = new ReglaMultiplesIntentos();
        List<Evento> historial = historial(TipoEvento.INTENTO_CONEXION, 8, null, Importancia.MEDIA);

        assertTrue(regla.evaluar(contexto(historial.getLast(), regla(8), historial, true)).isPresent());
    }

    @Test
    void multiplesIntentosIgnoraOtroTipo() {
        ReglaDeteccion regla = new ReglaMultiplesIntentos();
        Evento evento = evento("192.168.1.20", TipoEvento.ACCESO_PUERTO, 80, Importancia.MEDIA);

        assertTrue(regla.evaluar(contexto(evento, regla(1), List.of(evento), true)).isEmpty());
    }

    @Test
    void escaneoPuertosCumpleUmbralDePuertosDistintos() {
        ReglaDeteccion regla = new ReglaEscaneoPuertos();
        int[] puertos = {22, 80, 443, 8080, 3306};
        List<Evento> historial = new ArrayList<>();
        for (int puerto : puertos) {
            historial.add(evento("192.168.1.30", TipoEvento.ACCESO_PUERTO, puerto, Importancia.MEDIA));
        }

        assertTrue(regla.evaluar(contexto(historial.getLast(), regla(5), historial, true)).isPresent());
    }

    @Test
    void escaneoPuertosSinPuertoNoDispara() {
        ReglaDeteccion regla = new ReglaEscaneoPuertos();
        Evento evento = evento("192.168.1.30", TipoEvento.INTENTO_CONEXION, null, Importancia.MEDIA);

        assertTrue(regla.evaluar(contexto(evento, regla(1), List.of(evento), true)).isEmpty());
    }

    @Test
    void acumulacionSospechosaCumpleUmbralDeImportanciaAlta() {
        ReglaDeteccion regla = new ReglaAcumulacionSospechosa();
        List<Evento> historial = historial(TipoEvento.OTRO, 4, null, Importancia.ALTA);

        assertTrue(regla.evaluar(contexto(historial.getLast(), regla(4), historial, true)).isPresent());
    }

    @Test
    void acumulacionSospechosaIgnoraImportanciaMedia() {
        ReglaDeteccion regla = new ReglaAcumulacionSospechosa();
        Evento evento = evento("192.168.1.40", TipoEvento.OTRO, null, Importancia.MEDIA);

        assertTrue(regla.evaluar(contexto(evento, regla(1), List.of(evento), true)).isEmpty());
    }

    @Test
    void todasLasReglasExponenCodigoNoVacio() {
        List<ReglaDeteccion> reglas = List.of(
                new ReglaDispositivoNoRegistrado(),
                new ReglaActividadInusual(),
                new ReglaRepeticionConexiones(),
                new ReglaMultiplesIntentos(),
                new ReglaEscaneoPuertos(),
                new ReglaAcumulacionSospechosa());

        for (ReglaDeteccion regla : reglas) {
            assertFalse(regla.codigo().isBlank());
        }
    }

    private static List<Evento> historial(TipoEvento tipo, int cantidad, Integer puerto, Importancia importancia) {
        List<Evento> lista = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            lista.add(evento("192.168.1.50", tipo, puerto, importancia));
        }
        return lista;
    }

    private static ContextoAnalisis contexto(
            Evento evento, Regla regla, List<Evento> historial, boolean registrado) {
        return new ContextoAnalisis(evento, regla, historial, registrado);
    }

    private static Regla regla(int umbral) {
        Regla regla = new Regla();
        regla.setCodigo("PRUEBA");
        regla.setNombre("Prueba");
        regla.setDescripcion("Prueba");
        regla.setPuntuacion(10);
        regla.setUmbral(umbral);
        regla.setVentanaMinutos(10);
        regla.setActiva(true);
        return regla;
    }

    private static Evento evento(String ip, TipoEvento tipo, Integer puerto, Importancia importancia) {
        Evento evento = new Evento();
        evento.setDireccionIp(ip);
        evento.setTipo(tipo);
        evento.setPuerto(puerto);
        evento.setImportancia(importancia);
        evento.setFechaHora(FECHA);
        return evento;
    }
}
