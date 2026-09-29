package com.cun.cyberguard.web.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ReglaForm {

    private Long id;

    @NotNull(message = "La puntuación es obligatoria")
    @Min(value = 1, message = "La puntuación mínima es 1")
    @Max(value = 100, message = "La puntuación máxima es 100")
    private Integer puntuacion;

    @NotNull(message = "El umbral es obligatorio")
    @Min(value = 1, message = "El umbral mínimo es 1")
    private Integer umbral;

    @NotNull(message = "La ventana es obligatoria")
    @Min(value = 0, message = "La ventana no puede ser negativa")
    @Max(value = 1440, message = "La ventana máxima es 1440 minutos")
    private Integer ventanaMinutos;

    private boolean activa = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(Integer puntuacion) {
        this.puntuacion = puntuacion;
    }

    public Integer getUmbral() {
        return umbral;
    }

    public void setUmbral(Integer umbral) {
        this.umbral = umbral;
    }

    public Integer getVentanaMinutos() {
        return ventanaMinutos;
    }

    public void setVentanaMinutos(Integer ventanaMinutos) {
        this.ventanaMinutos = ventanaMinutos;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }
}
