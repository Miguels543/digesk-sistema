package com.digesk.backend.dto;

public class UsuarioDTO {
    private Integer idUsuario;
    private String nombre;
    private String contrasena;
    private String rol;

    public UsuarioDTO() {}

    public UsuarioDTO(Integer idUsuario, String nombre, String contrasena, String rol) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}