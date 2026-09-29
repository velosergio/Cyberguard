package com.cun.cyberguard.web;

import com.cun.cyberguard.domain.Regla;
import com.cun.cyberguard.service.ReglaService;
import com.cun.cyberguard.web.form.ReglaForm;
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
public class ReglaController {

    private final ReglaService reglaService;

    public ReglaController(ReglaService reglaService) {
        this.reglaService = reglaService;
    }

    @GetMapping("/reglas")
    public String lista(Model model) {
        model.addAttribute("reglas", reglaService.listar());
        return "reglas/lista";
    }

    @GetMapping("/reglas/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        try {
            Regla regla = reglaService.obtener(id);
            model.addAttribute("regla", regla);
            model.addAttribute("form", reglaService.aFormulario(regla));
            return "reglas/form";
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
            return "redirect:/reglas";
        }
    }

    @PostMapping("/reglas/{id}")
    public String guardar(@PathVariable Long id, @Valid @ModelAttribute("form") ReglaForm form,
                          BindingResult result, Model model, RedirectAttributes redirect) {
        Regla regla;
        try {
            regla = reglaService.obtener(id);
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
            return "redirect:/reglas";
        }
        if (result.hasErrors()) {
            model.addAttribute("regla", regla);
            return "reglas/form";
        }
        reglaService.actualizar(id, form);
        redirect.addFlashAttribute("mensaje", "Regla actualizada");
        return "redirect:/reglas";
    }
}
