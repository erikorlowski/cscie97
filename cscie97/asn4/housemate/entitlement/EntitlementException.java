package cscie97.asn4.housemate.entitlement;

/**
 * Base unchecked exception for the entitlement subsystem. Other entitlement
 * related exceptions extend this to provide a common supertype.
 */
public class EntitlementException extends Exception {
    private static final String PREFIX = "Entitlement Service Exception: ";

    /**
     * Construct an EntitlementException with the provided message. The
     * message will be prefixed to indicate the entitlement subsystem.
     *
     * @param message the detail message
     */
    public EntitlementException(String message) {
        super(PREFIX + message);
    }

    /**
     * Construct an EntitlementException with the provided message and cause.
     * The message will be prefixed to indicate the entitlement subsystem.
     *
     * @param message the detail message
     * @param cause the underlying cause
     */
    public EntitlementException(String message, Throwable cause) {
        super(PREFIX + message, cause);
    }
}
