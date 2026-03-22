package es.maryshopping.backend.customers.shared.infrastructure.iam;

import es.maryshopping.backend.shared_kernel.exceptions.AuthenticationException;
import es.maryshopping.backend.shared_kernel.exceptions.DuplicatedEntityException;
import es.maryshopping.backend.shared_kernel.exceptions.EntityNotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

/**
 * Servicio de integración con Keycloak para autenticación y gestión de usuarios.
 *
 * Maneja operaciones de:
 * - Autenticación: login, refresh token, logout
 * - Gestión de usuarios: crear, actualizar, eliminar, asignar roles
 */
@Service
@Slf4j
public class KeycloakIAMService implements IAM {

    private final RestTemplate restTemplate;
    private final Keycloak keycloakAdmin;

    @Value("${keycloak.iam-client.realm}")
    private String realm;
    @Value("${keycloak.iam-client.auth-server-url}")
    private String serverUrl;
    @Value("${keycloak.iam-client.client-id}")
    private String clientId;
    @Value("${keycloak.iam-client.client-secret}")
    private String clientSecret;
    @Value("${keycloak.iam-client.grant-type}")
    private String grantType;

    public KeycloakIAMService(Keycloak keycloakAdmin) {
        this.keycloakAdmin = keycloakAdmin;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(10000);
        this.restTemplate = new RestTemplate(factory);
    }

    /**
     * Autentica un usuario con Keycloak usando email y contraseña.
     */
    @Override
    public TokenIamResponse authenticateUser(String email, String password) {
        log.debug("Autenticando usuario con email: {}", email);

        if (realm == null || realm.isEmpty()) {
            throw new AuthenticationException("Keycloak realm no está configurado");
        }

        try (Keycloak userClient = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .grantType(grantType)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .username(email)
                .password(password)
                .build()) {

            AccessTokenResponse tokenResponse = userClient.tokenManager().getAccessToken();

            log.debug("Autenticación exitosa. Token expira en: {} segundos", tokenResponse.getExpiresIn());

            return new TokenIamResponse(
                    tokenResponse.getToken(),
                    tokenResponse.getRefreshToken(),
                    tokenResponse.getTokenType(),
                    tokenResponse.getExpiresIn()
            );

        } catch (jakarta.ws.rs.NotAuthorizedException e) {
            log.warn("Autenticación fallida para usuario: {}", email);
            throw new AuthenticationException("Credenciales inválidas", e);
        } catch (Exception e) {
            log.warn("Error en autenticación para usuario: {}", email, e);
            throw new AuthenticationException("Error autenticando usuario: " + e.getMessage(), e);
        }
    }

    /**
     * Realiza logout del usuario llamando al endpoint OIDC de Keycloak.
     *
     * Llama al endpoint estándar de logout de OpenID Connect:
     *   POST /realms/{realm}/protocol/openid-connect/logout
     *
     * Esto invalida el refresh token Y cierra la sesión del usuario en Keycloak,
     * lo que impide obtener nuevos tokens.
     *
     * Nota: el Access Token (JWT) sigue siendo válido localmente hasta que expire,
     * porque es stateless. La invalidación inmediata del Access Token la gestiona
     * el TokenBlacklistService en el backend.
     */
    @Override
    public LogoutIamResponse logoutUser(String refreshToken) {
        log.debug("Procesando logout de usuario via OIDC endpoint");

        if (realm == null || realm.isEmpty()) {
            throw new AuthenticationException("Keycloak realm no está configurado");
        }

        try {
            String logoutUrl = serverUrl + "realms/" + realm
                    + "/protocol/openid-connect/logout";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> formBody = new LinkedMultiValueMap<>();
            formBody.add("client_id", clientId);
            formBody.add("client_secret", clientSecret);
            formBody.add("refresh_token", refreshToken);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(logoutUrl, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.debug("Logout exitoso — sesión de Keycloak cerrada");
                return new LogoutIamResponse(true, "Usuario desconectado correctamente");
            } else {
                log.warn("Logout en Keycloak devolvió HTTP {}", response.getStatusCode().value());
                return new LogoutIamResponse(true,
                        "Logout procesado (Keycloak respondió " + response.getStatusCode().value() + ")");
            }

        } catch (Exception e) {
            log.warn("Logout fallido: {}", e.getMessage(), e);
            throw new AuthenticationException("Operación de logout fallida: " + e.getMessage(), e);
        }
    }


