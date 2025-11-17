package cscie97.asn4.housemate.entitlement;

public class AuthenticationException extends Exception {
    private final String user;

    public AuthenticationException(String user) {
        super("Authentication failed for user: " + user);
        this.user = user;
    }

    public String getUser() { return user; }
}
