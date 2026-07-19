package com.digesk.backend.repository;

import com.digesk.backend.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    List<Pedido> findByProductoIsNotNullAndCotizacionIsNull();
    long countByFechaEntregaBetween(LocalDate desde, LocalDate hasta);
}