package com.david.trenes.security;

import com.david.trenes.model.Usuario;
import com.david.trenes.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MongoUserDetailsService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(usernameOrEmail);
        if (usuarioOpt.isEmpty()) {
            usuarioOpt = usuarioRepository.findByEmail(usernameOrEmail);
        }

        Usuario usuario = usuarioOpt.orElseThrow(
            () -> new UsernameNotFoundException("Usuario no encontrado: " + usernameOrEmail)
        );

        String role = usuario.getRole();
        List<SimpleGrantedAuthority> authorities = List.of();
        if (role != null && !role.isBlank()) {
            String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
            authorities = List.of(new SimpleGrantedAuthority(authority));
        }

        return User.builder()
            .username(usuario.getUsername())
            .password(usuario.getPassword())
            .authorities(authorities)
            .build();
    }
}
