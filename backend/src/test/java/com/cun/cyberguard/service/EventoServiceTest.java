package com.cun.cyberguard.service;

import com.cun.cyberguard.analysis.MotorAnalisis;
import com.cun.cyberguard.analysis.ResultadoAnalisis;
import com.cun.cyberguard.domain.Dispositivo;
import com.cun.cyberguard.domain.Evento;
import com.cun.cyberguard.domain.enums.EstadoDispositivo;
import com.cun.cyberguard.domain.enums.Importancia;
import com.cun.cyberguard.domain.enums.NivelRiesgo;
import com.cun.cyberguard.domain.enums.TipoDispositivo;
import com.cun.cyberguard.domain.enums.TipoEvento;
import com.cun.cyberguard.repository.DispositivoRepository;
import com.cun.cyberguard.repository.EventoRepository;
import com.cun.cyberguard.web.form.EventoForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Cómo el registro de un evento mantiene la última actividad del dispositivo. */
@ExtendWith(MockitoExtension.class)
class EventoServiceTest {

    private static final String IP = "192.168.10.11";
    private static final LocalDateTime ULTIMA = LocalDateTime.of(2026, 10, 1, 12, 0);

    @Mock
    private EventoRepository eventoRepository;
    @Mock
    private DispositivoRepository dispositivoRepository;
    @Mock
    private MotorAnalisis motorAnalisis;
    @Mock
    private AlertaService alertaService;

    private EventoService servicio;

    @BeforeEach
    void preparar() {
        servicio = new EventoService(eventoRepository, dispositivoRepository, motorAnalisis, alertaService);
    }

    @Test
    void eventoPosteriorAvanzaLaUltimaActividad() {
        Dispositivo dispositivo = dispositivo(EstadoDispositivo.ACTIVO, ULTIMA);
        prepararRegistro(dispositivo);

        servicio.registrar(form(ULTIMA.plusHours(1)));

        assertEquals(ULTIMA.plusHours(1), dispositivo.getUltimaActividad());
        verify(dispositivoRepository).save(dispositivo);
    }

    @Test
    void eventoConFechaPasadaNoRetrocedeLaUltimaActividad() {
        Dispositivo dispositivo = dispositivo(EstadoDispositivo.ACTIVO, ULTIMA);
        prepararRegistro(dispositivo);

        servicio.registrar(form(ULTIMA.minusDays(3)));

        assertEquals(ULTIMA, dispositivo.getUltimaActividad());
        verify(dispositivoRepository, never()).save(any());
    }

    @Test
    void eventoConLaMismaFechaNoReescribeLaUltimaActividad() {
        Dispositivo dispositivo = dispositivo(EstadoDispositivo.ACTIVO, ULTIMA);
        prepararRegistro(dispositivo);

        servicio.registrar(form(ULTIMA));

        assertEquals(ULTIMA, dispositivo.getUltimaActividad());
        verify(dispositivoRepository, never()).save(any());
    }

    @Test
    void primerEventoDelDispositivoFijaLaUltimaActividad() {
        Dispositivo dispositivo = dispositivo(EstadoDispositivo.ACTIVO, null);
        prepararRegistro(dispositivo);

        servicio.registrar(form(ULTIMA.minusDays(3)));

        assertEquals(ULTIMA.minusDays(3), dispositivo.getUltimaActividad());
        verify(dispositivoRepository).save(dispositivo);
    }

    @Test
    void dispositivoDesconocidoNoRegistraActividad() {
        Dispositivo dispositivo = dispositivo(EstadoDispositivo.DESCONOCIDO, null);
        prepararRegistro(dispositivo);

        servicio.registrar(form(ULTIMA));

        assertNull(dispositivo.getUltimaActividad());
        verify(dispositivoRepository, never()).save(any());
    }

    private void prepararRegistro(Dispositivo dispositivo) {
        when(dispositivoRepository.findByDireccionIp(IP)).thenReturn(Optional.of(dispositivo));
        when(eventoRepository.save(any(Evento.class))).thenAnswer(invocacion -> invocacion.getArgument(0));
        when(motorAnalisis.analizar(any(Evento.class)))
                .thenReturn(new ResultadoAnalisis(List.of(), 0, NivelRiesgo.BAJO));
    }

    private static Dispositivo dispositivo(EstadoDispositivo estado, LocalDateTime ultimaActividad) {
        Dispositivo dispositivo = new Dispositivo();
        dispositivo.setIdDispositivos(1L);
        dispositivo.setNombre("PC-Laboratorio");
        dispositivo.setDireccionIp(IP);
        dispositivo.setTipo(TipoDispositivo.COMPUTADOR);
        dispositivo.setEstado(estado);
        dispositivo.setUltimaActividad(ultimaActividad);
        return dispositivo;
    }

    private static EventoForm form(LocalDateTime fechaHora) {
        EventoForm form = new EventoForm();
        form.setDireccionIp(IP);
        form.setTipo(TipoEvento.INTENTO_CONEXION);
        form.setFechaHora(fechaHora);
        form.setImportancia(Importancia.MEDIA);
        return form;
    }
}
