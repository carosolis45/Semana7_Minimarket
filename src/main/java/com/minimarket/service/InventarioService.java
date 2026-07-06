package com.minimarket.service;

import com.minimarket.entity.Inventario;

import java.util.List;

public interface InventarioService {
    List<Inventario> findAll();
    Inventario findById(Long id);
    Inventario save(Inventario inventario);
    void deleteById(Long id);
    List<Inventario> findByProductoId(Long productoId);

    /**
     * Indica si los datos del movimiento son validos: tipoMovimiento no nulo ni
     * vacio (Entrada/Salida) y cantidad no nula y mayor que cero.
     */
    boolean datosMovimientoValidos(Inventario inventario);

    /** Indica si el producto asociado al inventario es el esperado (relacion correcta). */
    boolean productoEsCorrecto(Inventario inventario, Long productoId);

    /**
     * Registra un movimiento de inventario validando previamente sus datos y que
     * tenga un producto asociado.
     * @throws IllegalArgumentException si los datos del movimiento son invalidos.
     */
    Inventario registrarMovimiento(Inventario inventario);
}
