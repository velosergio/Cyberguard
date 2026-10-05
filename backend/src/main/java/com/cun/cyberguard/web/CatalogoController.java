package com.cun.cyberguard.web;

import com.cun.cyberguard.domain.enums.EstadoAlerta;
import com.cun.cyberguard.domain.enums.EstadoDispositivo;
import com.cun.cyberguard.domain.enums.Importancia;
import com.cun.cyberguard.domain.enums.NivelRiesgo;
import com.cun.cyberguard.domain.enums.Rol;
import com.cun.cyberguard.domain.enums.TipoDispositivo;
import com.cun.cyberguard.domain.enums.TipoEvento;
import com.cun.cyberguard.web.dto.OpcionDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Listas de valores con su etiqueta, para que el frontend no duplique los enums. */
@RestController
@RequestMapping("/api/catalogos")
public class CatalogoController {

    @GetMapping
    public Map<String, List<OpcionDto>> catalogos() {
        Map<String, List<OpcionDto>> catalogos = new LinkedHashMap<>();
        catalogos.put("tiposDispositivo", OpcionDto.de(TipoDispositivo.values(), TipoDispositivo::getEtiqueta));
        catalogos.put("estadosDispositivo", OpcionDto.de(EstadoDispositivo.values(), EstadoDispositivo::getEtiqueta));
        catalogos.put("tiposEvento", OpcionDto.de(TipoEvento.values(), TipoEvento::getEtiqueta));
        catalogos.put("importancias", OpcionDto.de(Importancia.values(), Importancia::getEtiqueta));
        catalogos.put("nivelesRiesgo", OpcionDto.de(NivelRiesgo.values(), NivelRiesgo::getEtiqueta));
        catalogos.put("estadosAlerta", OpcionDto.de(EstadoAlerta.values(), EstadoAlerta::getEtiqueta));
        catalogos.put("roles", OpcionDto.de(Rol.values(), Rol::getEtiqueta));
        return catalogos;
    }
}
