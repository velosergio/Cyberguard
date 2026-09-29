package com.cun.cyberguard.repository;

import com.cun.cyberguard.domain.Usuario;
import com.cun.cyberguard.domain.enums.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsuario(String usuario);

    boolean existsByUsuario(String usuario);

    boolean existsByCorreo(String correo);

    boolean existsByUsuarioAndIdNot(String usuario, Long id);

    boolean existsByCorreoAndIdNot(String correo, Long id);

    long countByRolAndActivo(Rol rol, boolean activo);
}
