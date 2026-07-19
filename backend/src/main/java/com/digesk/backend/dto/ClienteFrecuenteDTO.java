package com.digesk.backend.dto;

import java.math.BigDecimal;

public record ClienteFrecuenteDTO(String nombre, int pedidos, BigDecimal totalPagado) {}