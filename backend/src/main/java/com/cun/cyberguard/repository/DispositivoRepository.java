package com.cun.cyberguard.repository;

import com.cun.cyberguard.domain.Dispositivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DispositivoRepository extends JpaRepository<Dispositivo, Long> {

    Optional<Dispositivo> findByDireccionIp(String direccionIp);

    boolean existsByDireccionIp(String direccionIp);

    boolean existsByDireccionIpAndIdDispositivosNot(String direccionIp, Long idDispositivos);

    List<Dispositivo> findAllByOrderByNombreAsc();
}
