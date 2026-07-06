package com.minimarket.exception;

/**
 * Excepcion de dominio que indica que los datos de una operacion son invalidos
 * (cantidad no positiva, movimiento sin tipo, carrito sin producto, etc.).
 * El manejador global la traduce a una respuesta HTTP 400, en reemplazo de la
 * IllegalArgumentException generica para mejorar la trazabilidad.
 */
public class DatosInvalidosException extends RuntimeException {

    public DatosInvalidosException(String message) {
        super(message);
    }
}
