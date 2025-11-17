package cscie97.asn4.housemate.entitlement;

/**
 * Represents an authentication token issued to a User. The token contains a
 * reference to the credential used to authenticate and the associated user.
 * It also records whether the token has administrative privileges and the
 * last time it was used.
 */
public class AccessToken implements Visitable {
    private final char[] token;
    private final Credential credential;
    private final User user;
    private final boolean isAdmin;
    private long lastUsedTimeMsecs;

    /**
     * Create a new AccessToken.
     *
     * @param token the token characters
     * @param user the user associated with the token
     * @param credential the credential used to authenticate
     */
    public AccessToken(char[] token, User user, Credential credential) {
        this.token = token;
        this.user = user;
        this.credential = credential;
        this.isAdmin = credential != null && credential.isAdmin();
        this.lastUsedTimeMsecs = System.currentTimeMillis();
    }

    public char[] getToken() { return token; }
    public Credential getCredential() { return credential; }
    public User getUser() { return user; }
    public boolean isAdmin() { return isAdmin; }
    public long getLastUsedTimeMsecs() { return lastUsedTimeMsecs; }

    /**
     * Update the last-used timestamp to the current system time.
     */
    public void touch() { lastUsedTimeMsecs = System.currentTimeMillis(); }

    /**
     * Accept a Visitor to operate on this AccessToken.
     *
     * @param visitor the visitor handling this access token
     */
    @Override
    public void accept(Visitor visitor) {
        visitor.visitAccessToken(this);
    }
}
