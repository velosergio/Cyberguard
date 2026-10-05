package com.cun.cyberguard.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class TokenService {

    public static final String CLAIM_ROLES = "roles";

    private final JwtEncoder encoder;
    private final Duration duracion;

    public TokenService(JwtEncoder encoder, @Value("${cyberguard.jwt.duracion-minutos}") long duracionMinutos) {
        this.encoder = encoder;
        this.duracion = Duration.ofMinutes(duracionMinutos);
    }

    public TokenEmitido emitir(Authentication autenticacion) {
        Instant ahora = Instant.now();
        Instant expira = ahora.plus(duracion);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("cyberguard")
                .subject(autenticacion.getName())
                .issuedAt(ahora)
                .expiresAt(expira)
                .claim(CLAIM_ROLES, autenticacion.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .build();
        JwsHeader cabecera = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = encoder.encode(JwtEncoderParameters.from(cabecera, claims)).getTokenValue();
        return new TokenEmitido(token, expira);
    }

    public record TokenEmitido(String token, Instant expiraEn) {
    }
}
