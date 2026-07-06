package com.minimarket.service;

import com.minimarket.entity.Producto;
import com.minimarket.exception.RecursoNoEncontradoException;
import com.minimarket.exception.StockInsuficienteException;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.service.impl.ProductoServiceImpl;
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
 * Pruebas unitarias del servicio de producto. Se simula ProductoRepository con
 * Mockito para validar la consulta y el descuento de stock de forma aislada.
 */
@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    private Producto producto;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(10L);
        producto.setNombre("Aceite 1L");
        producto.setPrecio(2500.0);
        producto.setStock(30);
    }

    @Test
    @DisplayName("hayStock retorna true cuando el stock alcanza la cantidad solicitada")
    void hayStock_suficiente_retornaTrue() {
        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto)); // stock=30
        assertTrue(productoService.hayStock(10L, 25));
        verify(productoRepository).findById(10L);
    }

    @Test
    @DisplayName("hayStock retorna false cuando la cantidad supera el stock")
    void hayStock_insuficiente_retornaFalse() {
        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));
        assertFalse(productoService.hayStock(10L, 40));
    }

    @Test
    @DisplayName("hayStock retorna false cuando el producto no existe")
    void hayStock_productoInexistente_retornaFalse() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());
        assertFalse(productoService.hayStock(99L, 1));
    }

    @Test
    @DisplayName("descontarStock reduce el stock y persiste el producto")
    void descontarStock_conStock_descuentaYGuarda() {
        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        Producto actualizado = productoService.descontarStock(10L, 10);

        assertEquals(20, actualizado.getStock()); // 30 - 10
        verify(productoRepository).save(producto);
    }

    @Test
    @DisplayName("descontarStock lanza excepcion si no hay stock suficiente")
    void descontarStock_sinStock_lanzaExcepcion() {
        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));

        assertThrows(StockInsuficienteException.class, () -> productoService.descontarStock(10L, 50));
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    @DisplayName("descontarStock lanza excepcion si el producto no existe")
    void descontarStock_productoInexistente_lanzaExcepcion() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> productoService.descontarStock(99L, 1));
    }

    @Test
    @DisplayName("findById, findAll, save y deleteById delegan en el repositorio")
    void crud_delega() {
        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));
        when(productoRepository.findAll()).thenReturn(List.of(producto));
        when(productoRepository.save(producto)).thenReturn(producto);

        assertEquals(10L, productoService.findById(10L).getId());
        assertEquals(1, productoService.findAll().size());
        assertEquals(producto, productoService.save(producto));

        productoService.deleteById(10L);
        verify(productoRepository, times(1)).deleteById(10L);
    }

    @Test
    @DisplayName("findByCategoriaId delega en el repositorio")
    void findByCategoriaId_delega() {
        when(productoRepository.findByCategoriaId(5L)).thenReturn(List.of(producto));
        assertEquals(1, productoService.findByCategoriaId(5L).size());
        verify(productoRepository).findByCategoriaId(5L);
    }
}
