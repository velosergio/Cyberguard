package com.cun.cyberguard.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SuppressWarnings("null")
class SeguridadAccesoTest {

    private static final String NUEVO_USUARIO = """
            {"nombre":"Nuevo","usuario":"nuevo","correo":"nuevo@cyberguard.local",
             "contrasena":"Clave1234","rol":"CONSULTA","activo":true}""";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void anonimoRecibe401() throws Exception {
        mockMvc.perform(get("/api/panel")).andExpect(status().isUnauthorized());
    }

    @Test
    void loginDevuelveTokenQueDaAccesoAlPanel() throws Exception {
        String respuesta = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usuario\":\"admin\",\"contrasena\":\"Admin123*\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.usuario.rol").value("ADMINISTRADOR"))
                .andReturn().getResponse().getContentAsString();
        JsonNode json = objectMapper.readTree(respuesta);
        String token = json.get("token").asText();

        mockMvc.perform(get("/api/panel").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/usuarios").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void loginConClaveIncorrectaRecibe401() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usuario\":\"admin\",\"contrasena\":\"incorrecta\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Usuario o contraseña incorrectos"));
    }

    @Test
    void tokenManipuladoRecibe401() throws Exception {
        mockMvc.perform(get("/api/panel").header("Authorization", "Bearer abc.def.ghi"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void consultaNoPuedeCrearUsuarios() throws Exception {
        mockMvc.perform(post("/api/usuarios")
                        .with(jwt().jwt(token -> token.subject("consulta"))
                                .authorities(new SimpleGrantedAuthority("ROLE_CONSULTA")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(NUEVO_USUARIO))
                .andExpect(status().isForbidden());
    }

    @Test
    void consultaNoPuedeVerReglasNiReportes() throws Exception {
        var consulta = jwt().jwt(token -> token.subject("consulta"))
                .authorities(new SimpleGrantedAuthority("ROLE_CONSULTA"));
        mockMvc.perform(get("/api/reglas").with(consulta)).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/reportes").with(consulta)).andExpect(status().isForbidden());
    }

    @Test
    void analistaNoPuedeEditarReglas() throws Exception {
        mockMvc.perform(put("/api/reglas/1")
                        .with(jwt().jwt(token -> token.subject("analista"))
                                .authorities(new SimpleGrantedAuthority("ROLE_ANALISTA")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"puntuacion\":10,\"umbral\":1,\"ventanaMinutos\":5,\"activa\":true}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void validacionDevuelve400ConErroresPorCampo() throws Exception {
        mockMvc.perform(post("/api/dispositivos")
                        .with(jwt().jwt(token -> token.subject("analista"))
                                .authorities(new SimpleGrantedAuthority("ROLE_ANALISTA")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"\",\"direccionIp\":\"no-es-ip\",\"tipo\":\"SERVIDOR\",\"estado\":\"ACTIVO\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.nombre").exists())
                .andExpect(jsonPath("$.errores.direccionIp").exists());
    }

    @Test
    void analistaRegistraEventoYLasConsultasSerializanSinErrores() throws Exception {
        var analista = jwt().jwt(token -> token.subject("analista"))
                .authorities(new SimpleGrantedAuthority("ROLE_ANALISTA"));

        mockMvc.perform(post("/api/eventos")
                        .with(analista)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"direccionIp":"203.0.113.50","tipo":"ACTIVIDAD_INUSUAL",
                                 "fechaHora":"2026-10-05T10:30","importancia":"ALTA","detalle":"prueba"}"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.direccionIp").value("203.0.113.50"));

        String alertas = mockMvc.perform(get("/api/alertas").with(analista))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].reglas").isArray())
                .andExpect(jsonPath("$[0].evento.direccionIp").exists())
                .andReturn().getResponse().getContentAsString();
        long idAlerta = objectMapper.readTree(alertas).get(0).get("id").asLong();

        mockMvc.perform(get("/api/alertas/" + idAlerta).with(analista))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reglas[0].nombre").exists());
        mockMvc.perform(put("/api/alertas/" + idAlerta + "/estado")
                        .with(analista)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"EN_REVISION\",\"nota\":\"revisando\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_REVISION"))
                .andExpect(jsonPath("$.reglas[0].nombre").exists());
        mockMvc.perform(get("/api/panel").with(analista))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alertasRecientes[0].nivel").exists());
        mockMvc.perform(get("/api/reportes").with(analista))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alertas[0].reglas").isArray());
        mockMvc.perform(get("/api/reportes/alertas.csv").with(analista))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/catalogos").with(analista))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tiposEvento[0].valor").exists());
    }

    @Test
    void recursoInexistenteDevuelve404() throws Exception {
        mockMvc.perform(get("/api/alertas/999999")
                        .with(jwt().jwt(token -> token.subject("consulta"))
                                .authorities(new SimpleGrantedAuthority("ROLE_CONSULTA"))))
                .andExpect(status().isNotFound());
    }
}
