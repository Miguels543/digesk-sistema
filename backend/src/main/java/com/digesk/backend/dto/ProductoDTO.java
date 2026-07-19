package com.digesk.backend.dto;

public class ProductoDTO {
    private Integer idProducto;
    private String descripcion;
    private String tipo;
    private Integer idPedido;

    public ProductoDTO() {}

    public ProductoDTO(Integer idProducto, String descripcion, String tipo, Integer idPedido) {
        this.idProducto = idProducto;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.idPedido = idPedido;
    }

    public Integer getIdProducto() { return idProducto; }
    public void setIdProducto(Integer idProducto) { this.idProducto = idProducto; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Integer getIdPedido() { return idPedido; }
    public void setIdPedido(Integer idPedido) { this.idPedido = idPedido; }
}