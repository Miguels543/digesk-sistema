package com.digesk.backend.controller;

import com.digesk.backend.dto.ComprobanteDTO;
import com.digesk.backend.service.ComprobanteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comprobantes")
public class ComprobanteController {

    @Autowired private ComprobanteService comprobanteService;

    @GetMapping
    public List<ComprobanteDTO> listar() { return comprobanteService.listar(); }

    @GetMapping("/{id}")
    public ComprobanteDTO buscarPorId(@PathVariable Integer id) { return comprobanteService.buscarPorId(id); }

    @PostMapping
    public ComprobanteDTO crear(@RequestBody ComprobanteDTO dto) { return comprobanteService.crear(dto); }

    @PutMapping("/{id}")
    public ComprobanteDTO actualizar(@PathVariable Integer id, @RequestBody ComprobanteDTO dto) { return comprobanteService.actualizar(id, dto); }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) { comprobanteService.eliminar(id); }
}