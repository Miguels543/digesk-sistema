package com.digesk.backend.controller;

import com.digesk.backend.dto.ClienteDTO;
import com.digesk.backend.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public List<ClienteDTO> listar() { return clienteService.listar(); }

    @GetMapping("/{id}")
    public ClienteDTO buscarPorId(@PathVariable Integer id) { return clienteService.buscarPorId(id); }

    @PostMapping
    public ClienteDTO crear(@RequestBody ClienteDTO dto) { return clienteService.crear(dto); }

    @PutMapping("/{id}")
    public ClienteDTO actualizar(@PathVariable Integer id, @RequestBody ClienteDTO dto) { return clienteService.actualizar(id, dto); }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) { clienteService.eliminar(id); }
}