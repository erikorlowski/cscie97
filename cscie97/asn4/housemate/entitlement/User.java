package cscie97.asn4.housemate.entitlement;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a user of the Housemate system. Users hold credentials,
 * entitlements (permissions and roles) and resource roles.
 */
public class User implements Visitable {
    private final String id;
    private final String name;
    private final Set<Credential> credentials = new HashSet<>();
    private final Set<Entitlement> entitlements = new HashSet<>();
    private final Set<ResourceRole> resourceRoles = new HashSet<>();

    /**
     * Create a new user.
     *
     * @param id the user identifier
     * @param name the user's name
     */
    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }
    /**
     * Get the user's identifier.
     *
     * @return the user id
     */
    public String getId() { return id; }

    /**
     * Get the user's display name.
     *
     * @return the user name
     */
    public String getName() { return name; }

    /**
     * Get the credentials associated with this user.
     *
     * @return the mutable set of credentials for the user
     */
    public Set<Credential> getCredentials() { return credentials; }

    /**
     * Get the entitlements (permissions and roles) assigned to this user.
     *
     * @return the set of entitlements
     */
    public Set<Entitlement> getEntitlements() { return entitlements; }

    /**
     * Get the resource roles assigned to this user.
     *
     * @return the set of resource roles
     */
    public Set<ResourceRole> getResourceRoles() { return resourceRoles; }

    /**
     * Add a credential to the user. Null values are ignored.
     *
     * @param c the credential to add
     */
    public void addCredential(Credential c) { if (c != null) credentials.add(c); }

    /**
     * Add an entitlement (permission or role) to the user. Null values are ignored.
     *
     * @param e the entitlement to add
     */
    public void addEntitlement(Entitlement e) { if (e != null) entitlements.add(e); }

    /**
     * Assign a resource role to the user. Null values are ignored.
     *
     * @param r the resource role to add
     */
    public void addResourceRole(ResourceRole r) { if (r != null) resourceRoles.add(r); }

    /**
     * Accept a Visitor to operate on this User.
     *
     * @param visitor the visitor handling this user
     */
    @Override
    public void accept(Visitor visitor) {
        visitor.visitUser(this);
    }
}

