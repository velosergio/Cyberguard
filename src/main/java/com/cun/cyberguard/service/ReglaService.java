package com.cun.cyberguard.service;

import com.cun.cyberguard.domain.Regla;
import com.cun.cyberguard.repository.ReglaRepository;
import com.cun.cyberguard.web.form.ReglaForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReglaService {

    private final ReglaRepository reglaRepository;

    public ReglaService(ReglaRepository reglaRepository) {
        this.reglaRepository = reglaRepository;
    }

    @Transactional(readOnly = true)
    public List<Regla> listar() {
        return reglaRepository.findAllByOrderByNombreAsc();
    }

    @Transactional(readOnly = true)
    public Regla obtener(Long id) {
        return reglaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La regla no existe"));
    }

    @Transactional
    public void actualizar(Long id, ReglaForm form) {
        Regla regla = obtener(id);
        regla.setPuntuacion(form.getPuntuacion());
        regla.setUmbral(form.getUmbral());
        regla.setVentanaMinutos(form.getVentanaMinutos());
        regla.setActiva(form.isActiva());
        reglaRepository.save(regla);
    }

    public ReglaForm aFormulario(Regla regla) {
        ReglaForm form = new ReglaForm();
        form.setId(regla.getId());
        form.setPuntuacion(regla.getPuntuacion());
        form.setUmbral(regla.getUmbral());
        form.setVentanaMinutos(regla.getVentanaMinutos());
        form.setActiva(regla.isActiva());
        return form;
    }
}
