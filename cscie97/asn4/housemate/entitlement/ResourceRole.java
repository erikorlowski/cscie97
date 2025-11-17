package cscie97.asn4.housemate.entitlement;

public class ResourceRole implements Visitable {
    private final String name;
    private final Role role;
    private final Resource resource;

    public ResourceRole(String name, Role role, Resource resource) {
        this.name = name;
        this.role = role;
        this.resource = resource;
    }

    public String getName() { return name; }
    public Role getRole() { return role; }
    public Resource getResource() { return resource; }

    public boolean checkAccess(Permission permission, Resource resource) {
        if (!this.resource.checkAccess(resource)) return false;
        return role != null && role.checkAccess(permission);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitResourceRole(this);
    }
}
