package com.minimarket.service.impl;

import com.minimarket.common.Constantes;
import com.minimarket.entity.Carrito;
import com.minimarket.entity.Producto;
import com.minimarket.exception.DatosInvalidosException;
import com.minimarket.exception.RecursoNoEncontradoException;
import com.minimarket.exception.StockInsuficienteException;
import com.minimarket.repository.CarritoRepository;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.service.CarritoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarritoServiceImpl implements CarritoService {

    private static final Logger log = LoggerFactory.getLogger(CarritoServiceImpl.class);

    private final CarritoRepository carritoRepository;
    private final ProductoRepository productoRepository;

    // Inyeccion por constructor: facilita el uso de @InjectMocks en las pruebas.
    public CarritoServiceImpl(CarritoRepository carritoRepository,
                              ProductoRepository productoRepository) {
        this.carritoRepository = carritoRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    public List<Carrito> findAll() {
        return carritoRepository.findAll();
    }

    @Override
    public Carrito findById(Long id) {
        return carritoRepository.findById(id).orElse(null);
    }

    @Override
    public Carrito save(Carrito carrito) {
        return carritoRepository.save(carrito);
    }

    @Override
    public void deleteById(Long id) {
        carritoRepository.deleteById(id);
    }

    @Override
    public List<Carrito> findByUsuarioId(Long usuarioId) {
        return carritoRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public Carrito agregarProducto(Carrito carrito) {
        if (carrito.getProducto() == null || carrito.getProducto().getId() == null) {
            throw new DatosInvalidosException(Constantes.Mensajes.CARRITO_SIN_PRODUCTO);
        }
        // Consulta el stock real del producto (dependencia simulada en pruebas).
        Optional<Producto> producto = productoRepository.findById(carrito.getProducto().getId());
        if (producto.isEmpty()) {
            throw new RecursoNoEncontradoException(Constantes.Mensajes.PRODUCTO_NO_EXISTE);
        }
        Integer cantidad = carrito.getCantidad();
        if (cantidad == null || cantidad <= 0) {
            throw new DatosInvalidosException(Constantes.Mensajes.CANTIDAD_INVALIDA);
        }
        if (producto.get().getStock() < cantidad) {
            log.warn("Stock insuficiente al agregar producto {} al carrito (pide {}, hay {})",
                    producto.get().getId(), cantidad, producto.get().getStock());
            throw new StockInsuficienteException(producto.get().getId(), cantidad, producto.get().getStock());
        }
        log.info("Producto {} agregado al carrito (cantidad {})", producto.get().getId(), cantidad);
        return carritoRepository.save(carrito);
    }

    @Override
    public boolean usuarioEsCorrecto(Carrito carrito, Long usuarioId) {
        return carrito != null
                && carrito.getUsuario() != null
                && carrito.getUsuario().getId() != null
                && carrito.getUsuario().getId().equals(usuarioId);
    }
}