    // =====================================================================
    // GESTIÓN DE USUARIOS - CREAR, ACTUALIZAR, ELIMINAR, ASIGNAR ROLES
    // =====================================================================

    /**
     * Crea un nuevo usuario en Keycloak.
     * Configura la contraseña de forma permanente (no temporal).
     * Opcionalmente asigna un rol realm al usuario.
     */
    @Override
    public String createUser(String username, String email, String firstName, String lastName,
                             String password, String roleName) {
        log.debug("Creando usuario: {} con email: {}", username, email);

        validateRealmConfiguration();

        try {
            var realmResource = keycloakAdmin.realm(realm);
            var usersResource = realmResource.users();

            // Crear representación del usuario
            UserRepresentation user = new UserRepresentation();
            user.setEnabled(true);
            user.setEmailVerified(true);
            user.setUsername(username);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setRealmRoles(Collections.singletonList("default-roles-" + realm));

            // Configurar contraseña permanente
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setTemporary(false);
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(password);
            user.setCredentials(Collections.singletonList(credential));

            // Crear usuario en Keycloak
            Response response = usersResource.create(user);

            if (response.getStatus() == 201) {
                String userId = extractUserIdFromLocationHeader(response);
                log.debug("✅ Usuario creado exitosamente con ID: {}", userId);

                // Asignar rol si se proporciona
                if (roleName != null && !roleName.isBlank()) {
                    try {
                        assignRealmRole(userId, roleName);
                        log.debug("✅ Rol '{}' asignado al usuario '{}'", roleName, userId);
                    } catch (Exception e) {
                        log.warn("⚠️ Usuario creado pero fallo la asignación de rol: {}", e.getMessage());
                        // Continuar - usuario creado, rol es secundario
                    }
                }

                return userId;
            } else if (response.getStatus() == 409) {
                log.warn("Usuario '{}' ya existe en Keycloak", username);
                throw new DuplicatedEntityException("Usuario con nombre o email ya existe");
            } else {
                log.warn("Fallo al crear usuario: HTTP {}", response.getStatus());
                throw new AuthenticationException("Error creando usuario: HTTP " + response.getStatus());
            }

        } catch (DuplicatedEntityException | AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Error creando usuario: {}", e.getMessage(), e);
            throw new AuthenticationException("Error creando usuario: " + e.getMessage(), e);
        }
    }

