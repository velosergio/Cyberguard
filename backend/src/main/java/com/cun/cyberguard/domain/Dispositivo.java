package com.cun.cyberguard.domain;

import com.cun.cyberguard.domain.enums.EstadoDispositivo;
import com.cun.cyberguard.domain.enums.TipoDispositivo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "dispositivos")
public class Dispositivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dispositivos")
    private Long idDispositivos;

    @Column(nullable = false, unique = true, length = 45)
    private String direccionIp;

    @Column(length = 17)
    private String direccionMac;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoDispositivo tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoDispositivo estado;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    private LocalDateTime ultimaActividad;

    public Long getIdDispositivos() {
        return idDispositivos;
    }

    public void setIdDispositivos(Long idDispositivos) {
        this.idDispositivos = idDispositivos;
    }

    public String getDireccionIp() {
        return direccionIp;
    }

    public void setDireccionIp(String direccionIp) {
        this.direccionIp = direccionIp;
    }

    public String getDireccionMac() {
        return direccionMac;
    }

    public void setDireccionMac(String direccionMac) {
        this.direccionMac = direccionMac;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoDispositivo getTipo() {
        return tipo;
    }

    public void setTipo(TipoDispositivo tipo) {
        this.tipo = tipo;
    }

    public EstadoDispositivo getEstado() {
        return estado;
    }

    public void setEstado(EstadoDispositivo estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public LocalDateTime getUltimaActividad() {
        return ultimaActividad;
    }

    public void setUltimaActividad(LocalDateTime ultimaActividad) {
        this.ultimaActividad = ultimaActividad;
    }

    /**
     * Anota un evento del equipo. La última actividad solo avanza: un evento con fecha
     * anterior a la guardada no la hace retroceder. Un equipo desconocido no registra actividad.
     *
     * @return {@code true} si la última actividad cambió y hay que guardar el dispositivo
     */
    public boolean registrarActividad(LocalDateTime fecha) {
        if (fecha == null || estado == EstadoDispositivo.DESCONOCIDO) {
            return false;
        }
        if (ultimaActividad != null && !fecha.isAfter(ultimaActividad)) {
            return false;
        }
        ultimaActividad = fecha;
        return true;
    }
}
