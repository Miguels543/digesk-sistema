package com.digesk.backend.service;

import com.digesk.backend.dto.ClienteDTO;
import com.digesk.backend.entity.Cliente;
import com.digesk.backend.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public List<ClienteDTO> listar() {
        return clienteRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public ClienteDTO buscarPorId(Integer id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        return convertirADTO(cliente);
    }

    public ClienteDTO crear(ClienteDTO dto) {
        Cliente cliente = convertirAEntity(dto);
        return convertirADTO(clienteRepository.save(cliente));
    }

    public ClienteDTO actualizar(Integer id, ClienteDTO dto) {
        Cliente cliente = convertirAEntity(dto);
        cliente.setIdCliente(id);
        return convertirADTO(clienteRepository.save(cliente));
    }

    public void eliminar(Integer id) {
        clienteRepository.deleteById(id);
    }

    private ClienteDTO convertirADTO(Cliente c) {
        return new ClienteDTO(c.getIdCliente(), c.getNombre(), c.getTelefono(), c.getCorreo(), c.getTipo());
    }

    private Cliente convertirAEntity(ClienteDTO dto) {
        Cliente c = new Cliente();
        c.setIdCliente(dto.getIdCliente());
        c.setNombre(dto.getNombre());
        c.setTelefono(dto.getTelefono());
        c.setCorreo(dto.getCorreo());
        c.setTipo(dto.getTipo());
        return c;
    }
}