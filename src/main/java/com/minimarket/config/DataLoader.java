package com.minimarket.config;

import com.minimarket.common.Constantes;
import com.minimarket.entity.Categoria;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.CategoriaRepository;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.RolRepository;
import com.minimarket.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

/**
 * Carga datos iniciales de seguridad al arrancar la aplicacion: los roles del
 * negocio (ADMIN, CAJERO, CLIENTE, BODEGUERO) y un usuario de ejemplo por rol.
 *
 * <p>Las contrasenas se almacenan cifradas con BCrypt. Los roles llevan el
 * prefijo {@code ROLE_} requerido por Spring Security para que {@code hasRole(...)}
 * y {@code @PreAuthorize} funcionen.
 *
 * <p>Usuarios sembrados (usuario / contrasena):
 * <ul>
 *   <li>admin / admin123 -> ROLE_ADMIN</li>
 *   <li>cajero / cajero123 -> ROLE_CAJERO</li>
 *   <li>bodeguero / bodeguero123 -> ROLE_BODEGUERO</li>
 *   <li>cliente / cliente123 -> ROLE_CLIENTE</li>
 * </ul>
 */
@Configuration
public class DataLoader {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    @Bean
    public CommandLineRunner initData(RolRepository rolRepository,
                                      UsuarioRepository usuarioRepository,
                                      CategoriaRepository categoriaRepository,
                                      ProductoRepository productoRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            if (usuarioRepository.count() > 0) {
                return; // evita duplicar datos en reinicios con la misma BD
            }

            Rol admin = crearRol(rolRepository, Constantes.Roles.ADMIN);
            Rol cajero = crearRol(rolRepository, Constantes.Roles.CAJERO);
            Rol bodeguero = crearRol(rolRepository, Constantes.Roles.BODEGUERO);
            Rol cliente = crearRol(rolRepository, Constantes.Roles.CLIENTE);

            crearUsuario(usuarioRepository, passwordEncoder, "admin", "admin123",
                    "Ana", "Admin", "admin@minimarket.cl", Set.of(admin));
            crearUsuario(usuarioRepository, passwordEncoder, "cajero", "cajero123",
                    "Carlos", "Cajero", "cajero@minimarket.cl", Set.of(cajero));
            crearUsuario(usuarioRepository, passwordEncoder, "bodeguero", "bodeguero123",
                    "Bruno", "Bodega", "bodega@minimarket.cl", Set.of(bodeguero));
            crearUsuario(usuarioRepository, passwordEncoder, "cliente", "cliente123",
                    "Clara", "Cliente", "cliente@minimarket.cl", Set.of(cliente));

            // Catalogo base de ejemplo (para que las pruebas de endpoints tengan datos).
            Categoria bebidas = new Categoria();
            bebidas.setNombre("Bebidas");
            bebidas = categoriaRepository.save(bebidas);

            Producto agua = new Producto();
            agua.setNombre("Agua Mineral 1L");
            agua.setPrecio(990.0);
            agua.setStock(50);
            agua.setCategoria(bebidas);
            productoRepository.save(agua);

            log.info("DataLoader: sembrados 4 roles, 4 usuarios, 1 categoria y 1 producto de ejemplo");
        };
    }

    private Rol crearRol(RolRepository rolRepository, String nombre) {
        return rolRepository.findByNombre(nombre).orElseGet(() -> rolRepository.save(new Rol(nombre)));
    }

    private void crearUsuario(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                              String username, String rawPassword, String nombre, String apellido,
                              String email, Set<Rol> roles) {
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(passwordEncoder.encode(rawPassword)); // hash BCrypt
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setEmail(email);
        usuario.setRoles(roles);
        usuarioRepository.save(usuario);
    }
}
