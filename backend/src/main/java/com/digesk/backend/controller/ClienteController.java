package com.digesk.backend.controller;

import com.digesk.backend.entity.Cliente;
import com.digesk.backend.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    // Listar todos los clientes
    @GetMapping
    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    // Buscar un cliente por id
    @GetMapping("/{id}")
    public Optional<Cliente> buscarPorId(@PathVariable Integer id) {
        return clienteRepository.findById(id);
    }

    // Crear un cliente nuevo
    @PostMapping
    public Cliente crear(@RequestBody Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    // Actualizar un cliente existente
    @PutMapping("/{id}")
    public Cliente actualizar(@PathVariable Integer id, @RequestBody Cliente clienteActualizado) {
        clienteActualizado.setIdCliente(id);
        return clienteRepository.save(clienteActualizado);
    }

    // Eliminar un cliente
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        clienteRepository.deleteById(id);
    }
}