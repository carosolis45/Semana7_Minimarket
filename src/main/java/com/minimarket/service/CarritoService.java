package com.minimarket.service;

import com.minimarket.entity.Carrito;

import java.util.List;

public interface CarritoService {
    List<Carrito> findAll();
    Carrito findById(Long id);
    Carrito save(Carrito carrito);
    void deleteById(Long id);
    List<Carrito> findByUsuarioId(Long usuarioId);

    /**
     * Agrega un producto al carrito solo si hay stock suficiente del producto,
     * consultando el repositorio de productos (dependencia simulada en pruebas).
     * @throws IllegalArgumentException si el producto no existe o el stock es insuficiente.
     */
    Carrito agregarProducto(Carrito carrito);

    /**
     * Indica si el usuario indicado es el propietario del carrito (relacion
     * Carrito-Usuario correcta).
     */
    boolean usuarioEsCorrecto(Carrito carrito, Long usuarioId);
}
