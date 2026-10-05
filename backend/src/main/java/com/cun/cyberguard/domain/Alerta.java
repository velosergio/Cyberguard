package com.cun.cyberguard.domain;

import com.cun.cyberguard.domain.enums.EstadoAlerta;
import com.cun.cyberguard.domain.enums.NivelRiesgo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "alertas")
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dispositivo_id")
    private Dispositivo dispositivo;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false)
    private int puntuacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private NivelRiesgo nivel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoAlerta estado = EstadoAlerta.PENDIENTE;

    @Column(length = 500)
    private String nota;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "alerta_regla",
            joinColumns = @JoinColumn(name = "alerta_id"),
            inverseJoinColumns = @JoinColumn(name = "regla_id"))
    private Set<Regla> reglas = new LinkedHashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public Dispositivo getDispositivo() {
        return dispositivo;
    }

    public void setDispositivo(Dispositivo dispositivo) {
        this.dispositivo = dispositivo;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    public NivelRiesgo getNivel() {
        return nivel;
    }

    public void setNivel(NivelRiesgo nivel) {
        this.nivel = nivel;
    }

    public EstadoAlerta getEstado() {
        return estado;
    }

    public void setEstado(EstadoAlerta estado) {
        this.estado = estado;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public Set<Regla> getReglas() {
        return reglas;
    }

    public void setReglas(Set<Regla> reglas) {
        this.reglas = reglas;
    }
}
