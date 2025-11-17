package cscie97.asn4.housemate.entitlement;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A Role represents a collection of Entitlements (which may be Permissions or
 * other Roles). This class participates in the Composite design pattern: a
 * Role is a composite node that can contain child Entitlements, allowing the
 * system to treat individual permissions and grouped roles uniformly.
 */
public class Role extends Entitlement {
    private final Set<Entitlement> children = new HashSet<>();

    /**
     * Create a Role with the given identifier, name and description.
     *
     * @param id the role identifier
     * @param name the human readable name for the role
     * @param description a description of the role
     */
    public Role(String id, String name, String description) {
        super(id, name, description);
    }

    /**
     * Add a child entitlement to this role. Child entitlements can be
     * individual Permissions or other Roles (composite behaviour).
     *
     * @param newChild the entitlement to add (ignored if null)
     */
    public void addChild(Entitlement newChild) {
        if (newChild != null) {
            children.add(newChild);
        }
    }

    /**
     * Get an unmodifiable view of this role's children.
     *
     * @return the set of child entitlements
     */
    public Set<Entitlement> getChildren() {
        return Collections.unmodifiableSet(children);
    }

    /**
     * Check whether this role grants the provided permission. The check
     * traverses child entitlements and returns true if any child grants the
     * permission.
     *
     * @param permission the permission to check
     * @return true if any child entitlement grants the permission
     */
    @Override
    public boolean checkAccess(Permission permission) {
        for (Entitlement e : children) {
            if (e.checkAccess(permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Accept a Visitor to operate on this Role.
     *
     * @param visitor the visitor handling this role
     */
    @Override
    public void accept(Visitor visitor) {
        visitor.visitEntitlement(this);
    }
}
