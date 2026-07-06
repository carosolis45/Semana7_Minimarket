package com.minimarket.controller;

import com.minimarket.entity.Venta;
import com.minimarket.service.VentaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API REST de ventas. La generacion de ventas esta restringida al rol CAJERO
 * (y ADMIN) mediante {@code @PreAuthorize}; la consulta esta disponible para el
 * personal interno.
 */
@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CAJERO')")
    public List<Venta> listarVentas() {
        return ventaService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CAJERO')")
    public ResponseEntity<Venta> obtenerVentaPorId(@PathVariable Long id) {
        Venta venta = ventaService.findById(id);
        return (venta != null) ? ResponseEntity.ok(venta) : ResponseEntity.notFound().build();
    }

    // Solo los cajeros (y administradores) pueden generar ventas.
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CAJERO')")
    public Venta guardarVenta(@Valid @RequestBody Venta venta) {
        return ventaService.save(venta);
    }
}
