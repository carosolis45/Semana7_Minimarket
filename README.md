 Documentación OpenAPI (Semana 7)

### Swagger UI
La documentación interactiva está disponible en:
http://localhost:8080/swagger-ui/index.html

text

### Especificación OpenAPI
- JSON: `http://localhost:8080/v3/api-docs`
- Archivo: `openapi.json` (en la raíz del proyecto)

### Endpoints documentados

**Productos (5 endpoints)**
| Método | Endpoint | Roles |
|--------|----------|-------|
| GET | `/api/productos` | ADMIN, CAJERO, BODEGUERO, CLIENTE |
| GET | `/api/productos/{id}` | ADMIN, CAJERO, BODEGUERO, CLIENTE |
| POST | `/api/productos` | ADMIN |
| PUT | `/api/productos/{id}` | ADMIN |
| DELETE | `/api/productos/{id}` | ADMIN |

**Carritos (5 endpoints)**
| Método | Endpoint | Roles |
|--------|----------|-------|
| GET | `/api/carrito` | ADMIN |
| GET | `/api/carrito/{id}` | ADMIN, CLIENTE |
| POST | `/api/carrito` | CLIENTE |
| PUT | `/api/carrito/{id}` | ADMIN |
| DELETE | `/api/carrito/{id}` | CLIENTE |

---

# MiniMarket Plus — Autenticación por rol validada con pruebas unitarias (Desarrollo Backend II, PBY2202)

Actividad **sumativa de la Semana 6**: aplicación de mecanismos de autenticación
y autorización por rol en el backend de "MiniMarket Plus" (Spring Boot 3, Java 17)
y su validación mediante pruebas unitarias con **JUnit 5 + Mockito + Spring
Security Test + JaCoCo**. Construido sobre la base de las Semanas 4 y 5.

## Herramientas

| Herramienta | Uso |
|---|---|
| **JUnit 5 (Jupiter)** | Framework de pruebas. |
| **Mockito** | Simulación de dependencias en pruebas de servicio. |
| **Spring Security Test** | Pruebas de autorización con `@WithMockUser` + `MockMvc`. |
| **JaCoCo** | Cobertura de código (reporte en `target/site/jacoco/index.html`). |

## Seguridad: roles y control de acceso

Autenticación HTTP Basic + `@EnableMethodSecurity`. Roles y usuarios sembrados
por `DataLoader` (contraseñas cifradas con BCrypt):

| Usuario | Contraseña | Rol |
|---|---|---|
| `admin` | `admin123` | `ROLE_ADMIN` |
| `cajero` | `cajero123` | `ROLE_CAJERO` |
| `bodeguero` | `bodeguero123` | `ROLE_BODEGUERO` |
| `cliente` | `cliente123` | `ROLE_CLIENTE` |

### Matriz de autorización (`@PreAuthorize`)

| Recurso | Lectura | Crear / modificar | Eliminar |
|---|---|---|---|
| `/api/productos` | autenticados | **solo ADMIN** | solo ADMIN |
| `/api/inventario` | ADMIN/BODEGUERO/CAJERO | ADMIN/BODEGUERO | solo ADMIN |
| `/api/ventas` | ADMIN/CAJERO | **ADMIN/CAJERO** | — |
| `/api/auth/login` | público | — | — |

## Pruebas (60 en total)

### Pruebas de autorización (Spring Security Test + MockMvc)

| Clase de prueba | Verifica | Método cubierto |
|---|---|---|
| `ProductoControllerSecurityTest` | ADMIN crea/edita (200), CLIENTE/CAJERO 403, sin auth 401 | `ProductoController.guardar/actualizar/eliminar` |
| `InventarioControllerSecurityTest` | ADMIN/BODEGUERO registran (200), CAJERO/CLIENTE 403, DELETE solo ADMIN | `InventarioController.registrar/eliminar` |
| `VentaControllerSecurityTest` | CAJERO/ADMIN generan venta (200), CLIENTE/BODEGUERO 403 | `VentaController.guardarVenta` |
| `AuthControllerSecurityTest` | login válido 200 + roles, inválido 401, body vacío 400 | `AuthController.login` |

### Pruebas de servicio (JUnit 5 + Mockito) — trazabilidad prueba → método

| Clase de prueba | Método del servicio cubierto |
|---|---|
| `CarritoServiceTest` | `agregarProducto` (stock ok/insuficiente/inexistente/cantidad), `usuarioEsCorrecto`, CRUD |
| `InventarioServiceTest` | `datosMovimientoValidos`, `productoEsCorrecto`, `registrarMovimiento`, CRUD |
| `ProductoServiceTest` | `hayStock`, `descontarStock`, CRUD |
| `UsuarioServiceTest` | `registrar` (cifrado BCrypt), CRUD |

## Cobertura JaCoCo (clases clave)

| Clase | Cobertura |
|---|---|
| `ProductoServiceImpl` | 100 % |
| `UsuarioServiceImpl` | 100 % |
| `InventarioServiceImpl` | 98 % |
| `CarritoServiceImpl` | 97 % |
| `AuthController` / `SecurityConfig` / `DataLoader` | 100 % |

Evidencias (reporte JaCoCo y corrida de pruebas en terminal) en
[`evidencias/`](evidencias/).

## Mejoras aplicadas (retroalimentación del docente)

1. **Trazabilidad** prueba → método (tablas de arriba).
2. **`equals`/`hashCode`/`toString`** en las 8 entidades.
3. **Bean Validation** (`@NotBlank`, `@NotNull`, `@Positive`, `@Email`, `@Size`).
4. **Excepciones de dominio** (`StockInsuficienteException`,
   `RecursoNoEncontradoException`) + `@RestControllerAdvice`.
5. **Constantes externalizadas** (`Constantes`: roles, movimientos, mensajes).
6. **Pruebas de fallo** (403/401/400 de autorización y validación) y logging SLF4J.

## Ejecución

```bash
./mvnw clean test
# Reporte de cobertura: target/site/jacoco/index.html
# App: ./mvnw spring-boot:run  (HTTP Basic con los usuarios de la tabla)
```
