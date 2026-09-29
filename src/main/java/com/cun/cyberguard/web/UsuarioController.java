package com.cun.cyberguard.web;

import com.cun.cyberguard.domain.enums.Rol;
import com.cun.cyberguard.service.UsuarioService;
import com.cun.cyberguard.web.form.UsuarioForm;
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
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/usuarios")
    public String lista(Model model) {
        model.addAttribute("usuarios", usuarioService.listar());
        return "usuarios/lista";
    }

    @GetMapping("/usuarios/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("form", new UsuarioForm());
        model.addAttribute("nuevo", true);
        model.addAttribute("roles", Rol.values());
        return "usuarios/form";
    }

    @GetMapping("/usuarios/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        try {
            model.addAttribute("form", usuarioService.aFormulario(usuarioService.obtener(id)));
            model.addAttribute("nuevo", false);
            model.addAttribute("roles", Rol.values());
            return "usuarios/form";
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
            return "redirect:/usuarios";
        }
    }

    @PostMapping("/usuarios")
    public String guardar(@Valid @ModelAttribute("form") UsuarioForm form, BindingResult result,
                          Model model, RedirectAttributes redirect) {
        if (!result.hasErrors()) {
            try {
                usuarioService.guardar(form);
                redirect.addFlashAttribute("mensaje", "Usuario guardado");
                return "redirect:/usuarios";
            } catch (IllegalArgumentException ex) {
                result.reject("negocio", ex.getMessage());
            }
        }
        model.addAttribute("nuevo", form.getId() == null);
        model.addAttribute("roles", Rol.values());
        return "usuarios/form";
    }
}
