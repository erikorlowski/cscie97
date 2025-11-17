package cscie97.asn4.housemate.entitlement;

public class InvalidAccessTokenException extends Exception {
    private final char[] accessToken;

    public InvalidAccessTokenException(char[] accessToken) {
        super("Invalid access token");
        this.accessToken = accessToken;
    }

    public char[] getAccessToken() { return accessToken; }
}
