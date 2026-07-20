package com.digesk.backend.service;

import com.digesk.backend.dto.LoginRequestDTO;
import com.digesk.backend.dto.LoginResponseDTO;
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

    /**
     * Valida usuario/contraseña y devuelve los datos de sesión (sin la contraseña).
     *
     * NOTA: compara la contraseña en texto plano porque así se guarda actualmente
     * en tb_usuario. Es la "opción rápida" documentada en el README para la demo;
     * antes de un uso real habría que migrar a contraseñas con hash (BCrypt).
     */
    public LoginResponseDTO login(LoginRequestDTO request) {
        Usuario u = usuarioRepository.findByNombre(request.getNombre())
                .orElseThrow(() -> new RuntimeException("Usuario o contraseña incorrectos"));

        if (!u.getContrasena().equals(request.getContrasena())) {
            throw new RuntimeException("Usuario o contraseña incorrectos");
        }

        return new LoginResponseDTO(u.getIdUsuario(), u.getNombre(), u.getRol());
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