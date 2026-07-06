package com.minimarket.service.impl;

import com.minimarket.common.Constantes;
import com.minimarket.entity.Inventario;
import com.minimarket.exception.DatosInvalidosException;
import com.minimarket.repository.InventarioRepository;
import com.minimarket.service.InventarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventarioServiceImpl implements InventarioService {

    private static final Logger log = LoggerFactory.getLogger(InventarioServiceImpl.class);

    @Autowired
    private InventarioRepository inventarioRepository;

    @Override
    public List<Inventario> findAll() {
        return inventarioRepository.findAll();
    }

    @Override
    public Inventario findById(Long id) {
        return inventarioRepository.findById(id).orElse(null);
    }

    @Override
    public Inventario save(Inventario inventario) {
        return inventarioRepository.save(inventario);
    }

    @Override
    public void deleteById(Long id) {
        inventarioRepository.deleteById(id);
    }

    @Override
    public List<Inventario> findByProductoId(Long productoId) {
        return inventarioRepository.findByProductoId(productoId);
    }

    @Override
    public boolean datosMovimientoValidos(Inventario inventario) {
        if (inventario == null) {
            return false;
        }
        boolean tipoValido = inventario.getTipoMovimiento() != null
                && !inventario.getTipoMovimiento().trim().isEmpty();
        boolean cantidadValida = inventario.getCantidad() != null
                && inventario.getCantidad() > 0;
        return tipoValido && cantidadValida;
    }

    @Override
    public boolean productoEsCorrecto(Inventario inventario, Long productoId) {
        return inventario != null
                && inventario.getProducto() != null
                && inventario.getProducto().getId() != null
                && inventario.getProducto().getId().equals(productoId);
    }

    @Override
    public Inventario registrarMovimiento(Inventario inventario) {
        if (!datosMovimientoValidos(inventario)) {
            throw new DatosInvalidosException(Constantes.Mensajes.MOVIMIENTO_INVALIDO);
        }
        if (inventario.getProducto() == null) {
            throw new DatosInvalidosException(Constantes.Mensajes.MOVIMIENTO_SIN_PRODUCTO);
        }
        log.info("Movimiento de inventario registrado: tipo={}, cantidad={}",
                inventario.getTipoMovimiento(), inventario.getCantidad());
        return inventarioRepository.save(inventario);
    }
}
