package cscie97.asn4.housemate.entitlement;

public class AccessDeniedException extends Exception {
    private final String objectAttemptedToAccess;
    private final String user;

    public AccessDeniedException(String objectAttemptedToAccess, String user) {
        super("Access denied for user: " + user + " on " + objectAttemptedToAccess);
        this.objectAttemptedToAccess = objectAttemptedToAccess;
        this.user = user;
    }

    public String getObjectAttemptedToAccess() { return objectAttemptedToAccess; }
    public String getUser() { return user; }
}
