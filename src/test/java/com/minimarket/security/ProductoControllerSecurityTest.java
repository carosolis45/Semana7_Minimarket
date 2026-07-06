package com.minimarket.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.entity.Categoria;
import com.minimarket.entity.Producto;
import com.minimarket.service.ProductoService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de autorizacion del endpoint de productos. Verifican que la creacion
 * de productos este restringida al rol ADMIN: 201/200 con ADMIN, 403 con un rol
 * sin permiso y 401 sin autenticar. Se usa MockMvc + @WithMockUser y el servicio
 * se simula con @MockBean para aislar la prueba de la capa de persistencia.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ProductoControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductoService productoService;

    private Producto producto;

    @BeforeEach
    void setUp() {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Bebidas");

        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Agua Mineral 1L");
        producto.setPrecio(990.0);
        producto.setStock(50);
        producto.setCategoria(categoria);

        when(productoService.save(any(Producto.class))).thenReturn(producto);
    }

    @Test
    @DisplayName("ADMIN puede crear un producto (200)")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void crearProducto_comoAdmin_permitido() throws Exception {
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("CLIENTE NO puede crear un producto (403)")
    @WithMockUser(username = "cliente", roles = {"CLIENTE"})
    void crearProducto_comoCliente_prohibido() throws Exception {
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("CAJERO NO puede crear un producto (403)")
    @WithMockUser(username = "cajero", roles = {"CAJERO"})
    void crearProducto_comoCajero_prohibido() throws Exception {
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Sin autenticar NO se puede crear un producto (401)")
    void crearProducto_sinAutenticar_noAutorizado() throws Exception {
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Un usuario autenticado (CLIENTE) puede consultar productos (200)")
    @WithMockUser(username = "cliente", roles = {"CLIENTE"})
    void listarProductos_autenticado_permitido() throws Exception {
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("ADMIN puede actualizar un producto (200)")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void actualizarProducto_comoAdmin_permitido() throws Exception {
        when(productoService.findById(1L)).thenReturn(producto);
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("CLIENTE NO puede eliminar un producto (403)")
    @WithMockUser(username = "cliente", roles = {"CLIENTE"})
    void eliminarProducto_comoCliente_prohibido() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .delete("/api/productos/1"))
                .andExpect(status().isForbidden());
    }
}
