package com.cun.cyberguard.repository;

import com.cun.cyberguard.domain.Evento;
import com.cun.cyberguard.domain.enums.Importancia;
import com.cun.cyberguard.domain.enums.TipoEvento;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EventoRepositoryTest {

    private static final String IP = "192.168.1.40";
    private static final LocalDateTime BASE = LocalDateTime.of(2026, 10, 1, 10, 0);

    @Autowired
    private EventoRepository eventoRepository;

    @Test
    void historialDeUnEventoPasadoNoIncluyeEventosPosteriores() {
        // Cinco eventos actuales y uno registrado después con fecha de una hora antes.
        for (int i = 0; i < 5; i++) {
            guardar(IP, BASE.plusMinutes(60 + i));
        }
        Evento pasado = guardar(IP, BASE);

        List<Evento> historial = eventoRepository.historial(IP, BASE.minusMinutes(5), pasado.getFechaHora());

        assertEquals(1, historial.size());
        assertEquals(BASE, historial.getFirst().getFechaHora());
    }

    @Test
    void historialIncluyeLosLimitesDeLaVentanaYFiltraPorIp() {
        guardar(IP, BASE.minusMinutes(10));
        guardar(IP, BASE.minusMinutes(5));
        guardar(IP, BASE);
        guardar("192.168.1.41", BASE);

        List<Evento> historial = eventoRepository.historial(IP, BASE.minusMinutes(5), BASE);

        assertEquals(List.of(BASE.minusMinutes(5), BASE),
                historial.stream().map(Evento::getFechaHora).toList());
    }

    private Evento guardar(String ip, LocalDateTime fechaHora) {
        Evento evento = new Evento();
        evento.setDireccionIp(ip);
        evento.setTipo(TipoEvento.INTENTO_CONEXION);
        evento.setImportancia(Importancia.MEDIA);
        evento.setFechaHora(fechaHora);
        return eventoRepository.save(evento);
    }
}
