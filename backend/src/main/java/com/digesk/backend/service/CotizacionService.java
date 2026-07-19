package com.digesk.backend.service;

import com.digesk.backend.dto.CotizacionDTO;
import com.digesk.backend.entity.Cotizacion;
import com.digesk.backend.entity.Pedido;
import com.digesk.backend.entity.Producto;
import com.digesk.backend.repository.CotizacionRepository;
import com.digesk.backend.repository.PedidoRepository;
import com.digesk.backend.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CotizacionService {

    @Autowired private CotizacionRepository cotizacionRepository;
    @Autowired private PedidoRepository pedidoRepository;

    public List<CotizacionDTO> listar() {
        return cotizacionRepository.findAll().stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public CotizacionDTO buscarPorId(Integer id) {
        Cotizacion c = cotizacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));
        return convertirADTO(c);
    }

    public CotizacionDTO crear(CotizacionDTO dto) {
        return convertirADTO(cotizacionRepository.save(convertirAEntity(dto)));
    }

    public CotizacionDTO actualizar(Integer id, CotizacionDTO dto) {
        Cotizacion c = convertirAEntity(dto);
        c.setIdCotizacion(id);
        return convertirADTO(cotizacionRepository.save(c));
    }

    public void eliminar(Integer id) {
        cotizacionRepository.deleteById(id);
    }

    // Agregar arriba de la clase, como atributo estático:
    private static final java.util.Map<String, java.math.BigDecimal> PRECIOS_BASE = java.util.Map.of(
        "volante", new java.math.BigDecimal("0.50"),
        "tarjeta", new java.math.BigDecimal("0.80"),
        "afiche", new java.math.BigDecimal("3.00"),
        "banner", new java.math.BigDecimal("25.00"),
        "gigantografia", new java.math.BigDecimal("40.00"),
        "diseño", new java.math.BigDecimal("15.00")
    );
    private static final java.math.BigDecimal PRECIO_DEFAULT = new java.math.BigDecimal("2.00");

    @Autowired private ProductoRepository productoRepository;

    // Nuevo método:
    public CotizacionDTO generarCotizacion(Integer idPedido) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        Producto producto = productoRepository.findAll().stream()
                .filter(p -> p.getPedido().getIdPedido().equals(idPedido))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("El pedido no tiene producto asociado, no se puede cotizar"));

        String tipoNormalizado = producto.getTipo() != null ? producto.getTipo().toLowerCase().trim() : "";
        java.math.BigDecimal precioBase = PRECIOS_BASE.getOrDefault(tipoNormalizado, PRECIO_DEFAULT);
        java.math.BigDecimal cantidad = new java.math.BigDecimal(pedido.getCantidad());
        java.math.BigDecimal precioTotal = precioBase.multiply(cantidad);

        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setPedido(pedido);
        cotizacion.setPrecioTotal(precioTotal);
        cotizacion.setFecha(java.time.LocalDate.now());
        cotizacion.setEstado("Generada");

        return convertirADTO(cotizacionRepository.save(cotizacion));
    }

    private CotizacionDTO convertirADTO(Cotizacion c) {
        return new CotizacionDTO(c.getIdCotizacion(), c.getPrecioTotal(), c.getFecha(), c.getEstado(), c.getPedido().getIdPedido());
    }

    private Cotizacion convertirAEntity(CotizacionDTO dto) {
        Cotizacion c = new Cotizacion();
        c.setIdCotizacion(dto.getIdCotizacion());
        c.setPrecioTotal(dto.getPrecioTotal());
        c.setFecha(dto.getFecha());
        c.setEstado(dto.getEstado());
        Pedido pedido = pedidoRepository.findById(dto.getIdPedido())
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        c.setPedido(pedido);
        return c;
    }
}