package cscie97.asn4.housemate.entitlement;

public class Resource implements Visitable {
    private final String name;

    public Resource(String name) {
        this.name = name;
    }

    public String getName() { return name; }

    public boolean checkAccess(Resource resource) {
        if (resource == null) return false;
        return this.name.equals(resource.getName()) || resource.getName().startsWith(this.name + ".");
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitResource(this);
    }
}
