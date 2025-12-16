package cscie97.asn5.ngatc.systemmonitor;

/**
 * Exception thrown when database operations fail in the System Monitor module.
 */
public class DatabaseException extends RuntimeException {
    private final String message;

    public DatabaseException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
