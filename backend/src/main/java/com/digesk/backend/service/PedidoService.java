package com.digesk.backend.service;

import com.digesk.backend.dto.PedidoDTO;
import com.digesk.backend.entity.Cliente;
import com.digesk.backend.entity.Pedido;
import com.digesk.backend.entity.Usuario;
import com.digesk.backend.repository.ClienteRepository;
import com.digesk.backend.repository.PedidoRepository;
import com.digesk.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    @Autowired private PedidoRepository pedidoRepository;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    public List<PedidoDTO> listar() {
        return pedidoRepository.findAll().stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public PedidoDTO buscarPorId(Integer id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        return convertirADTO(pedido);
    }

    public PedidoDTO crear(PedidoDTO dto) {
        Pedido pedido = convertirAEntity(dto);
        pedido.setEstado("Pendiente");
        return convertirADTO(pedidoRepository.save(pedido));
    }

    public PedidoDTO actualizar(Integer id, PedidoDTO dto) {
        Pedido pedido = convertirAEntity(dto);
        pedido.setIdPedido(id);
        return convertirADTO(pedidoRepository.save(pedido));
    }

    public void eliminar(Integer id) {
        pedidoRepository.deleteById(id);
    }

    // ===== NUEVO: pedidos con producto asignado pero sin cotización =====
    public List<PedidoDTO> obtenerPedidosSinCotizar() {
        return pedidoRepository.findByProductoIsNotNullAndCotizacionIsNull()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    private PedidoDTO convertirADTO(Pedido p) {
        return new PedidoDTO(
                p.getIdPedido(), p.getDescripcion(), p.getCantidad(), p.getFechaEntrega(),
                p.getEstado(), p.getCliente().getIdCliente(),
                p.getUsuario() != null ? p.getUsuario().getIdUsuario() : null
        );
    }

    private Pedido convertirAEntity(PedidoDTO dto) {
        Pedido p = new Pedido();
        p.setIdPedido(dto.getIdPedido());
        p.setDescripcion(dto.getDescripcion());
        p.setCantidad(dto.getCantidad());
        p.setFechaEntrega(dto.getFechaEntrega());
        p.setEstado(dto.getEstado());

        Cliente cliente = clienteRepository.findById(dto.getIdCliente())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        p.setCliente(cliente);

        if (dto.getIdUsuario() != null) {
            Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            p.setUsuario(usuario);
        }
        return p;
    }

    private static final java.util.List<String> ORDEN_ESTADOS =
        java.util.List.of("Pendiente", "En Producción", "Listo", "Entregado");

    public PedidoDTO actualizarEstado(Integer idPedido, String nuevoEstado, boolean confirmar) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        int indiceActual = ORDEN_ESTADOS.indexOf(pedido.getEstado());
        int indiceNuevo = ORDEN_ESTADOS.indexOf(nuevoEstado);

        if (indiceNuevo == -1) {
            throw new RuntimeException("Estado inválido. Usa: " + ORDEN_ESTADOS);
        }
        if (indiceNuevo < indiceActual && !confirmar) {
            throw new RuntimeException("Estás retrocediendo el estado de '" + pedido.getEstado() +
                    "' a '" + nuevoEstado + "'. Manda confirmar=true si es intencional.");
        }

        pedido.setEstado(nuevoEstado);
        return convertirADTO(pedidoRepository.save(pedido));
    }
}