package cscie97.asn4.housemate.entitlement;

/**
 * A Permission is the most fundamental entitlement in the system. It has an
 * identifier, name and description inherited from Entitlement. Permission
 * represents a single, checkable access.
 */
public class Permission extends Entitlement {
    /**
     * Create a new Permission with the given id, name and description.
     *
     * @param id the permission identifier
     * @param name the human-readable name
     * @param description the permission description
     */
    public Permission(String id, String name, String description) {
        super(id, name, description);
    }

    /**
     * Check whether this permission matches the provided permission. Matching
     * is performed by comparing permission identifiers.
     *
     * @param permission the permission to compare against
     * @return true if the identifiers are equal, false otherwise
     */
    @Override
    public boolean checkAccess(Permission permission) {
        return this.id != null && this.id.equals(permission.getId());
    }

    /**
     * Accept a Visitor to operate on this Permission.
     *
     * @param visitor the visitor handling this permission
     */
    @Override
    public void accept(Visitor visitor) {
        visitor.visitEntitlement(this);
    }
}
