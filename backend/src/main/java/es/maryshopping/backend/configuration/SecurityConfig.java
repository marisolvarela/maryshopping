package es.maryshopping.backend.configuration;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Spring Security valida automáticamente los tokens JWT contra Keycloak
 * usando la configuración de application.yaml (issuer-uri, jwk-set-uri).
 *
 * Esta clase define:
 * - Qué endpoints son públicos (accesibles sin token, con token válido o con token caducado)
 * - Qué roles pueden acceder a cada endpoint
 * - Cómo extraer los roles de Keycloak del JWT
 *
 * IMPORTANTE — permitAll() con tokens caducados/revocados en rutas públicas:
 * Por defecto, si la petición lleva un token (aunque sea caducado o revocado),
 * Spring lo valida y puede devolver una respuesta vacía antes de llegar al controller.
 * Lo solucionamos con un BearerTokenResolver personalizado: en rutas públicas,
 * devolvemos null (sin token), por lo que Spring no intenta validarlo en absoluto.
 * En rutas protegidas, extraemos el token del header Authorization de forma normal.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String ROLE_PREFIX = "ROLE_";
    private static final String REALM_ACCESS_CLAIM = "realm_access";
    private static final String ROLES_CLAIM = "roles";
    private static final String ADMIN = "administrator";
    private static final String CUSTOMER = "customer";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth

                        // ── Públicos (sin token, token caducado o cualquier token) ──
                        .requestMatchers("/customers/iam/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        // GET proveedores (más específico, va antes del wildcard de productos)
                        .requestMatchers(HttpMethod.GET, "/catalog/products/providers").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.GET, "/catalog/products/providers/**").hasRole(ADMIN)
                        // GET catálogo (categorías, productos, imágenes): sin token
                        .requestMatchers(HttpMethod.GET, "/catalog/categories").permitAll()
                        .requestMatchers(HttpMethod.GET, "/catalog/categories/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/catalog/products").permitAll()
                        .requestMatchers(HttpMethod.GET, "/catalog/products/**").permitAll()

                        // ── Catalog: solo administrator (POST, PUT, DELETE) ───
                        .requestMatchers("/catalog/**").hasRole(ADMIN)

                        // ── Customers ─────────────────────────────────────────
                        .requestMatchers(HttpMethod.POST, "/customers").permitAll()
                        .requestMatchers(HttpMethod.GET, "/customers").hasRole(ADMIN)
                        .requestMatchers("/customers/**").hasAnyRole(CUSTOMER, ADMIN)

                        // ── Orders ────────────────────────────────────────────
                        .requestMatchers(HttpMethod.POST, "/orders/order").hasRole(CUSTOMER)
                        .requestMatchers(HttpMethod.GET, "/orders").hasRole(ADMIN)
                        .requestMatchers("/orders/**").hasAnyRole(CUSTOMER, ADMIN)

                        // ── Reports: solo administrator ───────────────────────
                        .requestMatchers("/reports/**").hasRole(ADMIN)

                        // ── Por defecto: autenticado ──────────────────────────
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .bearerTokenResolver(publicRoutesAwareBearerTokenResolver())
                )
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable);

        return http.build();
    }

    /**
     * BearerTokenResolver que devuelve null en rutas públicas.
     *
     * Si Spring no recibe ningún token, no intenta validarlo.
     * Esto evita respuestas vacías cuando Postman envía tokens caducados/revocados a rutas públicas.
     *
     * Para rutas protegidas, siempre extrae el token del header (si existe).
     * Si no existe token en rutas protegidas, Spring devuelve 401 correctamente.
     *
     * NOTA: Se usa getServletPath() en vez de PathPatternRequestMatcher para evitar
     * problemas con el context-path (/maryshopping) en Spring Security 6.
     */
    @Bean
    public BearerTokenResolver publicRoutesAwareBearerTokenResolver() {
        DefaultBearerTokenResolver standardResolver = new DefaultBearerTokenResolver();

        return (HttpServletRequest request) -> {
            String method = request.getMethod();
            String path = request.getServletPath();

            // Rutas siempre públicas (cualquier método)
            if (path.startsWith("/customers/iam/") || path.startsWith("/actuator/")) {
                return null;
            }

            // POST /customers: registro de nuevo cliente, sin token
            if ("POST".equalsIgnoreCase(method) && "/customers".equals(path)) {
                return null;
            }

            // GET /catalog/categories y subcategorías: sin token
            if ("GET".equalsIgnoreCase(method) && (path.equals("/catalog/categories") || path.startsWith("/catalog/categories/"))) {
                return null;
            }

            // GET /catalog/products y subcategorías: sin token
            // EXCEPTO /catalog/products/providers y /catalog/products/providers/**  → requieren ADMIN
            if ("GET".equalsIgnoreCase(method) && (path.equals("/catalog/products") || path.startsWith("/catalog/products/"))) {
                if (path.equals("/catalog/products/providers") || path.startsWith("/catalog/products/providers/")) {
                    // ruta protegida: extraer token normalmente
                    return standardResolver.resolve(request);
                }
                return null;
            }

            // Para el resto de rutas protegidas: extraer token del header Authorization
            // Si no hay token, el standardResolver devuelve null y Spring devolverá 401
            return standardResolver.resolve(request);
        };
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(this::extractKeycloakRoles);
        return converter;
    }

    private Collection<GrantedAuthority> extractKeycloakRoles(Jwt jwt) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        Map<String, Object> realmAccess = jwt.getClaim(REALM_ACCESS_CLAIM);
        if (realmAccess == null) {
            return authorities;
        }

        Object rolesObj = realmAccess.get(ROLES_CLAIM);
        if (rolesObj instanceof Collection<?> roles) {
            for (Object role : roles) {
                authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + role));
            }
        }

        return authorities;
    }
}

