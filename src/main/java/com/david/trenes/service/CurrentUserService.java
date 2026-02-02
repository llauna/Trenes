package com.david.trenes.security;

import com.david.trenes.model.Usuario;
import com.david.trenes.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UsuarioRepository usuarioRepository;

    public Usuario getCurrentUsuario() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName() == null) {
            throw new RuntimeException("Usuario no autenticado");
        }

        String username = auth.getName();
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado para username: " + username));
    }

    public String getCurrentUsuarioId() {
        return getCurrentUsuario().getId();
    }
}