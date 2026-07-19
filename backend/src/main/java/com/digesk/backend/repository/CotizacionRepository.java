package com.digesk.backend.repository;

import com.digesk.backend.entity.Cotizacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CotizacionRepository extends JpaRepository<Cotizacion, Integer> {
}