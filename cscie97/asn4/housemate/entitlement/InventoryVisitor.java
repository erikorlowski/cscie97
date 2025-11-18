package cscie97.asn4.housemate.entitlement;

/**
 * InventoryVisitor builds a textual inventory of the objects it visits. It is
 * useful for debugging and for the inventory_entitlement_service command.
 */
public class InventoryVisitor implements Visitor {
    private final StringBuilder inventory = new StringBuilder();

    /**
     * Return the built inventory as a single string.
     *
     * @return textual inventory of visited objects
     */
    public String getInventory() { return inventory.toString(); }

    /**
     * Visit an Entitlement and append a descriptive line to the inventory.
     *
     * @param entitlement the entitlement being visited
     */
    @Override
    public void visitEntitlement(Entitlement entitlement) {
        inventory.append("Entitlement: ").append(entitlement.getName()).append("\n");
    }

    /**
     * Visit a ResourceRole and append a descriptive line to the inventory.
     *
     * @param resourceRole the resource role being visited
     */
    @Override
    public void visitResourceRole(ResourceRole resourceRole) {
        inventory.append("ResourceRole: ").append(resourceRole.getName()).append("\n");
    }

    /**
     * Visit a User and append a descriptive line to the inventory.
     *
     * @param user the user being visited
     */
    @Override
    public void visitUser(User user) {
        inventory.append("User: ").append(user.getId()).append(" (").append(user.getName()).append(")\n");
    }

    /**
     * Visit a Credential and append a descriptive line to the inventory.
     *
     * @param credential the credential being visited
     */
    @Override
    public void visitCredential(Credential credential) {
        inventory.append("Credential for: ").append(credential.getUserId()).append("\n");
    }

    /**
     * Visit a Resource and append a descriptive line to the inventory.
     *
     * @param resource the resource being visited
     */
    @Override
    public void visitResource(Resource resource) {
        inventory.append("Resource: ").append(resource.getName()).append("\n");
    }

    /**
     * Visit an AccessToken and append a descriptive line to the inventory.
     *
     * @param accessToken the access token being visited
     */
    @Override
    public void visitAccessToken(AccessToken accessToken) {
        inventory.append("AccessToken for: ").append(accessToken.getUser().getId()).append("\n");
    }
}
