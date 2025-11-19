package cscie97.asn4.housemate.entitlement;

/**
 * Exception thrown when a user attempts to access an object without the
 * required permissions.
 */
public class AccessDeniedException extends EntitlementException {
    private final String objectAttemptedToAccess;
    private final String user;

    /**
     * Create an AccessDeniedException with information about the user and the
     * object they attempted to access.
     *
     * @param objectAttemptedToAccess the object the user tried to access
     * @param resource the user identifier
     */
    public AccessDeniedException(String objectAttemptedToAccess, String resource, String message) {
        super("Access denied for resource: " + resource + " on " + objectAttemptedToAccess + ". Reason: " + message);
        this.objectAttemptedToAccess = objectAttemptedToAccess;
        this.user = resource;
    }

    /**
     * Get the identifier of the object the user attempted to access.
     *
     * @return the object identifier
     */
    public String getObjectAttemptedToAccess() { return objectAttemptedToAccess; }

    /**
     * Get the identifier of the user who attempted access.
     *
     * @return the user identifier
     */
    public String getUser() { return user; }
}
