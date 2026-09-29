package com.cun.cyberguard.repository;

import com.cun.cyberguard.domain.Evento;
import com.cun.cyberguard.domain.enums.TipoEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findTop8ByOrderByFechaHoraDesc();

    @Query("""
            select e from Evento e
            where e.direccionIp = :ip and e.fechaHora >= :desde
            order by e.fechaHora asc
            """)
    List<Evento> historial(@Param("ip") String ip, @Param("desde") LocalDateTime desde);

    @Query("""
            select e from Evento e
            where (:ip is null or :ip = '' or lower(e.direccionIp) like lower(concat('%', :ip, '%')))
            and (:tipo is null or e.tipo = :tipo)
            and (:desde is null or e.fechaHora >= :desde)
            and (:hasta is null or e.fechaHora < :hasta)
            order by e.fechaHora desc
            """)
    List<Evento> buscar(
            @Param("ip") String ip,
            @Param("tipo") TipoEvento tipo,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta);
}
