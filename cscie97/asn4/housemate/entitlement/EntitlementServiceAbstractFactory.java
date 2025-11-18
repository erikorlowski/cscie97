package cscie97.asn4.housemate.entitlement;

/**
 * Simple abstract factory for creating entitlement domain objects. Implemented
 * as a singleton to centralize object creation for the Entitlement service.
 */
public class EntitlementServiceAbstractFactory {
    private static final EntitlementServiceAbstractFactory instance = new EntitlementServiceAbstractFactory();

    private EntitlementServiceAbstractFactory() {}

    /**
     * Return the singleton factory instance.
     *
     * @return the factory singleton
     */
    public static EntitlementServiceAbstractFactory getInstance() { return instance; }

    /**
     * Create a Permission domain object.
     *
     * @param id identifier for the permission
     * @param name human-readable name
     * @param description description of the permission
     * @return a new Permission instance
     */
    public Permission createPermission(String id, String name, String description) {
        return new Permission(id, name, description);
    }

    /**
     * Create a Role domain object.
     *
     * @param id identifier for the role
     * @param name human-readable name
     * @param description description of the role
     * @return a new Role instance
     */
    public Role createRole(String id, String name, String description) {
        return new Role(id, name, description);
    }

    /**
     * Create a User domain object.
     *
     * @param id user identifier
     * @param name user name
     * @return a new User instance
     */
    public User createUser(String id, String name) {
        return new User(id, name);
    }

    /**
     * Create a Credential object for a user. If creating a password
     * credential, the provided value will be hashed by the Credential
     * constructor.
     *
     * @param userId associated user id
     * @param isPassword true for password credentials, false for voiceprint
     * @param value the credential value (plain or pre-hashed)
     * @return a new Credential instance
     */
    public Credential createCredential(String userId, boolean isPassword, String value) {
        return new Credential(userId, isPassword, value);
    }

    /**
     * Create a Resource domain object.
     *
     * @param name resource name
     * @return a new Resource instance
     */
    public Resource createResource(String name) {
        return new Resource(name);
    }

    /**
     * Create a ResourceRole which ties a Role to a Resource.
     *
     * @param name resource role name
     * @param role the role granted
     * @param resource the resource covered by the role
     * @return a new ResourceRole instance
     */
    public ResourceRole createResourceRole(String name, Role role, Resource resource) {
        return new ResourceRole(name, role, resource);
    }

    /**
     * Create an AccessToken for the given user and credential. The token is
     * generated from a pseudo-random value and returned as a character array.
     *
     * @param user the user receiving the token
     * @param credential the credential associated with the token
     * @return a new AccessToken instance
     */
    public AccessToken createAccessToken(User user, Credential credential) {
        char[] token = Long.toHexString(System.nanoTime()).toCharArray();
        return new AccessToken(token, user, credential);
    }
}
