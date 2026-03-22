package es.maryshopping.backend.shared_kernel.security;

import es.maryshopping.backend.shared_kernel.exceptions.AuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio para acceder a la información del usuario autenticado.
 *
 * Extrae datos del JWT que Spring Security ya validó automáticamente.
 * El claim "sub" de Keycloak coincide con el customerId en base de datos.
 */
@Service
public class SecurityContextService {

    private static final String ROLE_PREFIX = "ROLE_";

    public UUID getCurrentUserId() {
        Jwt jwt = getJwtFromSecurityContext();
        String subject = jwt.getSubject();
        if (subject == null || subject.isBlank()) {
            throw new AuthenticationException("JWT token missing 'sub' claim");
        }
        return UUID.fromString(subject);
    }

    public String getCurrentUserEmail() {
        Jwt jwt = getJwtFromSecurityContext();
        String email = jwt.getClaimAsString("preferred_username");
        if (email == null || email.isBlank()) {
            email = jwt.getClaimAsString("email");
        }
        if (email == null || email.isBlank()) {
            throw new AuthenticationException("JWT token missing email claim");
        }
        return email;
    }

    public Set<String> getCurrentUserRoles() {
        Authentication authentication = getAuthentication();
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(this::removeRolePrefix)
                .collect(Collectors.toSet());
    }

    public boolean isAdministrator() {
        return getCurrentUserRoles().contains("administrator");
    }

    public boolean isCustomer() {
        return getCurrentUserRoles().contains("customer");
    }

    private Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationException("No authenticated user found");
        }
        return authentication;
    }

    private Jwt getJwtFromSecurityContext() {
        Authentication authentication = getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt jwt) {
            return jwt;
        }
        throw new AuthenticationException("Unexpected principal type: " + principal.getClass().getName());
    }

    private String removeRolePrefix(String authority) {
        if (authority.startsWith(ROLE_PREFIX)) {
            return authority.substring(ROLE_PREFIX.length()).toLowerCase();
        }
        return authority.toLowerCase();
    }
}

