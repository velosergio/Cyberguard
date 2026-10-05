package com.cun.cyberguard.web.form;

import com.cun.cyberguard.domain.enums.EstadoAlerta;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AlertaEstadoForm {

    @NotNull(message = "El estado es obligatorio")
    private EstadoAlerta estado;

    @Size(max = 500, message = "La nota admite hasta 500 caracteres")
    private String nota;

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
}
