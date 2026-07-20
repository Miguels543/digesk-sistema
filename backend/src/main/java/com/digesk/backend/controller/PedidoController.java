package com.digesk.backend.controller;

import com.digesk.backend.dto.PedidoDTO;
import com.digesk.backend.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired private PedidoService pedidoService;

    // ===== Lectura: disponible para cualquier usuario autenticado =====

    @GetMapping
    public List<PedidoDTO> listar() { return pedidoService.listar(); }

    @GetMapping("/sin-cotizar")
    public List<PedidoDTO> obtenerPedidosSinCotizar() {
        return pedidoService.obtenerPedidosSinCotizar();
    }

    @GetMapping("/{id}")
    public PedidoDTO buscarPorId(@PathVariable Integer id) { return pedidoService.buscarPorId(id); }

    // ===== Crear / editar / eliminar: exclusivo de Administrador (CU-04) =====

    @PostMapping
    public PedidoDTO crear(@RequestBody PedidoDTO dto,
                            @RequestHeader(value = "X-Rol", required = false) String rol) {
        exigirRol(rol, "Administrador");
        return pedidoService.crear(dto);
    }

    @PutMapping("/{id}")
    public PedidoDTO actualizar(@PathVariable Integer id, @RequestBody PedidoDTO dto,
                                 @RequestHeader(value = "X-Rol", required = false) String rol) {
        exigirRol(rol, "Administrador");
        return pedidoService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id,
                          @RequestHeader(value = "X-Rol", required = false) String rol) {
        exigirRol(rol, "Administrador");
        pedidoService.eliminar(id);
    }

    // ===== Actualizar estado: el único caso donde el Diseñador tiene permiso (CU-04) =====

    @PutMapping("/{id}/estado")
    public PedidoDTO actualizarEstado(@PathVariable Integer id,
                                        @RequestParam String estado,
                                        @RequestParam(defaultValue = "false") boolean confirmar,
                                        @RequestHeader(value = "X-Rol", required = false) String rol) {
        exigirRol(rol, "Administrador", "Diseñador");
        return pedidoService.actualizarEstado(id, estado, confirmar);
    }

    /**
     * Validación simple de rol vía header "X-Rol" (sin Spring Security completo,
     * como opción rápida documentada en el README del proyecto).
     * El frontend manda este header en cada request con el rol de la sesión activa.
     * NOTA: el rol viaja desde el cliente, así que esto no es seguridad robusta
     * contra alguien que llame la API directamente con credenciales falsificadas;
     * cubre el propósito de hacer cumplir la matriz de roles del CU-04 en el flujo normal.
     */
    private void exigirRol(String rolRecibido, String... rolesPermitidos) {
        for (String permitido : rolesPermitidos) {
            if (permitido.equals(rolRecibido)) return;
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para realizar esta acción");
    }
}