    /**
     * Actualiza datos de un usuario existente en Keycloak.
     * Permite cambiar: username, email, firstName, lastName, password.
     */
    @Override
    public void updateUser(String userId, String username, String email,
                           String firstName, String lastName, String password) {
        log.debug("Actualizando usuario: {}", userId);

        validateRealmConfiguration();

        try {
            var realmResource = keycloakAdmin.realm(realm);
            var usersResource = realmResource.users();
            var userResource = usersResource.get(userId);

            UserRepresentation user = userResource.toRepresentation();

            // Actualizar solo campos proporcionados
            if (username != null && !username.equals(user.getUsername())) {
                user.setUsername(username);
            }
            if (email != null && !email.equals(user.getEmail())) {
                user.setEmail(email);
            }
            if (firstName != null) {
                user.setFirstName(firstName);
            }
            if (lastName != null) {
                user.setLastName(lastName);
            }

            // Actualizar usuario
            try {
                userResource.update(user);
                log.debug("✅ Usuario actualizado: {}", userId);
            } catch (jakarta.ws.rs.BadRequestException e) {
                log.warn("Datos inválidos en actualización: {}", e.getMessage());
                throw new DuplicatedEntityException(
                    "Nombre de usuario o email ya en uso: " + e.getMessage()
                );
            }

            // Actualizar contraseña si se proporciona
            if (password != null && !password.isEmpty()) {
                CredentialRepresentation credential = new CredentialRepresentation();
                credential.setTemporary(false);
                credential.setType(CredentialRepresentation.PASSWORD);
                credential.setValue(password);
                userResource.resetPassword(credential);
                log.debug("✅ Contraseña actualizada para usuario: {}", userId);
            }

        } catch (jakarta.ws.rs.NotFoundException e) {
            log.warn("Usuario no encontrado: {}", userId);
            throw new EntityNotFoundException("Usuario no existe en Keycloak");
        } catch (DuplicatedEntityException | AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Error actualizando usuario: {}", e.getMessage(), e);
            throw new AuthenticationException("Error actualizando usuario: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina un usuario de Keycloak.
     */
    @Override
    public void deleteUser(String userId) {
        log.debug("Eliminando usuario: {}", userId);

        validateRealmConfiguration();

        try {
            var realmResource = keycloakAdmin.realm(realm);
            var usersResource = realmResource.users();

            try (Response response = usersResource.delete(userId)) {
                if (response.getStatus() == 204) {
                    log.debug("✅ Usuario eliminado: {}", userId);
                } else if (response.getStatus() == 404) {
                    log.warn("Usuario no encontrado: {}", userId);
                    throw new EntityNotFoundException("Usuario no existe en Keycloak");
                } else {
                    log.warn("Fallo al eliminar usuario: HTTP {}", response.getStatus());
                    throw new AuthenticationException("Error eliminando usuario: HTTP " + response.getStatus());
                }
            }

        } catch (EntityNotFoundException | AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Error eliminando usuario: {}", e.getMessage(), e);
            throw new AuthenticationException("Error eliminando usuario: " + e.getMessage(), e);
        }
    }

    /**
     * Asigna un rol realm a un usuario.
     */
    @Override
    public void assignRealmRole(String userId, String roleName) {
        log.debug("Asignando rol '{}' al usuario: {}", roleName, userId);

        try {
            var realmResource = keycloakAdmin.realm(realm);
            UserResource userResource = realmResource.users().get(userId);
            RoleScopeResource roleScopeResource = userResource.roles().realmLevel();

            // Obtener la representación del rol
            RoleRepresentation role = realmResource.roles().get(roleName).toRepresentation();
            if (role == null) {
                log.warn("Rol no encontrado: {}", roleName);
                throw new EntityNotFoundException("Rol no existe: " + roleName);
            }

            // Asignar rol al usuario
            roleScopeResource.add(List.of(role));
            log.debug("✅ Rol '{}' asignado al usuario: {}", roleName, userId);

        } catch (jakarta.ws.rs.NotFoundException e) {
            log.warn("Rol no encontrado en realm: {}", roleName);
            throw new EntityNotFoundException("Rol no existe: " + roleName, e);
        } catch (Exception e) {
            log.warn("Error asignando rol: {}", e.getMessage(), e);
            throw new AuthenticationException("Error asignando rol: " + e.getMessage(), e);
        }
    }

    /**
     * Valida que el realm esté configurado correctamente.
     */
    @Override
    public void validateRealmConfiguration() {
        if (realm == null || realm.isEmpty()) {
            log.error("Keycloak realm no está configurado");
            throw new AuthenticationException("Keycloak realm no está configurado");
        }
    }

    /**
     * Extrae el ID del usuario del header Location de la respuesta HTTP.
     */
    @Override
    public String extractUserIdFromLocationHeader(Response response) {
        String locationHeader = response.getHeaderString("Location");
        return locationHeader.substring(locationHeader.lastIndexOf("/") + 1);
    }
}
