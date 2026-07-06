package com.minimarket.service;

import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.exception.DatosInvalidosException;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del servicio de usuarios. Se simulan UsuarioRepository y
 * PasswordEncoder con Mockito para validar la logica de registro (cifrado de
 * contrasena) y la delegacion de las operaciones CRUD de forma aislada.
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("jperez");
        usuario.setPassword("secreto123");
        usuario.setNombre("Juan");
        usuario.setRoles(Set.of(new Rol("ROLE_CLIENTE")));
    }

    @Test
    @DisplayName("registrar cifra la contrasena con BCrypt antes de guardar")
    void registrar_cifraPasswordYGuarda() {
        when(passwordEncoder.encode("secreto123")).thenReturn("$2a$10$hashSimulado");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario guardado = usuarioService.registrar(usuario);

        assertEquals("$2a$10$hashSimulado", guardado.getPassword());
        assertNotEquals("secreto123", guardado.getPassword());
        verify(passwordEncoder).encode("secreto123");
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("registrar lanza excepcion y NO guarda si faltan datos obligatorios")
    void registrar_datosInvalidos_lanzaExcepcion() {
        usuario.setUsername("   "); // username en blanco

        assertThrows(DatosInvalidosException.class, () -> usuarioService.registrar(usuario));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("findByUsername delega en el repositorio")
    void findByUsername_delega() {
        when(usuarioRepository.findByUsername("jperez")).thenReturn(Optional.of(usuario));
        assertTrue(usuarioService.findByUsername("jperez").isPresent());
        verify(usuarioRepository).findByUsername("jperez");
    }

    @Test
    @DisplayName("findById y findAll delegan en el repositorio")
    void findByIdYFindAll_delegan() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        assertEquals("jperez", usuarioService.findById(1L).get().getUsername());
        assertEquals(1, usuarioService.findAll().size());
    }

    @Test
    @DisplayName("save delega la persistencia en el repositorio")
    void save_delega() {
        when(usuarioRepository.save(usuario)).thenReturn(usuario);
        assertEquals(usuario, usuarioService.save(usuario));
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("deleteById invoca la eliminacion en el repositorio")
    void deleteById_delega() {
        usuarioService.deleteById(1L);
        verify(usuarioRepository, times(1)).deleteById(1L);
    }
}
