package com.cun.cyberguard.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SeguridadAccesoTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "consulta", roles = "CONSULTA")
    void consultaNoPuedeCrearUsuarios() throws Exception {
        mockMvc.perform(post("/usuarios")
                        .with(csrf())
                        .param("nombre", "Nuevo")
                        .param("usuario", "nuevo")
                        .param("correo", "nuevo@cyberguard.local")
                        .param("contrasena", "Clave1234")
                        .param("rol", "CONSULTA")
                        .param("activo", "true"))
                .andExpect(status().isForbidden());
    }

    @Test
    void anonimoEsRedirigidoAlLogin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }
}
