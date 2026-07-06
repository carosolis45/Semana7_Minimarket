package com.minimarket.security.model;

import java.util.List;

/**
 * DTO de respuesta del login: confirma la autenticacion y devuelve el username
 * y los roles del usuario, sin exponer la contrasena.
 */
public class LoginResponse {

    private boolean autenticado;
    private String username;
    private List<String> roles;
    private String mensaje;

    public LoginResponse(boolean autenticado, String username, List<String> roles, String mensaje) {
        this.autenticado = autenticado;
        this.username = username;
        this.roles = roles;
        this.mensaje = mensaje;
    }

    public boolean isAutenticado() {
        return autenticado;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public String getMensaje() {
        return mensaje;
    }
}
