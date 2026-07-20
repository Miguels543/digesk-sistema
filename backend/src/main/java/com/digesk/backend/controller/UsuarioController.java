package com.digesk.backend.controller;

import com.digesk.backend.dto.LoginRequestDTO;
import com.digesk.backend.dto.LoginResponseDTO;
import com.digesk.backend.dto.UsuarioDTO;
import com.digesk.backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        try {
            LoginResponseDTO response = usuarioService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }
}