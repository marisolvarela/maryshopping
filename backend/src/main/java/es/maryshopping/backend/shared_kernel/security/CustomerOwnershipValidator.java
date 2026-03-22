package es.maryshopping.backend.shared_kernel.security;

import es.maryshopping.backend.shared_kernel.exceptions.CustomerDataAccessForbiddenException;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Valida que el usuario autenticado tiene derecho a operar sobre un recurso de un customer concreto.
 *
 * Reglas:
 * - Si el usuario tiene rol "administrator" → siempre se permite el acceso.
 * - Si el usuario tiene rol "customer" → solo se permite si el "sub" del JWT
 *   (que es su customerId) coincide con el customerId del recurso solicitado.
 *
 * Se inyecta en los controladores que operan sobre datos vinculados a un customer.
 */
@Service
public class CustomerOwnershipValidator {

    private final SecurityContextService securityContextService;

    public CustomerOwnershipValidator(SecurityContextService securityContextService) {
        this.securityContextService = securityContextService;
    }

    /**
     * Valida que el usuario autenticado puede operar sobre los datos del customer indicado.
     */
    public void validate(UUID resourceCustomerId) {
        if (securityContextService.isAdministrator()) {
            return;
        }

        UUID authenticatedCustomerId = securityContextService.getCurrentUserId();

        if (!authenticatedCustomerId.equals(resourceCustomerId)) {
            throw new CustomerDataAccessForbiddenException(
                    "Customer " + authenticatedCustomerId
                            + " is not allowed to access data owned by customer " + resourceCustomerId
            );
        }
    }
}

