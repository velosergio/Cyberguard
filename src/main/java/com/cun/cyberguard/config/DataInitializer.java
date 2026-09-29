package com.cun.cyberguard.config;

import com.cun.cyberguard.analysis.CodigosRegla;
import com.cun.cyberguard.domain.Dispositivo;
import com.cun.cyberguard.domain.Regla;
import com.cun.cyberguard.domain.Usuario;
import com.cun.cyberguard.domain.enums.EstadoDispositivo;
import com.cun.cyberguard.domain.enums.Importancia;
import com.cun.cyberguard.domain.enums.Rol;
import com.cun.cyberguard.domain.enums.TipoDispositivo;
import com.cun.cyberguard.domain.enums.TipoEvento;
import com.cun.cyberguard.repository.DispositivoRepository;
import com.cun.cyberguard.repository.EventoRepository;
import com.cun.cyberguard.repository.ReglaRepository;
import com.cun.cyberguard.repository.UsuarioRepository;
import com.cun.cyberguard.service.EventoService;
import com.cun.cyberguard.web.form.EventoForm;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    ApplicationRunner cargarDatos(
            UsuarioRepository usuarioRepository,
            ReglaRepository reglaRepository,
            DispositivoRepository dispositivoRepository,
            EventoRepository eventoRepository,
            EventoService eventoService,
            PasswordEncoder passwordEncoder) {
        return args -> {
            if (usuarioRepository.count() == 0) {
                usuarioRepository.save(usuario(passwordEncoder, "Administrador del laboratorio", "admin",
                        "admin@cyberguard.local", "Admin123*", Rol.ADMINISTRADOR));
                usuarioRepository.save(usuario(passwordEncoder, "Analista de eventos", "analista",
                        "analista@cyberguard.local", "Analista123*", Rol.ANALISTA));
                usuarioRepository.save(usuario(passwordEncoder, "Usuario de consulta", "consulta",
                        "consulta@cyberguard.local", "Consulta123*", Rol.CONSULTA));
            }
            if (reglaRepository.count() == 0) {
                reglaRepository.save(regla(CodigosRegla.DISPOSITIVO_NO_REGISTRADO, "Dispositivo no registrado",
                        "La IP del evento no pertenece a un dispositivo registrado.", 10, 1, 0));
                reglaRepository.save(regla(CodigosRegla.ACTIVIDAD_INUSUAL, "Actividad inusual",
                        "El evento fue clasificado como actividad inusual.", 10, 1, 60));
                reglaRepository.save(regla(CodigosRegla.REPETICION_CONEXIONES, "Repetición de conexiones",
                        "La misma IP repite un tipo de evento dentro de la ventana.", 15, 5, 10));
                reglaRepository.save(regla(CodigosRegla.MULTIPLES_INTENTOS, "Múltiples intentos de conexión",
                        "Una IP acumula intentos de conexión dentro de la ventana.", 20, 8, 5));
                reglaRepository.save(regla(CodigosRegla.ESCANEO_PUERTOS, "Escaneo de puertos",
                        "Una IP accede a varios puertos distintos en un periodo corto.", 25, 5, 2));
                reglaRepository.save(regla(CodigosRegla.ACUMULACION_SOSPECHOSA, "Acumulación de eventos sospechosos",
                        "Una IP acumula eventos de importancia alta.", 30, 4, 30));
            }
            if (dispositivoRepository.count() == 0) {
                dispositivoRepository.save(dispositivo("PC-Laboratorio", "192.168.10.11", "AA:BB:CC:DD:EE:01",
                        TipoDispositivo.COMPUTADOR));
                dispositivoRepository.save(dispositivo("Servidor-Academico", "192.168.10.20", "AA:BB:CC:DD:EE:02",
                        TipoDispositivo.SERVIDOR));
                dispositivoRepository.save(dispositivo("Camara-Pasillo", "192.168.10.30", "AA:BB:CC:DD:EE:03",
                        TipoDispositivo.CAMARA));
            }
            if (eventoRepository.count() == 0 && dispositivoRepository.count() > 0) {
                LocalDateTime ahora = LocalDateTime.now().withNano(0);
                Dispositivo pc = dispositivoRepository.findByDireccionIp("192.168.10.11").orElseThrow();
                Dispositivo servidor = dispositivoRepository.findByDireccionIp("192.168.10.20").orElseThrow();
                Dispositivo camara = dispositivoRepository.findByDireccionIp("192.168.10.30").orElseThrow();

                eventoService.registrar(evento(pc.getId(), pc.getDireccionIp(), TipoEvento.ACTIVIDAD_INUSUAL,
                        null, ahora.minusMinutes(40), "Tráfico fuera del horario de laboratorio", Importancia.MEDIA));
                eventoService.registrar(evento(null, "192.168.10.99", TipoEvento.DISPOSITIVO_DESCONOCIDO,
                        null, ahora.minusMinutes(35), "Equipo no inventariado", Importancia.MEDIA));

                for (int i = 0; i < 8; i++) {
                    eventoService.registrar(evento(servidor.getId(), servidor.getDireccionIp(), TipoEvento.INTENTO_CONEXION,
                            null, ahora.minusSeconds(80L - (i * 5L)), "Intento de conexión de laboratorio " + (i + 1),
                            Importancia.MEDIA));
                }
                int[] puertos = {22, 80, 443, 8080, 3306};
                for (int i = 0; i < puertos.length; i++) {
                    eventoService.registrar(evento(camara.getId(), camara.getDireccionIp(), TipoEvento.ACCESO_PUERTO,
                            puertos[i], ahora.minusSeconds(40L - (i * 4L)), "Acceso al puerto " + puertos[i],
                            Importancia.MEDIA));
                }
                for (int i = 0; i < 4; i++) {
                    eventoService.registrar(evento(pc.getId(), pc.getDireccionIp(), TipoEvento.OTRO,
                            null, ahora.minusMinutes(12L - i), "Evento de importancia alta " + (i + 1),
                            Importancia.ALTA));
                }
            }
        };
    }

    private Usuario usuario(PasswordEncoder encoder, String nombre, String usuario, String correo, String clave, Rol rol) {
        Usuario creado = new Usuario();
        creado.setNombre(nombre);
        creado.setUsuario(usuario);
        creado.setCorreo(correo);
        creado.setContrasena(encoder.encode(clave));
        creado.setRol(rol);
        creado.setActivo(true);
        return creado;
    }

    private Regla regla(String codigo, String nombre, String descripcion, int puntuacion, int umbral, int ventana) {
        Regla regla = new Regla();
        regla.setCodigo(codigo);
        regla.setNombre(nombre);
        regla.setDescripcion(descripcion);
        regla.setPuntuacion(puntuacion);
        regla.setUmbral(umbral);
        regla.setVentanaMinutos(ventana);
        regla.setActiva(true);
        return regla;
    }

    private Dispositivo dispositivo(String nombre, String ip, String mac, TipoDispositivo tipo) {
        Dispositivo dispositivo = new Dispositivo();
        dispositivo.setNombre(nombre);
        dispositivo.setDireccionIp(ip);
        dispositivo.setDireccionMac(mac);
        dispositivo.setTipo(tipo);
        dispositivo.setEstado(EstadoDispositivo.ACTIVO);
        return dispositivo;
    }

    private EventoForm evento(Long dispositivoId, String ip, TipoEvento tipo, Integer puerto,
                              LocalDateTime fecha, String detalle, Importancia importancia) {
        EventoForm form = new EventoForm();
        form.setDispositivoId(dispositivoId);
        form.setDireccionIp(ip);
        form.setTipo(tipo);
        form.setPuerto(puerto);
        form.setFechaHora(fecha);
        form.setDetalle(detalle);
        form.setImportancia(importancia);
        return form;
    }
}
