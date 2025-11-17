package cscie97.asn4.housemate.entitlement;

/**
 * ResourceRole ties a Role to a specific Resource. It indicates that the
 * permissions provided by the Role apply to the named Resource (or objects
 * contained within that resource according to the resource hierarchy).
 */
public class ResourceRole implements Visitable {
    private final String name;
    private final Role role;
    private final Resource resource;

    /**
     * Create a ResourceRole with the given name, role and resource.
     *
     * @param name the resource role name
     * @param role the role that provides permissions
     * @param resource the resource to which the role applies
     */
    public ResourceRole(String name, Role role, Resource resource) {
        this.name = name;
        this.role = role;
        this.resource = resource;
    }

    /**
     * Get the resource role name.
     *
     * @return the name
     */
    public String getName() { return name; }

    /**
     * Get the Role associated with this ResourceRole.
     *
     * @return the role
     */
    public Role getRole() { return role; }

    /**
     * Get the Resource associated with this ResourceRole.
     *
     * @return the resource
     */
    public Resource getResource() { return resource; }

    /**
     * Check whether the given permission is granted for the provided resource
     * under this ResourceRole. The method first verifies that the provided
     * resource is covered by the ResourceRole's resource, then delegates to
     * the role to check the permission.
     *
     * @param permission the permission to check
     * @param resource the resource being accessed
     * @return true if the permission is granted for the resource
     */
    public boolean checkAccess(Permission permission, Resource resource) {
        if (!this.resource.checkAccess(resource)) return false;
        return role != null && role.checkAccess(permission);
    }

    /**
     * Accept a Visitor to operate on this ResourceRole.
     *
     * @param visitor the visitor handling this resource role
     */
    @Override
    public void accept(Visitor visitor) {
        visitor.visitResourceRole(this);
    }
}
