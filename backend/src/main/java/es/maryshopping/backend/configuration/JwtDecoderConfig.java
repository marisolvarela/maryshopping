package es.maryshopping.backend.configuration;

import es.maryshopping.backend.shared_kernel.security.TokenBlacklistService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

/**
 * Configuración del decodificador de JWT con soporte para lista negra de tokens.
 *
 * Por defecto, Spring Boot auto-configura un JwtDecoder que valida la firma del token
 * contra las claves públicas de Keycloak (jwk-set-uri) y comprueba el emisor (issuer-uri).
 * Esa validación es puramente local (offline): Spring no pregunta a Keycloak si el token
 * sigue siendo válido.
 *
 * Este bean reemplaza el auto-configurado y añade una comprobación adicional:
 * antes de decodificar el token, comprueba si está en la lista negra (TokenBlacklistService).
 * Si el token ha sido revocado (porque el usuario hizo logout), se rechaza inmediatamente
 * con una JwtException, que Spring traduce a HTTP 401 Unauthorized.
 */
@Configuration
public class JwtDecoderConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Bean
    public JwtDecoder jwtDecoder(TokenBlacklistService tokenBlacklistService) {
        // El decoder se construye de forma lazy (en el primer uso, no en el arranque)
        // para evitar que un fallo temporal de conexión con Keycloak al arrancar
        // deje el bean en estado corrupto y provoque respuestas vacías.
        return new LazyJwtDecoder(jwkSetUri, issuerUri, tokenBlacklistService);
    }

    /**
     * JwtDecoder que inicializa el NimbusJwtDecoder en el primer uso (lazy).
     * Así el arranque no depende de que Keycloak esté disponible en ese instante.
     */
    private static class LazyJwtDecoder implements JwtDecoder {

        private final String jwkSetUri;
        private final String issuerUri;
        private final TokenBlacklistService tokenBlacklistService;
        private volatile NimbusJwtDecoder delegate;

        LazyJwtDecoder(String jwkSetUri, String issuerUri, TokenBlacklistService tokenBlacklistService) {
            this.jwkSetUri = jwkSetUri;
            this.issuerUri = issuerUri;
            this.tokenBlacklistService = tokenBlacklistService;
        }

        @Override
        public org.springframework.security.oauth2.jwt.Jwt decode(String token) throws JwtException {
            if (tokenBlacklistService.isBlacklisted(token)) {
                throw new JwtException("Token has been revoked");
            }
            return getDelegate().decode(token);
        }

        private NimbusJwtDecoder getDelegate() {
            if (delegate == null) {
                synchronized (this) {
                    if (delegate == null) {
                        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
                        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuerUri));
                        delegate = decoder;
                    }
                }
            }
            return delegate;
        }
    }
}


