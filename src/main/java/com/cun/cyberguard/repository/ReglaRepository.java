package com.cun.cyberguard.repository;

import com.cun.cyberguard.domain.Regla;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReglaRepository extends JpaRepository<Regla, Long> {

    List<Regla> findByActivaTrue();

    List<Regla> findAllByOrderByNombreAsc();

    Optional<Regla> findByCodigo(String codigo);
}
