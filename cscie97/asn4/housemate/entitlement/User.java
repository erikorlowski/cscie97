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

    public String getId() { return id; }
    public String getName() { return name; }

    public Set<Credential> getCredentials() { return credentials; }
    public Set<Entitlement> getEntitlements() { return entitlements; }
    public Set<ResourceRole> getResourceRoles() { return resourceRoles; }

    public void addCredential(Credential c) { if (c != null) credentials.add(c); }
    public void addEntitlement(Entitlement e) { if (e != null) entitlements.add(e); }
    public void addResourceRole(ResourceRole r) { if (r != null) resourceRoles.add(r); }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitUser(this);
    }
}

