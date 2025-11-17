package cscie97.asn4.housemate.entitlement;

/**
 * Exception thrown when a user attempts to access an object without the
 * required permissions.
 */
public class AccessDeniedException extends Exception {
    private final String objectAttemptedToAccess;
    private final String user;

    /**
     * Create an AccessDeniedException with information about the user and the
     * object they attempted to access.
     *
     * @param objectAttemptedToAccess the object the user tried to access
     * @param user the user identifier
     */
    public AccessDeniedException(String objectAttemptedToAccess, String user) {
        super("Access denied for user: " + user + " on " + objectAttemptedToAccess);
        this.objectAttemptedToAccess = objectAttemptedToAccess;
        this.user = user;
    }

    public String getObjectAttemptedToAccess() { return objectAttemptedToAccess; }
    public String getUser() { return user; }
}
