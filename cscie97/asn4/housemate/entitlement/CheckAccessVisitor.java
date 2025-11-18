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

    /**
     * Returns true if the visited subject has the requested access.
     *
     * @return true when access is granted
     */
    public boolean hasAccess() { return hasAccess; }

    /**
     * Visit an Entitlement and set access if it grants the required permission.
     *
     * @param entitlement the entitlement to evaluate
     */
    @Override
    public void visitEntitlement(Entitlement entitlement) {
        if(entitlement.checkAccess(permission)) {
            hasAccess = true;
        }
    }

    /**
     * Visit a ResourceRole and set access if it grants the required permission
     * on the target resource.
     *
     * @param resourceRole the resource role to evaluate
     */
    @Override
    public void visitResourceRole(ResourceRole resourceRole) {
        if (resourceRole.checkAccess(permission, resource)) {
            hasAccess = true;
        }
    }

    /**
     * Visit a User. No direct access evaluation is performed at the user node
     * for this visitor.
     *
     * @param user the user being visited
     */
    @Override
    public void visitUser(User user) { }

    /**
     * Visit a Credential. Credentials do not affect access decisions here.
     *
     * @param credential the credential being visited
     */
    @Override
    public void visitCredential(Credential credential) { }

    /**
     * Visit a Resource. No action is taken for the resource node itself.
     *
     * @param resource the resource being visited
     */
    @Override
    public void visitResource(Resource resource) { }

    /**
     * Visit an AccessToken and evaluate access based on whether the token
     * represents an administrative credential or a regular user credential.
     *
     * @param accessToken the access token being evaluated
     */
    @Override
    public void visitAccessToken(AccessToken accessToken) { 
        if(accessToken.isExpired()) {
            hasAccess = false;
            return;
        }

        if(accessToken.getCredential().isAdmin()) {
            // check user's entitlements (roles/permissions)
            for (Entitlement e : accessToken.getUser().getEntitlements()) {
                if (e.checkAccess(permission)) {
                    hasAccess = true;
                    return;
                }
            }
        } else {
            // check user's resourceRoles
            for (ResourceRole rr : accessToken.getUser().getResourceRoles()) {
                if (rr.checkAccess(permission, resource)) {
                    hasAccess = true;
                    return;
                }
            }
        }
    }
}

