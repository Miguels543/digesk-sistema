package com.digesk.backend.dto;

public class LoginRequestDTO {

    private String nombre;
    private String contrasena;

    public LoginRequestDTO() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
}