package com.minimarket.service;

import com.minimarket.entity.Producto;

import java.util.List;

public interface ProductoService {
    List<Producto> findAll();
    Producto findById(Long id);
    Producto save(Producto producto);
    void deleteById(Long id);
    List<Producto> findByCategoriaId(Long categoriaId);

    /**
     * Indica si el producto con el id dado tiene stock suficiente para la
     * cantidad solicitada (consulta el repositorio).
     */
    boolean hayStock(Long productoId, int cantidad);

    /**
     * Descuenta stock del producto al confirmar una operacion de venta.
     * @throws IllegalArgumentException si el producto no existe o no hay stock.
     */
    Producto descontarStock(Long productoId, int cantidad);
}
