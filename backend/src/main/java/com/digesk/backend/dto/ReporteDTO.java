package com.digesk.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public record ReporteDTO(BigDecimal ingresosTotales, long totalPedidos, List<ClienteFrecuenteDTO> clientesFrecuentes) {}