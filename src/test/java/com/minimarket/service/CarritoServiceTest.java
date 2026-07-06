package com.minimarket.service;

import com.minimarket.entity.Carrito;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Usuario;
import com.minimarket.exception.DatosInvalidosException;
import com.minimarket.exception.RecursoNoEncontradoException;
import com.minimarket.exception.StockInsuficienteException;
import com.minimarket.repository.CarritoRepository;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.service.impl.CarritoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del servicio de carrito. Se simulan CarritoRepository y
 * ProductoRepository con Mockito para validar, de forma aislada, que solo se
 * agreguen productos con stock suficiente y que la relacion Carrito-Usuario sea
 * la correcta.
 */
@ExtendWith(MockitoExtension.class)
class CarritoServiceTest {

    @Mock
    private CarritoRepository carritoRepository;
    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private CarritoServiceImpl carritoService;

    private Usuario usuario;
    private Producto producto;
    private Carrito carrito;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("cliente1");

        producto = new Producto();
        producto.setId(10L);
        producto.setNombre("Leche 1L");
        producto.setPrecio(1200.0);
        producto.setStock(20);

        carrito = new Carrito();
        carrito.setId(100L);
        carrito.setUsuario(usuario);
        carrito.setProducto(producto);
        carrito.setCantidad(5);
    }

    @Test
    @DisplayName("agregarProducto guarda el carrito cuando hay stock suficiente")
    void agregarProducto_conStock_guarda() {
        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto)); // stock=20
        when(carritoRepository.save(any(Carrito.class))).thenReturn(carrito);

        Carrito resultado = carritoService.agregarProducto(carrito);

        assertNotNull(resultado);
        assertEquals(5, resultado.getCantidad());
        verify(carritoRepository, times(1)).save(carrito);
    }

    @Test
    @DisplayName("agregarProducto lanza excepcion y NO guarda si el stock es insuficiente")
    void agregarProducto_sinStock_lanzaExcepcion() {
        producto.setStock(2);            // hay 2
        carrito.setCantidad(10);         // pide 10
        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));

        assertThrows(StockInsuficienteException.class, () -> carritoService.agregarProducto(carrito));
        verify(carritoRepository, never()).save(any(Carrito.class));
    }

    @Test
    @DisplayName("agregarProducto lanza excepcion si el producto no existe")
    void agregarProducto_productoInexistente_lanzaExcepcion() {
        when(productoRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> carritoService.agregarProducto(carrito));
        verify(carritoRepository, never()).save(any(Carrito.class));
    }

    @Test
    @DisplayName("agregarProducto lanza excepcion si la cantidad es cero o negativa")
    void agregarProducto_cantidadInvalida_lanzaExcepcion() {
        carrito.setCantidad(0);
        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));

        assertThrows(DatosInvalidosException.class, () -> carritoService.agregarProducto(carrito));
    }

    @Test
    @DisplayName("usuarioEsCorrecto valida la relacion Carrito-Usuario")
    void usuarioEsCorrecto_relacionValida() {
        assertTrue(carritoService.usuarioEsCorrecto(carrito, 1L));   // dueño correcto
        assertFalse(carritoService.usuarioEsCorrecto(carrito, 99L)); // otro usuario
    }

    @Test
    @DisplayName("findByUsuarioId retorna los carritos del usuario (relacion Carrito-Usuario)")
    void findByUsuarioId_retornaCarritosDelUsuario() {
        when(carritoRepository.findByUsuarioId(1L)).thenReturn(List.of(carrito));

        List<Carrito> carritos = carritoService.findByUsuarioId(1L);

        assertEquals(1, carritos.size());
        assertEquals(usuario, carritos.get(0).getUsuario());
        verify(carritoRepository).findByUsuarioId(1L);
    }

    @Test
    @DisplayName("findById y findAll delegan en el repositorio")
    void findByIdYFindAll_delegan() {
        when(carritoRepository.findById(100L)).thenReturn(Optional.of(carrito));
        when(carritoRepository.findAll()).thenReturn(List.of(carrito));

        assertEquals(100L, carritoService.findById(100L).getId());
        assertEquals(1, carritoService.findAll().size());
    }

    @Test
    @DisplayName("save y deleteById delegan en el repositorio")
    void saveYDelete_delegan() {
        when(carritoRepository.save(carrito)).thenReturn(carrito);
        assertEquals(carrito, carritoService.save(carrito));

        carritoService.deleteById(100L);
        verify(carritoRepository, times(1)).deleteById(100L);
    }
}
