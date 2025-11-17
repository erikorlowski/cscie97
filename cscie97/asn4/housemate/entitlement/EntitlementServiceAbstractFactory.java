package cscie97.asn4.housemate.entitlement;

/**
 * Simple abstract factory for creating entitlement domain objects. Implemented
 * as a singleton to centralize object creation for the Entitlement service.
 */
public class EntitlementServiceAbstractFactory {
    private static final EntitlementServiceAbstractFactory instance = new EntitlementServiceAbstractFactory();

    private EntitlementServiceAbstractFactory() {}

    public static EntitlementServiceAbstractFactory getInstance() { return instance; }

    public Permission createPermission(String id, String name, String description) {
        return new Permission(id, name, description);
    }

    public Role createRole(String id, String name, String description) {
        return new Role(id, name, description);
    }

    public User createUser(String id, String name) {
        return new User(id, name);
    }

    public Credential createCredential(String userId, boolean isPassword, String value) {
        return new Credential(userId, isPassword, value);
    }

    public Resource createResource(String name) {
        return new Resource(name);
    }

    public ResourceRole createResourceRole(String name, Role role, Resource resource) {
        return new ResourceRole(name, role, resource);
    }

    public AccessToken createAccessToken(User user, Credential credential) {
        char[] token = Long.toHexString(System.nanoTime()).toCharArray();
        return new AccessToken(token, user, credential);
    }
}
