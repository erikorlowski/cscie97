package cscie97.asn4.housemate.entitlement;

/**
 * CheckAccessVisitor evaluates whether a User (when visited) has the
 * specified Permission for a given Resource. It is used by the
 * EntitlementServiceApi to implement check access semantics.
 */
public class CheckAccessVisitor implements Visitor {
    private final Permission permission;
    private final Resource resource;
    private boolean hasAccess = false;

    /**
     * Create a CheckAccessVisitor.
     *
     * @param permission the permission to check
     * @param resource the resource being accessed
     */
    public CheckAccessVisitor(Permission permission, Resource resource) {
        this.permission = permission;
        this.resource = resource;
    }

    public boolean hasAccess() { return hasAccess; }

    @Override
    public void visitEntitlement(Entitlement entitlement) {
        // not used directly
    }

    @Override
    public void visitResourceRole(ResourceRole resourceRole) {
        if (resourceRole.checkAccess(permission, resource)) {
            hasAccess = true;
        }
    }

    @Override
    public void visitUser(User user) {
        // check user's resourceRoles
        for (ResourceRole rr : user.getResourceRoles()) {
            if (rr.checkAccess(permission, resource)) {
                hasAccess = true;
                return;
            }
        }
        // check user's entitlements (roles/permissions)
        for (Entitlement e : user.getEntitlements()) {
            if (e.checkAccess(permission)) {
                hasAccess = true;
                return;
            }
        }
    }

    @Override
    public void visitCredential(Credential credential) { }

    @Override
    public void visitResource(Resource resource) { }

    @Override
    public void visitAccessToken(AccessToken accessToken) { }
}

