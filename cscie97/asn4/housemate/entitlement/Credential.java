package cscie97.asn4.housemate.entitlement;

public class Credential implements Visitable {
    private final String userId;
    private final boolean isPassword;
    private final String value;
    private final boolean isAdmin;

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

    public boolean isMatch(String signInText) {
        if (signInText == null) return false;
        return value.equals(signInText);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitCredential(this);
    }
}
