package com.minimarket.exception;

/**
 * Excepcion de dominio que indica que un producto no tiene stock suficiente
 * para completar la operacion solicitada (agregar al carrito, vender o
 * descontar inventario). Mas trazable que una IllegalArgumentException generica.
 */
public class StockInsuficienteException extends RuntimeException {

    public StockInsuficienteException(String message) {
        super(message);
    }

    public StockInsuficienteException(Long productoId, int solicitado, int disponible) {
        super("Stock insuficiente para el producto " + productoId
                + ": solicitado=" + solicitado + ", disponible=" + disponible);
    }
}
