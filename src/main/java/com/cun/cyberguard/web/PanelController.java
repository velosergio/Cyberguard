package com.cun.cyberguard.web;

import com.cun.cyberguard.service.PanelService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PanelController {

    private final PanelService panelService;

    public PanelController(PanelService panelService) {
        this.panelService = panelService;
    }

    @GetMapping("/")
    public String panel(Model model) {
        model.addAttribute("resumen", panelService.resumir());
        return "panel/index";
    }
}
