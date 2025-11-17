package cscie97.asn4.housemate.entitlement;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Role extends Entitlement {
    private final Set<Entitlement> children = new HashSet<>();

    public Role(String id, String name, String description) {
        super(id, name, description);
    }

    public void addChild(Entitlement newChild) {
        if (newChild != null) {
            children.add(newChild);
        }
    }

    public Set<Entitlement> getChildren() {
        return Collections.unmodifiableSet(children);
    }

    @Override
    public boolean checkAccess(Permission permission) {
        for (Entitlement e : children) {
            if (e.checkAccess(permission)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitEntitlement(this);
    }
}
