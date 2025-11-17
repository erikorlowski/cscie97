package cscie97.asn4.housemate.entitlement;

public abstract class Entitlement implements Visitable {
    protected String id;
    protected String name;
    protected String description;

    public Entitlement(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }

    public abstract boolean checkAccess(Permission permission);
}
