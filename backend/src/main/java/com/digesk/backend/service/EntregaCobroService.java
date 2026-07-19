package com.digesk.backend.service;

import com.digesk.backend.dto.EntregaCobroDTO;
import com.digesk.backend.dto.ResultadoEntregaDTO;
import com.digesk.backend.entity.Comprobante;
import com.digesk.backend.entity.Cotizacion;
import com.digesk.backend.entity.Pedido;
import com.digesk.backend.repository.ComprobanteRepository;
import com.digesk.backend.repository.CotizacionRepository;
import com.digesk.backend.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class EntregaCobroService {

    @Autowired private PedidoRepository pedidoRepository;
    @Autowired private CotizacionRepository cotizacionRepository;
    @Autowired private ComprobanteRepository comprobanteRepository;

    @Transactional
    public ResultadoEntregaDTO registrarEntregaYCobro(EntregaCobroDTO dto) {
        Pedido pedido = pedidoRepository.findById(dto.getIdPedido())
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        if (!"Listo".equals(pedido.getEstado())) {
            throw new RuntimeException("El pedido debe estar en estado 'Listo' para registrar entrega. Estado actual: " + pedido.getEstado());
        }

        Cotizacion cotizacion = cotizacionRepository.findAll().stream()
                .filter(c -> c.getPedido().getIdPedido().equals(dto.getIdPedido()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("El pedido no tiene cotización generada"));

        // Crear comprobante
        Comprobante comprobante = new Comprobante();
        comprobante.setPedido(pedido);
        comprobante.setMonto(dto.getMontoRecibido());
        comprobante.setFechaPago(LocalDate.now());
        comprobante.setTipo(dto.getTipoComprobante());
        comprobante = comprobanteRepository.save(comprobante);

        // Actualizar estado del pedido a Entregado (siempre se entrega, pague o no completo)
        pedido.setEstado("Entregado");
        pedidoRepository.save(pedido);

        // Calcular saldo pendiente (CU-06: si no pagó completo, queda como deuda)
        BigDecimal saldoPendiente = cotizacion.getPrecioTotal().subtract(dto.getMontoRecibido());
        boolean pagoCompleto = saldoPendiente.compareTo(BigDecimal.ZERO) <= 0;

        return new ResultadoEntregaDTO(
                pedido.getIdPedido(), pedido.getEstado(), comprobante.getIdComprobante(),
                cotizacion.getPrecioTotal(), dto.getMontoRecibido(),
                pagoCompleto ? BigDecimal.ZERO : saldoPendiente, pagoCompleto
        );
    }

    // CU-06: listar pedidos con deuda pendiente
    public java.util.List<ResultadoEntregaDTO> listarDeudas() {
        java.util.List<Pedido> entregados = pedidoRepository.findAll().stream()
                .filter(p -> "Entregado".equals(p.getEstado()))
                .toList();

        java.util.List<ResultadoEntregaDTO> deudas = new java.util.ArrayList<>();

        for (Pedido pedido : entregados) {
            var cotizacionOpt = cotizacionRepository.findAll().stream()
                    .filter(c -> c.getPedido().getIdPedido().equals(pedido.getIdPedido()))
                    .findFirst();
            if (cotizacionOpt.isEmpty()) continue;

            BigDecimal totalPagado = comprobanteRepository.findAll().stream()
                    .filter(c -> c.getPedido().getIdPedido().equals(pedido.getIdPedido()))
                    .map(Comprobante::getMonto)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal saldo = cotizacionOpt.get().getPrecioTotal().subtract(totalPagado);
            if (saldo.compareTo(BigDecimal.ZERO) > 0) {
                deudas.add(new ResultadoEntregaDTO(
                        pedido.getIdPedido(), pedido.getEstado(), null,
                        cotizacionOpt.get().getPrecioTotal(), totalPagado, saldo, false
                ));
            }
        }
        return deudas;
    }

    // Registrar un abono adicional a una deuda existente
    @Transactional
    public ResultadoEntregaDTO registrarAbono(Integer idPedido, BigDecimal monto, String tipoComprobante) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        Comprobante comprobante = new Comprobante();
        comprobante.setPedido(pedido);
        comprobante.setMonto(monto);
        comprobante.setFechaPago(LocalDate.now());
        comprobante.setTipo(tipoComprobante);
        comprobanteRepository.save(comprobante);

        Cotizacion cotizacion = cotizacionRepository.findAll().stream()
                .filter(c -> c.getPedido().getIdPedido().equals(idPedido))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));

        BigDecimal totalPagado = comprobanteRepository.findAll().stream()
                .filter(c -> c.getPedido().getIdPedido().equals(idPedido))
                .map(Comprobante::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldo = cotizacion.getPrecioTotal().subtract(totalPagado);
        boolean pagoCompleto = saldo.compareTo(BigDecimal.ZERO) <= 0;

        return new ResultadoEntregaDTO(
                idPedido, pedido.getEstado(), comprobante.getIdComprobante(),
                cotizacion.getPrecioTotal(), totalPagado,
                pagoCompleto ? BigDecimal.ZERO : saldo, pagoCompleto
        );
    }
}