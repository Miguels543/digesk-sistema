package com.digesk.backend.dto;

public class LoginResponseDTO {

    private Integer idUsuario;
    private String nombre;
    private String rol;

    public LoginResponseDTO() {}

    public LoginResponseDTO(Integer idUsuario, String nombre, String rol) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.rol = rol;
    }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}