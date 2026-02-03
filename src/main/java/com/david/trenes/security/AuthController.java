package com.david.trenes.security;

import com.david.trenes.model.Usuario;
import com.david.trenes.repository.UsuarioRepository;
import com.david.trenes.security.dto.*;
import com.david.trenes.util.ValidationUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:8082", "http://127.0.0.1:8082"})
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UsuarioRepository usuarioRepository;
    private final SecurityMonitoringService securityMonitoringService;
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        String clientIp = getClientIp(request);
        log.info("Autenticando usuario: {} desde IP: {}", loginRequest.getUsername(), clientIp);
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            String jwt = tokenProvider.generateToken(authentication);
            
            // Record successful login for monitoring
            securityMonitoringService.recordSuccessfulLogin(clientIp, loginRequest.getUsername());
            
            AuthResponse response = AuthResponse.builder()
                .token(jwt)
                .type("Bearer")
                .username(loginRequest.getUsername())
                .expiresIn(tokenProvider.getExpirationDateFromJWT(jwt))
                .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            log.error("Error en autenticación para usuario: {} desde IP: {}", loginRequest.getUsername(), clientIp, ex);
            
            // Record failed login for monitoring
            securityMonitoringService.recordFailedLogin(clientIp, loginRequest.getUsername());
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(AuthResponse.builder()
                    .error("Credenciales inválidas")
                    .build());
        }
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        log.info("Refrescando token");
        
        try {
            String token = request.getToken();
            
            if (!tokenProvider.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponse.builder()
                        .error("Token inválido")
                        .build());
            }
            
            String newToken = tokenProvider.refreshToken(token);
            
            if (newToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponse.builder()
                        .error("No se pudo refrescar el token")
                        .build());
            }
            
            AuthResponse response = AuthResponse.builder()
                .token(newToken)
                .type("Bearer")
                .username(tokenProvider.getUsernameFromJWT(newToken))
                .expiresIn(tokenProvider.getExpirationDateFromJWT(newToken))
                .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            log.error("Error refrescando token", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(AuthResponse.builder()
                    .error("Error interno del servidor")
                    .build());
        }
    }
    
    @PostMapping("/validate")
    public ResponseEntity<ValidationResponse> validateToken(@RequestBody ValidateTokenRequest request) {
        log.info("Validando token");
        
        try {
            String token = request.getToken();
            boolean isValid = tokenProvider.validateToken(token);
            
            ValidationResponse response = ValidationResponse.builder()
                .valid(isValid)
                .username(isValid ? tokenProvider.getUsernameFromJWT(token) : null)
                .expired(isValid ? tokenProvider.isTokenExpired(token) : true)
                .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            log.error("Error validando token", ex);
            return ResponseEntity.ok(ValidationResponse.builder()
                .valid(false)
                .expired(true)
                .build());
        }
    }
    
    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getCurrentUser() {
        log.info("Obteniendo información del usuario actual");
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            UserInfoResponse response = UserInfoResponse.builder()
                .username(authentication.getName())
                .authorities(authentication.getAuthorities().stream()
                    .map(Object::toString)
                    .toList())
                .authenticated(true)
                .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            log.error("Error obteniendo información del usuario", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        log.info("Cerrando sesión");
        
        SecurityContextHolder.clearContext();
        
        return ResponseEntity.ok().build();
    }
    
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
