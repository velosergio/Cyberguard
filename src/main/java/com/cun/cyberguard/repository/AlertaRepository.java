package com.cun.cyberguard.repository;

import com.cun.cyberguard.domain.Alerta;
import com.cun.cyberguard.domain.enums.EstadoAlerta;
import com.cun.cyberguard.domain.enums.NivelRiesgo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    long countByNivel(NivelRiesgo nivel);

    List<Alerta> findTop5ByOrderByFechaHoraDesc();

    @Query("""
            select distinct a from Alerta a
            left join fetch a.reglas
            left join fetch a.evento
            left join fetch a.dispositivo
            where a.id = :id
            """)
    Optional<Alerta> findConDetalle(@Param("id") Long id);

    @Query("""
            select distinct a from Alerta a
            left join fetch a.reglas
            left join fetch a.evento
            left join fetch a.dispositivo
            where (:nivel is null or a.nivel = :nivel)
            and (:estado is null or a.estado = :estado)
            and (:desde is null or a.fechaHora >= :desde)
            and (:hasta is null or a.fechaHora < :hasta)
            order by a.fechaHora desc
            """)
    List<Alerta> buscar(
            @Param("nivel") NivelRiesgo nivel,
            @Param("estado") EstadoAlerta estado,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta);
}
