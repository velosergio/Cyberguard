package com.cun.cyberguard.service;

public class RecursoNoEncontradoException extends IllegalArgumentException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
