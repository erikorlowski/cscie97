package cscie97.asn4.housemate.entitlement;

public class AccessToken implements Visitable {
    private final char[] token;
    private final Credential credential;
    private final User user;
    private final boolean isAdmin;
    private long lastUsedTimeMsecs;

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

    public void touch() { lastUsedTimeMsecs = System.currentTimeMillis(); }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitAccessToken(this);
    }
}
