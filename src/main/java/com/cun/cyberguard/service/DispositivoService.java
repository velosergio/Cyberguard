package com.cun.cyberguard.service;

import com.cun.cyberguard.domain.Dispositivo;
import com.cun.cyberguard.repository.DispositivoRepository;
import com.cun.cyberguard.web.form.DispositivoForm;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DispositivoService {

    private final DispositivoRepository dispositivoRepository;

    public DispositivoService(DispositivoRepository dispositivoRepository) {
        this.dispositivoRepository = dispositivoRepository;
    }

    @Transactional(readOnly = true)
    public List<Dispositivo> listar() {
        return dispositivoRepository.findAllByOrderByNombreAsc();
    }

    @Transactional(readOnly = true)
    public Dispositivo obtener(@NonNull Long id) {
        return dispositivoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El dispositivo no existe"));
    }

    @Transactional
    public void guardar(DispositivoForm form) {
        Long id = form.getId();
        String ip = form.getDireccionIp().trim();
        boolean ipRepetida = id == null
                ? dispositivoRepository.existsByDireccionIp(ip)
                : dispositivoRepository.existsByDireccionIpAndIdNot(ip, id);
        if (ipRepetida) {
            throw new IllegalArgumentException("Ya existe un dispositivo con esa dirección IP");
        }

        Dispositivo dispositivo;
        if (id == null) {
            dispositivo = new Dispositivo();
            dispositivo.setFechaRegistro(LocalDateTime.now());
        } else {
            dispositivo = obtener(id);
        }
        dispositivo.setNombre(form.getNombre().trim());
        dispositivo.setDireccionIp(ip);
        dispositivo.setDireccionMac(normalizarMac(form.getDireccionMac()));
        dispositivo.setTipo(form.getTipo());
        dispositivo.setEstado(form.getEstado());
        dispositivoRepository.save(dispositivo);
    }

    public DispositivoForm aFormulario(Dispositivo dispositivo) {
        DispositivoForm form = new DispositivoForm();
        form.setId(dispositivo.getId());
        form.setNombre(dispositivo.getNombre());
        form.setDireccionIp(dispositivo.getDireccionIp());
        form.setDireccionMac(dispositivo.getDireccionMac());
        form.setTipo(dispositivo.getTipo());
        form.setEstado(dispositivo.getEstado());
        return form;
    }

    private String normalizarMac(String mac) {
        if (mac == null || mac.isBlank()) {
            return null;
        }
        return mac.trim().toUpperCase();
    }
}
