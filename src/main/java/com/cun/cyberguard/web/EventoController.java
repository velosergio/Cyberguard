package com.cun.cyberguard.web;

import com.cun.cyberguard.domain.enums.Importancia;
import com.cun.cyberguard.domain.enums.TipoEvento;
import com.cun.cyberguard.service.DispositivoService;
import com.cun.cyberguard.service.EventoService;
import com.cun.cyberguard.web.form.EventoForm;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
public class EventoController {

    private final EventoService eventoService;
    private final DispositivoService dispositivoService;

    public EventoController(EventoService eventoService, DispositivoService dispositivoService) {
        this.eventoService = eventoService;
        this.dispositivoService = dispositivoService;
    }

    @GetMapping("/eventos")
    public String lista(
            @RequestParam(required = false) String ip,
            @RequestParam(required = false) TipoEvento tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            Model model) {
        model.addAttribute("eventos", eventoService.buscar(ip, tipo, desde, hasta));
        model.addAttribute("tipos", TipoEvento.values());
        model.addAttribute("ip", ip);
        model.addAttribute("tipo", tipo);
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        return "eventos/lista";
    }

    @GetMapping("/eventos/nuevo")
    public String nuevo(Model model) {
        preparar(model, new EventoForm());
        return "eventos/form";
    }

    @PostMapping("/eventos")
    public String guardar(@Valid @ModelAttribute("form") EventoForm form, BindingResult result,
                          Model model, RedirectAttributes redirect) {
        if (!result.hasErrors()) {
            try {
                var evento = eventoService.registrar(form);
                redirect.addFlashAttribute("mensaje", "Evento registrado. Si cumplió alguna regla, quedó en alertas.");
                redirect.addFlashAttribute("eventoId", evento.getId());
                return "redirect:/eventos";
            } catch (IllegalArgumentException ex) {
                result.reject("negocio", ex.getMessage());
            }
        }
        preparar(model, form);
        return "eventos/form";
    }

    private void preparar(Model model, EventoForm form) {
        model.addAttribute("form", form);
        model.addAttribute("dispositivos", dispositivoService.listar());
        model.addAttribute("tipos", TipoEvento.values());
        model.addAttribute("importancias", Importancia.values());
    }
}
