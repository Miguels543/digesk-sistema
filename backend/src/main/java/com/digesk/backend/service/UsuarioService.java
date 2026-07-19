package com.digesk.backend.service;

import com.digesk.backend.dto.UsuarioDTO;
import com.digesk.backend.entity.Usuario;
import com.digesk.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired private UsuarioRepository usuarioRepository;

    public List<UsuarioDTO> listar() {
        return usuarioRepository.findAll().stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public UsuarioDTO buscarPorId(Integer id) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return convertirADTO(u);
    }

    public UsuarioDTO crear(UsuarioDTO dto) {
        return convertirADTO(usuarioRepository.save(convertirAEntity(dto)));
    }

    public UsuarioDTO actualizar(Integer id, UsuarioDTO dto) {
        Usuario u = convertirAEntity(dto);
        u.setIdUsuario(id);
        return convertirADTO(usuarioRepository.save(u));
    }

    public void eliminar(Integer id) {
        usuarioRepository.deleteById(id);
    }

    private UsuarioDTO convertirADTO(Usuario u) {
        return new UsuarioDTO(u.getIdUsuario(), u.getNombre(), u.getContrasena(), u.getRol());
    }

    private Usuario convertirAEntity(UsuarioDTO dto) {
        Usuario u = new Usuario();
        u.setIdUsuario(dto.getIdUsuario());
        u.setNombre(dto.getNombre());
        u.setContrasena(dto.getContrasena());
        u.setRol(dto.getRol());
        return u;
    }
}