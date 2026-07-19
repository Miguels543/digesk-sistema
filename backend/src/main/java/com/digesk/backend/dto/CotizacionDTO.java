package com.digesk.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CotizacionDTO {
    private Integer idCotizacion;
    private BigDecimal precioTotal;
    private LocalDate fecha;
    private String estado;
    private Integer idPedido;

    public CotizacionDTO() {}

    public CotizacionDTO(Integer idCotizacion, BigDecimal precioTotal, LocalDate fecha, String estado, Integer idPedido) {
        this.idCotizacion = idCotizacion;
        this.precioTotal = precioTotal;
        this.fecha = fecha;
        this.estado = estado;
        this.idPedido = idPedido;
    }

    public Integer getIdCotizacion() { return idCotizacion; }
    public void setIdCotizacion(Integer idCotizacion) { this.idCotizacion = idCotizacion; }
    public BigDecimal getPrecioTotal() { return precioTotal; }
    public void setPrecioTotal(BigDecimal precioTotal) { this.precioTotal = precioTotal; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Integer getIdPedido() { return idPedido; }
    public void setIdPedido(Integer idPedido) { this.idPedido = idPedido; }
}