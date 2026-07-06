package com.minimarket.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.entity.Usuario;
import com.minimarket.entity.Venta;
import com.minimarket.service.VentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de autorizacion del endpoint de ventas. Verifican que la generacion y
 * consulta de ventas esten restringidas al personal interno (ADMIN o CAJERO):
 * 200 con ADMIN/CAJERO, 403 con un rol sin permiso y 401 sin autenticar. Se usa
 * MockMvc + @WithMockUser y el servicio se simula con @MockBean para aislar la
 * prueba de la capa de persistencia.
 */
@SpringBootTest
@AutoConfigureMockMvc
class VentaControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VentaService ventaService;

    private Venta venta;

    @BeforeEach
    void setUp() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        venta = new Venta();
        venta.setId(1L);
        venta.setUsuario(usuario);
        venta.setFecha(new Date());

        when(ventaService.save(any(Venta.class))).thenReturn(venta);
    }

    @Test
    @DisplayName("CAJERO puede generar una venta (200)")
    @WithMockUser(username = "cajero", roles = {"CAJERO"})
    void generarVenta_comoCajero_permitido() throws Exception {
        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(venta)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("ADMIN puede generar una venta (200)")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void generarVenta_comoAdmin_permitido() throws Exception {
        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(venta)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("CLIENTE NO puede generar una venta (403)")
    @WithMockUser(username = "cliente", roles = {"CLIENTE"})
    void generarVenta_comoCliente_prohibido() throws Exception {
        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(venta)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("BODEGUERO NO puede generar una venta (403)")
    @WithMockUser(username = "bodeguero", roles = {"BODEGUERO"})
    void generarVenta_comoBodeguero_prohibido() throws Exception {
        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(venta)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Sin autenticar NO se puede generar una venta (401)")
    void generarVenta_sinAutenticar_noAutorizado() throws Exception {
        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(venta)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("CAJERO puede consultar las ventas (200)")
    @WithMockUser(username = "cajero", roles = {"CAJERO"})
    void listarVentas_comoCajero_permitido() throws Exception {
        mockMvc.perform(get("/api/ventas"))
                .andExpect(status().isOk());
    }
}
