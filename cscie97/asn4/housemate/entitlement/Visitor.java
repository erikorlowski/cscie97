package cscie97.asn4.housemate.entitlement;

public interface Visitor {
    void visitEntitlement(Entitlement entitlement);
    void visitResourceRole(ResourceRole resourceRole);
    void visitUser(User user);
    void visitCredential(Credential credential);
    void visitResource(Resource resource);
    void visitAccessToken(AccessToken accessToken);
}
