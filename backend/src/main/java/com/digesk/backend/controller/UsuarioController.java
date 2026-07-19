package com.digesk.backend.controller;

import com.digesk.backend.dto.UsuarioDTO;
import com.digesk.backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired private UsuarioService usuarioService;

    @GetMapping
    public List<UsuarioDTO> listar() { return usuarioService.listar(); }

    @GetMapping("/{id}")
    public UsuarioDTO buscarPorId(@PathVariable Integer id) { return usuarioService.buscarPorId(id); }

    @PostMapping
    public UsuarioDTO crear(@RequestBody UsuarioDTO dto) { return usuarioService.crear(dto); }

    @PutMapping("/{id}")
    public UsuarioDTO actualizar(@PathVariable Integer id, @RequestBody UsuarioDTO dto) { return usuarioService.actualizar(id, dto); }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) { usuarioService.eliminar(id); }
}