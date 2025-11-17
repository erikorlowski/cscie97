package cscie97.asn4.housemate.entitlement;

/**
 * InventoryVisitor builds a textual inventory of the objects it visits. It is
 * useful for debugging and for the inventory_entitlement_service command.
 */
public class InventoryVisitor implements Visitor {
    private final StringBuilder inventory = new StringBuilder();

    public String getInventory() { return inventory.toString(); }

    @Override
    public void visitEntitlement(Entitlement entitlement) {
        inventory.append("Entitlement: ").append(entitlement.getName()).append("\n");
    }

    @Override
    public void visitResourceRole(ResourceRole resourceRole) {
        inventory.append("ResourceRole: ").append(resourceRole.getName()).append("\n");
    }

    @Override
    public void visitUser(User user) {
        inventory.append("User: ").append(user.getId()).append(" (").append(user.getName()).append(")\n");
    }

    @Override
    public void visitCredential(Credential credential) {
        inventory.append("Credential for: ").append(credential.getUserId()).append("\n");
    }

    @Override
    public void visitResource(Resource resource) {
        inventory.append("Resource: ").append(resource.getName()).append("\n");
    }

    @Override
    public void visitAccessToken(AccessToken accessToken) {
        inventory.append("AccessToken for: ").append(accessToken.getUser().getId()).append("\n");
    }
}
