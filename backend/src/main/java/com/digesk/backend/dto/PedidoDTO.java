package com.digesk.backend.dto;

import java.time.LocalDate;

public class PedidoDTO {
    private Integer idPedido;
    private String descripcion;
    private Integer cantidad;
    private LocalDate fechaEntrega;
    private String estado;
    private Integer idCliente;
    private Integer idUsuario;

    public PedidoDTO() {}

    public PedidoDTO(Integer idPedido, String descripcion, Integer cantidad, LocalDate fechaEntrega,
                      String estado, Integer idCliente, Integer idUsuario) {
        this.idPedido = idPedido;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.fechaEntrega = fechaEntrega;
        this.estado = estado;
        this.idCliente = idCliente;
        this.idUsuario = idUsuario;
    }

    public Integer getIdPedido() { return idPedido; }
    public void setIdPedido(Integer idPedido) { this.idPedido = idPedido; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public LocalDate getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDate fechaEntrega) { this.fechaEntrega = fechaEntrega; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Integer getIdCliente() { return idCliente; }
    public void setIdCliente(Integer idCliente) { this.idCliente = idCliente; }
    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
}