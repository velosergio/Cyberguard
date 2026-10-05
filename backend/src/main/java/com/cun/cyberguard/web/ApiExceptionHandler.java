package com.cun.cyberguard.web;

import com.cun.cyberguard.service.RecursoNoEncontradoException;
import com.cun.cyberguard.web.dto.ErrorRespuesta;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorRespuesta> validacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errores.putIfAbsent(error.getField(), String.valueOf(error.getDefaultMessage())));
        return ResponseEntity.badRequest().body(new ErrorRespuesta("Hay datos que no son válidos", errores));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorRespuesta> cuerpoIlegible() {
        return ResponseEntity.badRequest().body(ErrorRespuesta.de("El cuerpo de la petición no es válido"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorRespuesta> parametro(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.badRequest().body(ErrorRespuesta.de("El parámetro '" + ex.getName() + "' no es válido"));
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorRespuesta> noEncontrado(RecursoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorRespuesta.de(ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorRespuesta> reglaDeNegocio(IllegalArgumentException ex) {
        String mensaje = ex.getMessage();
        return ResponseEntity.badRequest().body(ErrorRespuesta.de(mensaje == null ? "Solicitud no válida" : mensaje));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorRespuesta> credenciales(AuthenticationException ex) {
        String mensaje = ex instanceof DisabledException
                ? "La cuenta está inactiva"
                : "Usuario o contraseña incorrectos";
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorRespuesta.de(mensaje));
    }
}
