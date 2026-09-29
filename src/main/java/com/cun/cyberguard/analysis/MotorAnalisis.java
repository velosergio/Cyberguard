package com.cun.cyberguard.analysis;

import com.cun.cyberguard.domain.Evento;
import com.cun.cyberguard.domain.Regla;
import com.cun.cyberguard.domain.enums.EstadoDispositivo;
import com.cun.cyberguard.repository.DispositivoRepository;
import com.cun.cyberguard.repository.EventoRepository;
import com.cun.cyberguard.repository.ReglaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MotorAnalisis {

    private final ReglaRepository reglaRepository;
    private final EventoRepository eventoRepository;
    private final DispositivoRepository dispositivoRepository;
    private final ClasificadorRiesgo clasificador;
    private final Map<String, ReglaDeteccion> estrategias;

    public MotorAnalisis(
            ReglaRepository reglaRepository,
            EventoRepository eventoRepository,
            DispositivoRepository dispositivoRepository,
            ClasificadorRiesgo clasificador,
            List<ReglaDeteccion> reglas) {
        this.reglaRepository = reglaRepository;
        this.eventoRepository = eventoRepository;
        this.dispositivoRepository = dispositivoRepository;
        this.clasificador = clasificador;
        this.estrategias = new HashMap<>();
        for (ReglaDeteccion regla : reglas) {
            this.estrategias.put(regla.codigo(), regla);
        }
    }

    public ResultadoAnalisis analizar(Evento evento) {
        boolean registrado = dispositivoRepository.findByDireccionIp(evento.getDireccionIp())
                .filter(dispositivo -> dispositivo.getEstado() != EstadoDispositivo.DESCONOCIDO)
                .isPresent();

        List<ResultadoDeteccion> detecciones = new ArrayList<>();
        for (Regla regla : reglaRepository.findByActivaTrue()) {
            ReglaDeteccion estrategia = estrategias.get(regla.getCodigo());
            if (estrategia == null || !regla.isActiva()) {
                continue;
            }
            LocalDateTime desde = evento.getFechaHora().minusMinutes(Math.max(regla.getVentanaMinutos(), 0));
            List<Evento> historial = eventoRepository.historial(evento.getDireccionIp(), desde);
            estrategia.evaluar(new ContextoAnalisis(evento, regla, historial, registrado))
                    .ifPresent(detecciones::add);
        }

        int puntuacion = clasificador.limitar(detecciones.stream()
                .mapToInt(deteccion -> deteccion.regla().getPuntuacion())
                .sum());
        return new ResultadoAnalisis(detecciones, puntuacion, clasificador.clasificar(puntuacion));
    }
}
