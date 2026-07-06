package com.minimarket.common;

/**
 * Constantes del sistema centralizadas para evitar literales dispersos
 * (roles, tipos de movimiento y mensajes de error), mejorando la
 * mantenibilidad y evitando roturas por cambios menores.
 */
public final class Constantes {

    private Constantes() {
    }

    /** Nombres de rol (con prefijo ROLE_ requerido por Spring Security para hasRole). */
    public static final class Roles {
        public static final String ADMIN = "ROLE_ADMIN";
        public static final String CAJERO = "ROLE_CAJERO";
        public static final String CLIENTE = "ROLE_CLIENTE";
        public static final String BODEGUERO = "ROLE_BODEGUERO";

        private Roles() {
        }
    }

    /** Tipos de movimiento de inventario. */
    public static final class Movimientos {
        public static final String ENTRADA = "Entrada";
        public static final String SALIDA = "Salida";

        private Movimientos() {
        }
    }

    /** Mensajes de error reutilizables. */
    public static final class Mensajes {
        public static final String STOCK_INSUFICIENTE = "Stock insuficiente para el producto";
        public static final String PRODUCTO_NO_EXISTE = "El producto no existe";
        public static final String CANTIDAD_INVALIDA = "La cantidad debe ser mayor que cero";
        public static final String MOVIMIENTO_INVALIDO =
                "Datos de movimiento invalidos: tipoMovimiento y cantidad son obligatorios";
        public static final String MOVIMIENTO_SIN_PRODUCTO =
                "El movimiento debe estar asociado a un producto";
        public static final String CARRITO_SIN_PRODUCTO =
                "El carrito debe referenciar un producto valido";

        private Mensajes() {
        }
    }
}
