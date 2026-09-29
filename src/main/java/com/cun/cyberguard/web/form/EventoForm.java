package com.cun.cyberguard.web.form;

import com.cun.cyberguard.domain.enums.Importancia;
import com.cun.cyberguard.domain.enums.TipoEvento;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class EventoForm {

    private Long dispositivoId;

    @NotBlank(message = "La dirección IP es obligatoria")
    @Pattern(
            regexp = "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$",
            message = "La dirección IP no es válida")
    private String direccionIp;

    @NotNull(message = "El tipo de evento es obligatorio")
    private TipoEvento tipo;

    @Min(value = 1, message = "El puerto debe estar entre 1 y 65535")
    @Max(value = 65535, message = "El puerto debe estar entre 1 y 65535")
    private Integer puerto;

    @NotNull(message = "La fecha y hora son obligatorias")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime fechaHora;

    @Size(max = 500, message = "El detalle admite hasta 500 caracteres")
    private String detalle;

    @NotNull(message = "La importancia es obligatoria")
    private Importancia importancia = Importancia.MEDIA;

    public Long getDispositivoId() {
        return dispositivoId;
    }

    public void setDispositivoId(Long dispositivoId) {
        this.dispositivoId = dispositivoId;
    }

    public String getDireccionIp() {
        return direccionIp;
    }

    public void setDireccionIp(String direccionIp) {
        this.direccionIp = direccionIp;
    }

    public TipoEvento getTipo() {
        return tipo;
    }

    public void setTipo(TipoEvento tipo) {
        this.tipo = tipo;
    }

    public Integer getPuerto() {
        return puerto;
    }

    public void setPuerto(Integer puerto) {
        this.puerto = puerto;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public Importancia getImportancia() {
        return importancia;
    }

    public void setImportancia(Importancia importancia) {
        this.importancia = importancia;
    }
}
