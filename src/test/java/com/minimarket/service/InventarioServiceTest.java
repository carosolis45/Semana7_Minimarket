package com.minimarket.service;

import com.minimarket.entity.Inventario;
import com.minimarket.entity.Producto;
import com.minimarket.exception.DatosInvalidosException;
import com.minimarket.repository.InventarioRepository;
import com.minimarket.service.impl.InventarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del servicio de inventario. Se simula InventarioRepository
 * con Mockito para validar la informacion del movimiento (tipoMovimiento y
 * cantidad) y la relacion Producto-Inventario de forma aislada.
 */
@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @InjectMocks
    private InventarioServiceImpl inventarioService;

    private Producto producto;
    private Inventario inventario;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(10L);
        producto.setNombre("Arroz 1Kg");

        inventario = new Inventario();
        inventario.setId(1L);
        inventario.setProducto(producto);
        inventario.setCantidad(15);
        inventario.setTipoMovimiento("Entrada");
        inventario.setFechaMovimiento(new Date());
    }

    @Test
    @DisplayName("datosMovimientoValidos retorna true cuando tipoMovimiento y cantidad son validos")
    void datosMovimientoValidos_movimientoCompleto_retornaTrue() {
        assertTrue(inventarioService.datosMovimientoValidos(inventario));
    }

    @Test
    @DisplayName("datosMovimientoValidos retorna false cuando el tipoMovimiento esta vacio")
    void datosMovimientoValidos_tipoVacio_retornaFalse() {
        inventario.setTipoMovimiento("   ");
        assertFalse(inventarioService.datosMovimientoValidos(inventario));
    }

    @Test
    @DisplayName("datosMovimientoValidos retorna false cuando la cantidad es nula")
    void datosMovimientoValidos_cantidadNula_retornaFalse() {
        inventario.setCantidad(null);
        assertFalse(inventarioService.datosMovimientoValidos(inventario));
    }

    @Test
    @DisplayName("datosMovimientoValidos retorna false cuando la cantidad es cero o negativa")
    void datosMovimientoValidos_cantidadInvalida_retornaFalse() {
        inventario.setCantidad(0);
        assertFalse(inventarioService.datosMovimientoValidos(inventario));
    }

    @Test
    @DisplayName("productoEsCorrecto valida la relacion Producto-Inventario")
    void productoEsCorrecto_relacionValida() {
        assertTrue(inventarioService.productoEsCorrecto(inventario, 10L));   // producto correcto
        assertFalse(inventarioService.productoEsCorrecto(inventario, 99L));  // otro producto
    }

    @Test
    @DisplayName("registrarMovimiento guarda cuando los datos son validos")
    void registrarMovimiento_datosValidos_guarda() {
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);

        Inventario guardado = inventarioService.registrarMovimiento(inventario);

        assertNotNull(guardado);
        assertEquals("Entrada", guardado.getTipoMovimiento());
        verify(inventarioRepository, times(1)).save(inventario);
    }

    @Test
    @DisplayName("registrarMovimiento lanza excepcion y NO guarda con datos invalidos")
    void registrarMovimiento_datosInvalidos_lanzaExcepcion() {
        inventario.setTipoMovimiento(null);

        assertThrows(DatosInvalidosException.class,
                () -> inventarioService.registrarMovimiento(inventario));
        verify(inventarioRepository, never()).save(any(Inventario.class));
    }

    @Test
    @DisplayName("registrarMovimiento lanza excepcion si no hay producto asociado")
    void registrarMovimiento_sinProducto_lanzaExcepcion() {
        inventario.setProducto(null);

        assertThrows(DatosInvalidosException.class,
                () -> inventarioService.registrarMovimiento(inventario));
        verify(inventarioRepository, never()).save(any(Inventario.class));
    }

    @Test
    @DisplayName("findByProductoId retorna los movimientos del producto (relacion Producto-Inventario)")
    void findByProductoId_retornaMovimientos() {
        when(inventarioRepository.findByProductoId(10L)).thenReturn(List.of(inventario));

        List<Inventario> movimientos = inventarioService.findByProductoId(10L);

        assertEquals(1, movimientos.size());
        assertEquals(producto, movimientos.get(0).getProducto());
        verify(inventarioRepository).findByProductoId(10L);
    }

    @Test
    @DisplayName("findById, findAll, save y deleteById delegan en el repositorio")
    void crud_delega() {
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventario));
        when(inventarioRepository.findAll()).thenReturn(List.of(inventario));
        when(inventarioRepository.save(inventario)).thenReturn(inventario);

        assertEquals(1L, inventarioService.findById(1L).getId());
        assertEquals(1, inventarioService.findAll().size());
        assertEquals(inventario, inventarioService.save(inventario));

        inventarioService.deleteById(1L);
        verify(inventarioRepository, times(1)).deleteById(1L);
    }
}
