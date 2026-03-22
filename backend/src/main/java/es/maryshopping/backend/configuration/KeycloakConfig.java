package es.maryshopping.backend.configuration;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Esta configuración crea un bean de Keycloak que se utiliza para:
 * - Crear, actualizar y eliminar usuarios
 * - Asignar roles a usuarios
 * - Gestionar credenciales
 *
 * Las credenciales se obtienen de las propiedades de configuración de Spring Boot.
 */
@Configuration
public class KeycloakConfig {

    @Value("${keycloak.admin-client.realm}")
    private String realm;

    @Value("${keycloak.admin-client.auth-server-url}")
    private String serverUrl;

    @Value("${keycloak.admin-client.client-id}")
    private String clientId;

    @Value("${keycloak.admin-client.client-secret}")
    private String clientSecret;

    /**
     * Crea un bean de Keycloak con credenciales de administrador.
     *
     * Este cliente se conecta a Keycloak usando credenciales de admin (client credentials grant).
     */
    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType("client_credentials")
                .build();
    }
}

