package com.digesk.backend.service;

import com.digesk.backend.dto.ProductoDTO;
import com.digesk.backend.entity.Pedido;
import com.digesk.backend.entity.Producto;
import com.digesk.backend.repository.PedidoRepository;
import com.digesk.backend.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    @Autowired private ProductoRepository productoRepository;
    @Autowired private PedidoRepository pedidoRepository;

    public List<ProductoDTO> listar() {
        return productoRepository.findAll().stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public ProductoDTO buscarPorId(Integer id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        return convertirADTO(producto);
    }

    public ProductoDTO crear(ProductoDTO dto) {
        return convertirADTO(productoRepository.save(convertirAEntity(dto)));
    }

    public ProductoDTO actualizar(Integer id, ProductoDTO dto) {
        Producto producto = convertirAEntity(dto);
        producto.setIdProducto(id);
        return convertirADTO(productoRepository.save(producto));
    }

    public void eliminar(Integer id) {
        productoRepository.deleteById(id);
    }

    private ProductoDTO convertirADTO(Producto p) {
        return new ProductoDTO(p.getIdProducto(), p.getDescripcion(), p.getTipo(), p.getPedido().getIdPedido());
    }

    private Producto convertirAEntity(ProductoDTO dto) {
        Producto p = new Producto();
        p.setIdProducto(dto.getIdProducto());
        p.setDescripcion(dto.getDescripcion());
        p.setTipo(dto.getTipo());
        Pedido pedido = pedidoRepository.findById(dto.getIdPedido())
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        p.setPedido(pedido);
        return p;
    }
}