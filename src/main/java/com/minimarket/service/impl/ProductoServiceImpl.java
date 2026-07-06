package com.minimarket.service.impl;

import com.minimarket.common.Constantes;
import com.minimarket.entity.Producto;
import com.minimarket.exception.RecursoNoEncontradoException;
import com.minimarket.exception.StockInsuficienteException;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.service.ProductoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements ProductoService {

    private static final Logger log = LoggerFactory.getLogger(ProductoServiceImpl.class);

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    @Override
    public Producto findById(Long id) {
        return productoRepository.findById(id).orElse(null);
    }

    @Override
    public Producto save(Producto producto) {
        return productoRepository.save(producto);
    }

    @Override
    public void deleteById(Long id) {
        productoRepository.deleteById(id);
    }

    @Override
    public List<Producto> findByCategoriaId(Long categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId);
    }

    @Override
    public boolean hayStock(Long productoId, int cantidad) {
        Optional<Producto> producto = productoRepository.findById(productoId);
        return producto.isPresent() && producto.get().getStock() >= cantidad;
    }

    @Override
    public Producto descontarStock(Long productoId, int cantidad) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RecursoNoEncontradoException(Constantes.Mensajes.PRODUCTO_NO_EXISTE));
        if (producto.getStock() < cantidad) {
            log.warn("Intento de descontar {} unidades del producto {} con stock {}",
                    cantidad, productoId, producto.getStock());
            throw new StockInsuficienteException(productoId, cantidad, producto.getStock());
        }
        producto.setStock(producto.getStock() - cantidad);
        log.info("Stock del producto {} descontado en {} unidades", productoId, cantidad);
        return productoRepository.save(producto);
    }
}
