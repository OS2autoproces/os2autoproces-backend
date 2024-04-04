package dk.digitalidentity.ap.service.kitos.exception;

public class KitosSynchronizationException extends RuntimeException {
    public KitosSynchronizationException(final String error) {
        super(error);
    }
}
