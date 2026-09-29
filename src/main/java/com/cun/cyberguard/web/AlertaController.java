package com.cun.cyberguard.web;

import com.cun.cyberguard.domain.Alerta;
import com.cun.cyberguard.domain.enums.EstadoAlerta;
import com.cun.cyberguard.domain.enums.NivelRiesgo;
import com.cun.cyberguard.service.AlertaService;
import com.cun.cyberguard.web.form.AlertaEstadoForm;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
public class AlertaController {

    private final AlertaService alertaService;

    public AlertaController(AlertaService alertaService) {
        this.alertaService = alertaService;
    }

    @GetMapping("/alertas")
    public String lista(
            @RequestParam(required = false) NivelRiesgo nivel,
            @RequestParam(required = false) EstadoAlerta estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            Model model) {
        model.addAttribute("alertas", alertaService.buscar(nivel, estado, desde, hasta));
        model.addAttribute("niveles", NivelRiesgo.values());
        model.addAttribute("estados", EstadoAlerta.values());
        model.addAttribute("nivel", nivel);
        model.addAttribute("estado", estado);
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        return "alertas/lista";
    }

    @GetMapping("/alertas/{id}")
    public String detalle(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        try {
            Alerta alerta = alertaService.obtener(id);
            AlertaEstadoForm form = new AlertaEstadoForm();
            form.setEstado(alerta.getEstado());
            form.setNota(alerta.getNota());
            model.addAttribute("alerta", alerta);
            model.addAttribute("form", form);
            model.addAttribute("estados", EstadoAlerta.values());
            return "alertas/detalle";
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
            return "redirect:/alertas";
        }
    }

    @PostMapping("/alertas/{id}/estado")
    public String estado(@PathVariable Long id, @Valid @ModelAttribute("form") AlertaEstadoForm form,
                         BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("alerta", alertaService.obtener(id));
            model.addAttribute("estados", EstadoAlerta.values());
            return "alertas/detalle";
        }
        try {
            alertaService.actualizarEstado(id, form);
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
            return "redirect:/alertas";
        }
        redirect.addFlashAttribute("mensaje", "Estado de la alerta actualizado");
        return "redirect:/alertas/" + id;
    }
}
