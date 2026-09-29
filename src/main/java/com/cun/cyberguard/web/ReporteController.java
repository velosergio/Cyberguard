package com.cun.cyberguard.web;

import com.cun.cyberguard.domain.enums.NivelRiesgo;
import com.cun.cyberguard.domain.enums.TipoEvento;
import com.cun.cyberguard.service.ReporteService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@Controller
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/reportes")
    public String reportes(
            @RequestParam(required = false) String ip,
            @RequestParam(required = false) TipoEvento tipo,
            @RequestParam(required = false) NivelRiesgo nivel,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            Model model) {
        model.addAttribute("eventos", reporteService.eventos(ip, tipo, desde, hasta));
        model.addAttribute("alertas", reporteService.alertas(nivel, null, desde, hasta));
        model.addAttribute("tipos", TipoEvento.values());
        model.addAttribute("niveles", NivelRiesgo.values());
        model.addAttribute("ip", ip);
        model.addAttribute("tipo", tipo);
        model.addAttribute("nivel", nivel);
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        return "reportes/index";
    }

    @GetMapping("/reportes/eventos.csv")
    public ResponseEntity<byte[]> eventosCsv(
            @RequestParam(required = false) String ip,
            @RequestParam(required = false) TipoEvento tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        byte[] cuerpo = reporteService.eventosCsv(reporteService.eventos(ip, tipo, desde, hasta))
                .getBytes(StandardCharsets.UTF_8);
        return csv(cuerpo, "eventos.csv");
    }

    @GetMapping("/reportes/alertas.csv")
    public ResponseEntity<byte[]> alertasCsv(
            @RequestParam(required = false) NivelRiesgo nivel,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        byte[] cuerpo = reporteService.alertasCsv(reporteService.alertas(nivel, null, desde, hasta))
                .getBytes(StandardCharsets.UTF_8);
        return csv(cuerpo, "alertas.csv");
    }

    private ResponseEntity<byte[]> csv(byte[] cuerpo, String nombre) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(nombre).build().toString())
                .contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
                .body(cuerpo);
    }
}
