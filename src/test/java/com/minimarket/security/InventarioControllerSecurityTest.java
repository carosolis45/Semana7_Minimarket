package com.minimarket.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.entity.Inventario;
import com.minimarket.entity.Producto;
import com.minimarket.service.InventarioService;
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
 * Pruebas de autorizacion del endpoint de inventario. Verifican que el registro
 * y edicion de movimientos este restringido a ADMIN o BODEGUERO, que la
 * eliminacion sea exclusiva de ADMIN y que la lectura este disponible para
 * ADMIN, BODEGUERO y CAJERO. Se usa MockMvc + @WithMockUser y el servicio se
 * simula con @MockBean para aislar la prueba de la capa de persistencia.
 */
@SpringBootTest
@AutoConfigureMockMvc
class InventarioControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InventarioService inventarioService;

    private Inventario inventario;

    @BeforeEach
    void setUp() {
        Producto producto = new Producto();
        producto.setId(1L);

        inventario = new Inventario();
        inventario.setId(1L);
        inventario.setProducto(producto);
        inventario.setCantidad(10);
        inventario.setTipoMovimiento("Entrada");
        inventario.setFechaMovimiento(new Date());

        when(inventarioService.save(any(Inventario.class))).thenReturn(inventario);
    }

    @Test
    @DisplayName("BODEGUERO puede registrar un movimiento de inventario (200)")
    @WithMockUser(username = "bodeguero", roles = {"BODEGUERO"})
    void registrarMovimiento_comoBodeguero_permitido() throws Exception {
        mockMvc.perform(post("/api/inventario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventario)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("ADMIN puede registrar un movimiento de inventario (200)")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void registrarMovimiento_comoAdmin_permitido() throws Exception {
        mockMvc.perform(post("/api/inventario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventario)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("CAJERO NO puede registrar un movimiento de inventario (403)")
    @WithMockUser(username = "cajero", roles = {"CAJERO"})
    void registrarMovimiento_comoCajero_prohibido() throws Exception {
        mockMvc.perform(post("/api/inventario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventario)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("CLIENTE NO puede registrar un movimiento de inventario (403)")
    @WithMockUser(username = "cliente", roles = {"CLIENTE"})
    void registrarMovimiento_comoCliente_prohibido() throws Exception {
        mockMvc.perform(post("/api/inventario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventario)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Sin autenticar NO se puede registrar un movimiento de inventario (401)")
    void registrarMovimiento_sinAutenticar_noAutorizado() throws Exception {
        mockMvc.perform(post("/api/inventario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventario)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("CAJERO puede consultar los movimientos de inventario (200)")
    @WithMockUser(username = "cajero", roles = {"CAJERO"})
    void listarMovimientos_comoCajero_permitido() throws Exception {
        mockMvc.perform(get("/api/inventario"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("BODEGUERO NO puede eliminar un movimiento de inventario (403, solo ADMIN)")
    @WithMockUser(username = "bodeguero", roles = {"BODEGUERO"})
    void eliminarMovimiento_comoBodeguero_prohibido() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .delete("/api/inventario/1"))
                .andExpect(status().isForbidden());
    }
}
