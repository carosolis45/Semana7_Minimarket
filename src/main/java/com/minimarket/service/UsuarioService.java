package com.minimarket.service;

import com.minimarket.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    List<Usuario> findAll();
    Optional<Usuario> findById(Long id);
    Optional<Usuario> findByUsername(String username);
    Usuario save(Usuario usuario);
    void deleteById(Long id);

    /**
     * Registra un usuario cifrando su contrasena con BCrypt antes de persistir,
     * para que nunca se almacene en texto plano.
     */
    Usuario registrar(Usuario usuario);
}