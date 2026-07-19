package com.digesk.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ComprobanteDTO {
    private Integer idComprobante;
    private BigDecimal monto;
    private LocalDate fechaPago;
    private String tipo;
    private Integer idPedido;

    public ComprobanteDTO() {}

    public ComprobanteDTO(Integer idComprobante, BigDecimal monto, LocalDate fechaPago, String tipo, Integer idPedido) {
        this.idComprobante = idComprobante;
        this.monto = monto;
        this.fechaPago = fechaPago;
        this.tipo = tipo;
        this.idPedido = idPedido;
    }

    public Integer getIdComprobante() { return idComprobante; }
    public void setIdComprobante(Integer idComprobante) { this.idComprobante = idComprobante; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public LocalDate getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDate fechaPago) { this.fechaPago = fechaPago; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Integer getIdPedido() { return idPedido; }
    public void setIdPedido(Integer idPedido) { this.idPedido = idPedido; }
}