package com.digesk.backend.controller;

import com.digesk.backend.dto.ProductoDTO;
import com.digesk.backend.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired private ProductoService productoService;

    @GetMapping
    public List<ProductoDTO> listar() { return productoService.listar(); }

    @GetMapping("/{id}")
    public ProductoDTO buscarPorId(@PathVariable Integer id) { return productoService.buscarPorId(id); }

    @PostMapping
    public ProductoDTO crear(@RequestBody ProductoDTO dto) { return productoService.crear(dto); }

    @PutMapping("/{id}")
    public ProductoDTO actualizar(@PathVariable Integer id, @RequestBody ProductoDTO dto) { return productoService.actualizar(id, dto); }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) { productoService.eliminar(id); }
}