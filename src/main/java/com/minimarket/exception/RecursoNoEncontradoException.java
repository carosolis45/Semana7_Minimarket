package com.minimarket.exception;

/**
 * Excepcion de dominio que indica que un recurso solicitado (producto, usuario,
 * etc.) no existe. El manejador global la traduce a una respuesta HTTP 404.
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String message) {
        super(message);
    }

    public RecursoNoEncontradoException(String recurso, Long id) {
        super(recurso + " no encontrado con id " + id);
    }
}
