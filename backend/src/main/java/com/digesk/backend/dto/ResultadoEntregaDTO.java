package com.digesk.backend.dto;

import java.math.BigDecimal;

public class ResultadoEntregaDTO {
    private Integer idPedido;
    private String estadoPedido;
    private Integer idComprobante;
    private BigDecimal precioTotal;
    private BigDecimal montoPagado;
    private BigDecimal saldoPendiente;
    private boolean pagoCompleto;

    public ResultadoEntregaDTO(Integer idPedido, String estadoPedido, Integer idComprobante,
                                 BigDecimal precioTotal, BigDecimal montoPagado,
                                 BigDecimal saldoPendiente, boolean pagoCompleto) {
        this.idPedido = idPedido;
        this.estadoPedido = estadoPedido;
        this.idComprobante = idComprobante;
        this.precioTotal = precioTotal;
        this.montoPagado = montoPagado;
        this.saldoPendiente = saldoPendiente;
        this.pagoCompleto = pagoCompleto;
    }

    public Integer getIdPedido() { return idPedido; }
    public String getEstadoPedido() { return estadoPedido; }
    public Integer getIdComprobante() { return idComprobante; }
    public BigDecimal getPrecioTotal() { return precioTotal; }
    public BigDecimal getMontoPagado() { return montoPagado; }
    public BigDecimal getSaldoPendiente() { return saldoPendiente; }
    public boolean isPagoCompleto() { return pagoCompleto; }
}