package com.cun.cyberguard.analysis;

import com.cun.cyberguard.analysis.reglas.ReglaAcumulacionSospechosa;
import com.cun.cyberguard.analysis.reglas.ReglaActividadInusual;
import com.cun.cyberguard.analysis.reglas.ReglaDispositivoNoRegistrado;
import com.cun.cyberguard.analysis.reglas.ReglaEscaneoPuertos;
import com.cun.cyberguard.analysis.reglas.ReglaMultiplesIntentos;
import com.cun.cyberguard.analysis.reglas.ReglaRepeticionConexiones;
import com.cun.cyberguard.domain.Dispositivo;
import com.cun.cyberguard.domain.Evento;
import com.cun.cyberguard.domain.Regla;
import com.cun.cyberguard.domain.enums.EstadoDispositivo;
import com.cun.cyberguard.domain.enums.Importancia;
import com.cun.cyberguard.domain.enums.NivelRiesgo;
import com.cun.cyberguard.domain.enums.TipoEvento;
import com.cun.cyberguard.repository.DispositivoRepository;
import com.cun.cyberguard.repository.EventoRepository;
import com.cun.cyberguard.repository.ReglaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MotorAnalisisTest {

    @Mock
    private ReglaRepository reglaRepository;
    @Mock
    private EventoRepository eventoRepository;
    @Mock
    private DispositivoRepository dispositivoRepository;

    private MotorAnalisis motor;

    @BeforeEach
    void preparar() {
        motor = new MotorAnalisis(
                reglaRepository,
                eventoRepository,
                dispositivoRepository,
                new ClasificadorRiesgo(),
                List.of(
                        new ReglaDispositivoNoRegistrado(),
                        new ReglaActividadInusual(),
                        new ReglaRepeticionConexiones(),
                        new ReglaMultiplesIntentos(),
                        new ReglaEscaneoPuertos(),
                        new ReglaAcumulacionSospechosa()));
    }

    @Test
    void detectaDispositivoNoRegistrado() {
        Evento evento = evento("10.0.0.8", TipoEvento.DISPOSITIVO_DESCONOCIDO, null, Importancia.MEDIA);
        when(dispositivoRepository.findByDireccionIp("10.0.0.8")).thenReturn(Optional.empty());
        when(reglaRepository.findByActivaTrue()).thenReturn(List.of(
                regla(CodigosRegla.DISPOSITIVO_NO_REGISTRADO, 10, 1, 0)));
        when(eventoRepository.historial(eq("10.0.0.8"), any())).thenReturn(List.of(evento));

        ResultadoAnalisis resultado = motor.analizar(evento);

        assertEquals(10, resultado.puntuacion());
        assertEquals(NivelRiesgo.BAJO, resultado.nivel());
        assertEquals(CodigosRegla.DISPOSITIVO_NO_REGISTRADO, resultado.detecciones().getFirst().regla().getCodigo());
    }

    @Test
    void detectaMultiplesIntentosDeConexion() {
        List<Evento> historial = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            historial.add(evento("192.168.1.20", TipoEvento.INTENTO_CONEXION, null, Importancia.MEDIA));
        }
        Evento actual = historial.getLast();
        registrarDispositivo("192.168.1.20");
        when(reglaRepository.findByActivaTrue()).thenReturn(List.of(
                regla(CodigosRegla.MULTIPLES_INTENTOS, 20, 8, 5)));
        when(eventoRepository.historial(eq("192.168.1.20"), any())).thenReturn(historial);

        ResultadoAnalisis resultado = motor.analizar(actual);

        assertEquals(20, resultado.puntuacion());
        assertEquals(NivelRiesgo.BAJO, resultado.nivel());
        assertTrue(resultado.hayDetecciones());
    }

    @Test
    void detectaEscaneoDePuertos() {
        int[] puertos = {22, 80, 443, 8080, 3306};
        List<Evento> historial = new ArrayList<>();
        for (int puerto : puertos) {
            historial.add(evento("192.168.1.30", TipoEvento.ACCESO_PUERTO, puerto, Importancia.MEDIA));
        }
        registrarDispositivo("192.168.1.30");
        when(reglaRepository.findByActivaTrue()).thenReturn(List.of(
                regla(CodigosRegla.ESCANEO_PUERTOS, 25, 5, 2)));
        when(eventoRepository.historial(eq("192.168.1.30"), any())).thenReturn(historial);

        ResultadoAnalisis resultado = motor.analizar(historial.getLast());

        assertEquals(25, resultado.puntuacion());
        assertEquals(NivelRiesgo.MEDIO, resultado.nivel());
    }

    @Test
    void sumaVariasReglasYRecortaEnCien() {
        Evento evento = evento("10.1.1.9", TipoEvento.ACTIVIDAD_INUSUAL, null, Importancia.ALTA);
        when(dispositivoRepository.findByDireccionIp("10.1.1.9")).thenReturn(Optional.empty());
        when(reglaRepository.findByActivaTrue()).thenReturn(List.of(
                regla(CodigosRegla.DISPOSITIVO_NO_REGISTRADO, 60, 1, 0),
                regla(CodigosRegla.ACTIVIDAD_INUSUAL, 60, 1, 60)));
        when(eventoRepository.historial(eq("10.1.1.9"), any())).thenReturn(List.of(evento));

        ResultadoAnalisis resultado = motor.analizar(evento);

        assertEquals(2, resultado.detecciones().size());
        assertEquals(100, resultado.puntuacion());
        assertEquals(NivelRiesgo.CRITICO, resultado.nivel());
    }

    private void registrarDispositivo(String ip) {
        Dispositivo dispositivo = new Dispositivo();
        dispositivo.setDireccionIp(ip);
        dispositivo.setEstado(EstadoDispositivo.ACTIVO);
        when(dispositivoRepository.findByDireccionIp(ip)).thenReturn(Optional.of(dispositivo));
    }

    private Evento evento(String ip, TipoEvento tipo, Integer puerto, Importancia importancia) {
        Evento evento = new Evento();
        evento.setDireccionIp(ip);
        evento.setTipo(tipo);
        evento.setPuerto(puerto);
        evento.setImportancia(importancia);
        evento.setFechaHora(LocalDateTime.of(2026, 9, 28, 10, 0));
        return evento;
    }

    private Regla regla(String codigo, int puntuacion, int umbral, int ventana) {
        Regla regla = new Regla();
        regla.setCodigo(codigo);
        regla.setNombre(codigo);
        regla.setDescripcion(codigo);
        regla.setPuntuacion(puntuacion);
        regla.setUmbral(umbral);
        regla.setVentanaMinutos(ventana);
        regla.setActiva(true);
        return regla;
    }
}
