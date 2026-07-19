package com.digesk.backend.controller;

import com.digesk.backend.dto.PedidoDTO;
import com.digesk.backend.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired private PedidoService pedidoService;

    @GetMapping
    public List<PedidoDTO> listar() { return pedidoService.listar(); }

    @GetMapping("/{id}")
    public PedidoDTO buscarPorId(@PathVariable Integer id) { return pedidoService.buscarPorId(id); }

    @PostMapping
    public PedidoDTO crear(@RequestBody PedidoDTO dto) { return pedidoService.crear(dto); }

    @PutMapping("/{id}")
    public PedidoDTO actualizar(@PathVariable Integer id, @RequestBody PedidoDTO dto) { return pedidoService.actualizar(id, dto); }

    @PutMapping("/{id}/estado")
    public PedidoDTO actualizarEstado(@PathVariable Integer id,
                                        @RequestParam String estado,
                                        @RequestParam(defaultValue = "false") boolean confirmar) {
        return pedidoService.actualizarEstado(id, estado, confirmar);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) { pedidoService.eliminar(id); }
}