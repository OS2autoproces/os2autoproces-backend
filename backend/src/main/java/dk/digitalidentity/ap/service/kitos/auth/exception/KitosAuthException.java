package dk.digitalidentity.ap.service.kitos.auth.exception;

import org.springframework.http.HttpStatus;

public class KitosAuthException extends RuntimeException {
    public KitosAuthException(final HttpStatus status) {
        super("Kitos authentication failed with HTTP: " + status.value());
    }
}
