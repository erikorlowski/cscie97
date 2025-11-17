package cscie97.asn4.housemate.entitlement;

public class Permission extends Entitlement {
    public Permission(String id, String name, String description) {
        super(id, name, description);
    }

    @Override
    public boolean checkAccess(Permission permission) {
        return this.id != null && this.id.equals(permission.getId());
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitEntitlement(this);
    }
}
