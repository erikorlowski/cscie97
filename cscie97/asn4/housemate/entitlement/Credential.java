package cscie97.asn4.housemate.entitlement;

/**
 * Represents a user's credential, either a password or a voiceprint.
 * Password credentials are considered administrative credentials per the
 * system design.
 */
public class Credential implements Visitable {
    private final String userId;
    private final boolean isPassword;
    private final String value;
    private final boolean isAdmin;

    /**
     * Create a credential for the given user.
     *
     * @param userId the associated user id
     * @param isPassword true if this credential is a password, false for voiceprint
     * @param value the stored credential value
     */
    public Credential(String userId, boolean isPassword, String value) {
        this.userId = userId;
        this.isPassword = isPassword;
        this.value = value;
        this.isAdmin = isPassword; // password credentials denote admin per design
    }

    public String getUserId() { return userId; }
    public boolean isPassword() { return isPassword; }
    public String getValue() { return value; }
    public boolean isAdmin() { return isAdmin; }

    /**
     * Check whether the provided sign-in text matches this credential.
     *
     * @param signInText text supplied during authentication
     * @return true if the value matches the stored credential
     */
    public boolean isMatch(String signInText) {
        if (signInText == null) return false;
        return value.equals(signInText);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitCredential(this);
    }
}
