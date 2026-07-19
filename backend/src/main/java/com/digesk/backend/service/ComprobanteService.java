package com.digesk.backend.service;

import com.digesk.backend.dto.ComprobanteDTO;
import com.digesk.backend.entity.Comprobante;
import com.digesk.backend.entity.Pedido;
import com.digesk.backend.repository.ComprobanteRepository;
import com.digesk.backend.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComprobanteService {

    @Autowired private ComprobanteRepository comprobanteRepository;
    @Autowired private PedidoRepository pedidoRepository;

    public List<ComprobanteDTO> listar() {
        return comprobanteRepository.findAll().stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public ComprobanteDTO buscarPorId(Integer id) {
        Comprobante c = comprobanteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comprobante no encontrado"));
        return convertirADTO(c);
    }

    public ComprobanteDTO crear(ComprobanteDTO dto) {
        return convertirADTO(comprobanteRepository.save(convertirAEntity(dto)));
    }

    public ComprobanteDTO actualizar(Integer id, ComprobanteDTO dto) {
        Comprobante c = convertirAEntity(dto);
        c.setIdComprobante(id);
        return convertirADTO(comprobanteRepository.save(c));
    }

    public void eliminar(Integer id) {
        comprobanteRepository.deleteById(id);
    }

    private ComprobanteDTO convertirADTO(Comprobante c) {
        return new ComprobanteDTO(c.getIdComprobante(), c.getMonto(), c.getFechaPago(), c.getTipo(), c.getPedido().getIdPedido());
    }

    private Comprobante convertirAEntity(ComprobanteDTO dto) {
        Comprobante c = new Comprobante();
        c.setIdComprobante(dto.getIdComprobante());
        c.setMonto(dto.getMonto());
        c.setFechaPago(dto.getFechaPago());
        c.setTipo(dto.getTipo());
        Pedido pedido = pedidoRepository.findById(dto.getIdPedido())
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        c.setPedido(pedido);
        return c;
    }
}