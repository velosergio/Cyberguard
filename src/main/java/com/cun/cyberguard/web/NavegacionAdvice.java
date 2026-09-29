package com.cun.cyberguard.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class NavegacionAdvice {

    @ModelAttribute("ruta")
    public String ruta(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
