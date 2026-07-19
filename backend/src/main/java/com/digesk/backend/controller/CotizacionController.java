package com.digesk.backend.controller;

import com.digesk.backend.dto.CotizacionDTO;
import com.digesk.backend.service.CotizacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cotizaciones")
public class CotizacionController {

    @Autowired private CotizacionService cotizacionService;

    @GetMapping
    public List<CotizacionDTO> listar() { return cotizacionService.listar(); }

    @GetMapping("/{id}")
    public CotizacionDTO buscarPorId(@PathVariable Integer id) { return cotizacionService.buscarPorId(id); }

    @PostMapping
    public CotizacionDTO crear(@RequestBody CotizacionDTO dto) { return cotizacionService.crear(dto); }

    @PostMapping("/generar/{idPedido}")
    public CotizacionDTO generar(@PathVariable Integer idPedido) {
        return cotizacionService.generarCotizacion(idPedido);
    }

    @PutMapping("/{id}")
    public CotizacionDTO actualizar(@PathVariable Integer id, @RequestBody CotizacionDTO dto) { return cotizacionService.actualizar(id, dto); }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) { cotizacionService.eliminar(id); }
}