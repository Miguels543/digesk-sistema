package com.digesk.backend.controller;

import com.digesk.backend.dto.EntregaCobroDTO;
import com.digesk.backend.dto.ResultadoEntregaDTO;
import com.digesk.backend.service.EntregaCobroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/entregas")
public class EntregaCobroController {

    @Autowired private EntregaCobroService entregaCobroService;

    // CU-05: registrar entrega y cobro en una sola operación
    @PostMapping
    public ResultadoEntregaDTO registrar(@RequestBody EntregaCobroDTO dto) {
        return entregaCobroService.registrarEntregaYCobro(dto);
    }

    // CU-06: listar pedidos con deuda pendiente
    @GetMapping("/deudas")
    public List<ResultadoEntregaDTO> deudas() {
        return entregaCobroService.listarDeudas();
    }

    // CU-06: registrar abono a una deuda existente
    @PostMapping("/{idPedido}/abono")
    public ResultadoEntregaDTO abonar(@PathVariable Integer idPedido,
                                        @RequestParam BigDecimal monto,
                                        @RequestParam String tipoComprobante) {
        return entregaCobroService.registrarAbono(idPedido, monto, tipoComprobante);
    }
}