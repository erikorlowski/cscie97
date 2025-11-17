package cscie97.asn4.housemate.entitlement;

/**
 * Exception thrown when an access token cannot be resolved or is invalid.
 */
public class InvalidAccessTokenException extends Exception {
    private final char[] accessToken;

    /**
     * Create an InvalidAccessTokenException storing the offending token.
     *
     * @param accessToken the token that was invalid
     */
    public InvalidAccessTokenException(char[] accessToken) {
        super("Invalid access token");
        this.accessToken = accessToken;
    }

    public char[] getAccessToken() { return accessToken; }
}
