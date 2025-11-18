package cscie97.asn4.housemate.entitlement;

/**
 * Exception thrown when authentication fails for a given user.
 */
public class AuthenticationException extends EntitlementException {
    private final String user;

    /**
     * Create an AuthenticationException for the specified user.
     *
     * @param user the user identifier
     */
    public AuthenticationException(String user) {
        super("Authentication failed for user: " + user);
        this.user = user;
    }

    /**
     * Get the identifier of the user for whom authentication failed.
     * @return the user identifier
     */
    public String getUser() { return user; }
}
