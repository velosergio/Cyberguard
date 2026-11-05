package com.cun.cyberguard.service;

import com.cun.cyberguard.analysis.ResultadoAnalisis;
import com.cun.cyberguard.analysis.ResultadoDeteccion;
import com.cun.cyberguard.domain.Alerta;
import com.cun.cyberguard.domain.Evento;
import com.cun.cyberguard.domain.Regla;
import com.cun.cyberguard.domain.enums.EstadoAlerta;
import com.cun.cyberguard.domain.enums.NivelRiesgo;
import com.cun.cyberguard.repository.AlertaRepository;
import com.cun.cyberguard.web.form.AlertaEstadoForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlertaServiceTest {

    @Mock
    private AlertaRepository alertaRepository;

    private AlertaService servicio;

    @BeforeEach
    void preparar() {
        servicio = new AlertaService(alertaRepository);
    }

    @Test
    void crearPersisteUnaAlertaConLasReglasDetectadas() {
        Evento evento = new Evento();
        evento.setFechaHora(LocalDateTime.of(2026, 11, 5, 12, 0));
        Regla regla = new Regla();
        regla.setCodigo("ACTIVIDAD_INUSUAL");
        ResultadoAnalisis analisis = new ResultadoAnalisis(
                List.of(new ResultadoDeteccion(regla, "detalle")), 10, NivelRiesgo.BAJO);
        when(alertaRepository.save(any(Alerta.class))).thenAnswer(inv -> inv.getArgument(0));

        Alerta alerta = servicio.crear(evento, analisis);

        ArgumentCaptor<Alerta> capturado = ArgumentCaptor.forClass(Alerta.class);
        verify(alertaRepository).save(capturado.capture());
        assertEquals(EstadoAlerta.PENDIENTE, capturado.getValue().getEstado());
        assertEquals(10, capturado.getValue().getPuntuacion());
        assertEquals(Set.of(regla), capturado.getValue().getReglas());
        assertEquals(evento, alerta.getEvento());
    }

    @Test
    void actualizarEstadoCambiaEstadoYNota() {
        Alerta alerta = new Alerta();
        alerta.setIdAlertas(3L);
        alerta.setEstado(EstadoAlerta.PENDIENTE);
        alerta.setReglas(Set.of());
        when(alertaRepository.findById(3L)).thenReturn(Optional.of(alerta));

        AlertaEstadoForm form = new AlertaEstadoForm();
        form.setEstado(EstadoAlerta.EN_REVISION);
        form.setNota("  Revisando laboratorio  ");

        Alerta actualizada = servicio.actualizarEstado(3L, form);

        assertEquals(EstadoAlerta.EN_REVISION, actualizada.getEstado());
        assertEquals("Revisando laboratorio", actualizada.getNota());
    }

    @Test
    void obtenerInexistenteFalla() {
        when(alertaRepository.findConDetalle(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> servicio.obtener(99L));
    }
}
