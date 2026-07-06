package com.minimarket.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.security.model.LoginRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de autenticacion del endpoint publico de login (POST /api/auth/login).
 * A diferencia de las pruebas de autorizacion, aqui NO se usa @MockBean ni
 * @WithMockUser: se ejerce la autenticacion real via AuthenticationManager + BCrypt
 * contra los usuarios sembrados por DataLoader (admin/admin123, cliente/cliente123).
 * Se verifica el flujo feliz (200 + cuerpo) y los errores 401 (credenciales
 * invalidas) y 400 (validacion @NotBlank del body).
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Login admin/admin123 retorna 200, autenticado=true y rol ROLE_ADMIN")
    void loginAdmin_credencialesValidas_ok() throws Exception {
        LoginRequest request = new LoginRequest("admin", "admin123");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.autenticado").value(true))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.roles", hasItem("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("Login cliente/cliente123 retorna 200, autenticado=true y rol ROLE_CLIENTE")
    void loginCliente_credencialesValidas_ok() throws Exception {
        LoginRequest request = new LoginRequest("cliente", "cliente123");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.autenticado").value(true))
                .andExpect(jsonPath("$.username").value("cliente"))
                .andExpect(jsonPath("$.roles", hasItem("ROLE_CLIENTE")));
    }

    @Test
    @DisplayName("Login admin con contrasena incorrecta retorna 401")
    void loginAdmin_passwordIncorrecta_noAutorizado() throws Exception {
        LoginRequest request = new LoginRequest("admin", "claveMala");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Login de usuario inexistente retorna 401")
    void loginUsuarioInexistente_noAutorizado() throws Exception {
        LoginRequest request = new LoginRequest("fantasma", "loquesea123");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Login con body vacio retorna 400 por validacion @NotBlank")
    void loginBodyVacio_solicitudInvalida() throws Exception {
        LoginRequest request = new LoginRequest();
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
