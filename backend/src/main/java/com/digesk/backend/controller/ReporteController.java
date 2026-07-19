package com.digesk.backend.controller;

import com.digesk.backend.dto.ClienteFrecuenteDTO;
import com.digesk.backend.dto.ReporteDTO;
import com.digesk.backend.entity.Comprobante;
import com.digesk.backend.repository.ComprobanteRepository;
import com.digesk.backend.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    @Autowired private ComprobanteRepository comprobanteRepository;
    @Autowired private PedidoRepository pedidoRepository;

    @GetMapping
    public ReporteDTO generarReporte(@RequestParam("desde") String desde,
                                       @RequestParam("hasta") String hasta) {

        LocalDate fechaDesde = LocalDate.parse(desde);
        LocalDate fechaHasta = LocalDate.parse(hasta);

        List<Comprobante> comprobantes = comprobanteRepository.findByFechaPagoBetween(fechaDesde, fechaHasta);

        BigDecimal ingresosTotales = comprobantes.stream()
                .map(Comprobante::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalPedidos = pedidoRepository.countByFechaEntregaBetween(fechaDesde, fechaHasta);

        Map<String, List<Comprobante>> porCliente = comprobantes.stream()
                .collect(Collectors.groupingBy(c -> c.getPedido().getCliente().getNombre()));

        List<ClienteFrecuenteDTO> clientesFrecuentes = porCliente.entrySet().stream()
                .map(e -> new ClienteFrecuenteDTO(
                        e.getKey(),
                        e.getValue().size(),
                        e.getValue().stream().map(Comprobante::getMonto).reduce(BigDecimal.ZERO, BigDecimal::add)))
                .sorted((a, b) -> b.pedidos() - a.pedidos())
                .collect(Collectors.toList());

        return new ReporteDTO(ingresosTotales, totalPedidos, clientesFrecuentes);
    }
}