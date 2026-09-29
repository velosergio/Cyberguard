package com.cun.cyberguard.web;

import com.cun.cyberguard.domain.enums.EstadoDispositivo;
import com.cun.cyberguard.domain.enums.TipoDispositivo;
import com.cun.cyberguard.service.DispositivoService;
import com.cun.cyberguard.web.form.DispositivoForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DispositivoController {

    private final DispositivoService dispositivoService;

    public DispositivoController(DispositivoService dispositivoService) {
        this.dispositivoService = dispositivoService;
    }

    @GetMapping("/dispositivos")
    public String lista(Model model) {
        model.addAttribute("dispositivos", dispositivoService.listar());
        return "dispositivos/lista";
    }

    @GetMapping("/dispositivos/nuevo")
    public String nuevo(Model model) {
        preparar(model, new DispositivoForm(), true);
        return "dispositivos/form";
    }

    @GetMapping("/dispositivos/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        try {
            preparar(model, dispositivoService.aFormulario(dispositivoService.obtener(id)), false);
            return "dispositivos/form";
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
            return "redirect:/dispositivos";
        }
    }

    @PostMapping("/dispositivos")
    public String guardar(@Valid @ModelAttribute("form") DispositivoForm form, BindingResult result,
                          Model model, RedirectAttributes redirect) {
        if (result.hasErrors() || rechazo(result, () -> dispositivoService.guardar(form))) {
            preparar(model, form, form.getId() == null);
            return "dispositivos/form";
        }
        redirect.addFlashAttribute("mensaje", "Dispositivo guardado");
        return "redirect:/dispositivos";
    }

    private void preparar(Model model, DispositivoForm form, boolean nuevo) {
        model.addAttribute("form", form);
        model.addAttribute("nuevo", nuevo);
        model.addAttribute("tipos", TipoDispositivo.values());
        model.addAttribute("estados", EstadoDispositivo.values());
    }

    private boolean rechazo(BindingResult result, Runnable accion) {
        try {
            accion.run();
            return false;
        } catch (IllegalArgumentException ex) {
            result.reject("negocio", ex.getMessage());
            return true;
        }
    }
}
