package cscie97.asn4.housemate.entitlement;

/**
 * Base unchecked exception for the entitlement subsystem. Other entitlement
 * related exceptions extend this to provide a common supertype.
 */
public class EntitlementException extends RuntimeException {
    public EntitlementException(String message) { super(message); }
    public EntitlementException(String message, Throwable cause) { super(message, cause); }
}
