package es.maryshopping.backend.shared_kernel.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio de lista negra de tokens para invalidación inmediata de Access Tokens.
 *
 * Los JWT son stateless (sin estado en el servidor): una vez emitidos, Spring Security
 * los valida localmente con la clave pública de Keycloak y no pregunta al servidor si
 * el token sigue siendo válido. Esto significa que un Access Token sigue funcionando
 * hasta que expira, incluso después del logout.
 *
 * Para solucionarlo, este servicio almacena en memoria los hashes SHA-256 de los tokens
 * revocados. Cuando Spring Security recibe un token, comprueba primero si está en la
 * lista negra antes de aceptarlo.
 *
 * Las entradas se limpian automáticamente cada 15 minutos para eliminar tokens que
 * ya habrían expirado de forma natural. En un monolito de una sola instancia (como
 * este proyecto), el almacenamiento en memoria es suficiente.
 */
@Service
@Slf4j
public class TokenBlacklistService {

    /**
     * Mapa: SHA-256 del token → instante en el que se añadió a la lista negra.
     */
    private final ConcurrentHashMap<String, Instant> blacklist = new ConcurrentHashMap<>();

    /**
     * Tiempo máximo que una entrada permanece en la lista negra (30 minutos).
     */
    private static final long MAX_ENTRY_AGE_SECONDS = 1800;

    /**
     * Añade un token a la lista negra.
     */
    public void blacklist(String rawToken) {
        String hash = sha256(rawToken);
        blacklist.put(hash, Instant.now());
        log.debug("Token añadido a la lista negra (hash: {}...)", hash.substring(0, 8));
    }

    /**
     * Comprueba si un token está en la lista negra.
     */
    public boolean isBlacklisted(String rawToken) {
        String hash = sha256(rawToken);
        return blacklist.containsKey(hash);
    }

    /**
     * Limpieza programada: elimina entradas expiradas cada 15 minutos.
     * Un token que lleva más de 30 minutos en la lista negra ya habría expirado
     * de forma natural, así que no necesitamos seguir guardándolo.
     */
    @Scheduled(fixedRate = 900_000) // cada 15 minutos
    public void cleanup() {
        Instant cutoff = Instant.now().minusSeconds(MAX_ENTRY_AGE_SECONDS);
        int before = blacklist.size();

        blacklist.entrySet().removeIf(entry -> entry.getValue().isBefore(cutoff));

        int removed = before - blacklist.size();
        if (removed > 0) {
            log.debug("Limpieza de lista negra: {} entradas eliminadas, {} restantes",
                    removed, blacklist.size());
        }
    }

    /**
     * Calcula el hash SHA-256 del token para no almacenar el token completo en memoria.
     */
    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 siempre está disponible en cualquier JVM
            throw new RuntimeException("SHA-256 no disponible", e);
        }
    }
}


