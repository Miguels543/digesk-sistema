package com.digesk.backend.repository;

import com.digesk.backend.entity.Comprobante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ComprobanteRepository extends JpaRepository<Comprobante, Integer> {
    List<Comprobante> findByFechaPagoBetween(LocalDate desde, LocalDate hasta);
}