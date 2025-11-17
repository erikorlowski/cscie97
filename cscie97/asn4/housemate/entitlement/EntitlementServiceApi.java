package cscie97.asn4.housemate.entitlement;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Top-level API for the Housemate Entitlement Service. This singleton class
 * accepts textual commands via executeCommand and maintains in-memory sets of
 * users, entitlements, resource roles and tokens. The implementation is
 * intentionally simple and intended for use in the assignment and tests.
 */
public class EntitlementServiceApi implements Visitable {
    private static final EntitlementServiceApi instance = new EntitlementServiceApi();

    private final Set<AccessToken> accessTokens = new HashSet<>();
    private final Set<User> users = new HashSet<>();
    private final Set<Entitlement> entitlements = new HashSet<>();
    private final Set<ResourceRole> resourceRoles = new HashSet<>();
    private final Set<Resource> resources = new HashSet<>();

    private EntitlementServiceApi() {}

    public static EntitlementServiceApi getInstance() { return instance; }

    /**
     * Execute a textual command against the Entitlement Service. Supported
     * commands include define_permission, define_role, add_entitlement_to_role,
     * create_user, add_user_credential, add_role_to_user, create_resource_role,
     * add_resource_role_to_user, login, logout, check_access and
     * inventory_entitlement_service. This method parses the primary command
     * token and delegates to the appropriate private handler.
     *
     * @param commandText the full command text
     * @return the textual result or null for unrecognized commands
     */
    public String executeCommand(String commandText) {
        if (commandText == null) return null;
        String trimmed = commandText.trim();
        if (trimmed.isEmpty()) return null;

        // extract primary command token (before first space or comma)
        String lower = trimmed.toLowerCase();
        String primary;
        int commaIdx = lower.indexOf(',');
        int spaceIdx = lower.indexOf(' ');
        int endIdx = -1;
        if (commaIdx == -1 && spaceIdx == -1) {
            endIdx = lower.length();
        } else if (commaIdx == -1) {
            endIdx = spaceIdx;
        } else if (spaceIdx == -1) {
            endIdx = commaIdx;
        } else {
            endIdx = Math.min(commaIdx, spaceIdx);
        }
        primary = lower.substring(0, endIdx).trim();

        switch (primary) {
            case "define_permission":
                return definePermission(trimmed);
            case "define_role":
                return defineRole(trimmed);
            case "add_entitlement_to_role":
                return addEntitlementToRole(trimmed);
            case "create_user":
                return createUser(trimmed);
            case "add_user_credential":
                return addUserCredential(trimmed);
            case "add_role_to_user":
                return addRoleToUser(trimmed);
            case "create_resource_role":
                return createResourceRole(trimmed);
            case "add_resource_role_to_user":
                return addResourceRoleToUser(trimmed);
            case "login":
                return login(trimmed);
            case "logout":
                return logout(trimmed);
            case "check_access":
                return checkAccess(trimmed);
            case "inventory_entitlement_service":
                return inventory();
            default:
                return null;
        }
    }

    private String inventory() {
        InventoryVisitor v = new InventoryVisitor();
        for (User u : users) u.accept(v);
        for (Entitlement e : entitlements) e.accept(v);
        for (ResourceRole rr : resourceRoles) rr.accept(v);
        for (Resource r : resources) r.accept(v);
        for (AccessToken t : accessTokens) t.accept(v);
        return v.getInventory();
    }

    private String definePermission(String commandText) {
        // format: define_permission, id, name, description
        String[] parts = commandText.split(",");
        if (parts.length < 4) return "ERROR: invalid define_permission";
        String id = parts[1].trim();
        String name = parts[2].trim();
        String desc = parts[3].trim();
        Permission p = EntitlementServiceAbstractFactory.getInstance().createPermission(id, name, desc);
        entitlements.add(p);
        return "Created permission " + id;
    }

    private String defineRole(String commandText) {
        String[] parts = commandText.split(",");
        if (parts.length < 4) return "ERROR: invalid define_role";
        String id = parts[1].trim();
        String name = parts[2].trim();
        String desc = parts[3].trim();
        Role r = EntitlementServiceAbstractFactory.getInstance().createRole(id, name, desc);
        entitlements.add(r);
        return "Created role " + id;
    }

    private String addEntitlementToRole(String commandText) {
        String[] parts = commandText.split(",");
        if (parts.length < 3) return "ERROR: invalid add_entitlement_to_role";
        String roleId = parts[1].trim();
        String entId = parts[2].trim();
        Optional<Role> role = entitlements.stream().filter(e -> e instanceof Role && e.getId().equals(roleId)).map(e -> (Role)e).findFirst();
        Optional<Entitlement> ent = entitlements.stream().filter(e -> e.getId().equals(entId)).findFirst();
        if (!role.isPresent()) return "ERROR: role not found";
        if (!ent.isPresent()) return "ERROR: entitlement not found";
        role.get().addChild(ent.get());
        return "Added entitlement to role";
    }

    private String createUser(String commandText) {
        String[] parts = commandText.split(",");
        if (parts.length < 3) return "ERROR: invalid create_user";
        String id = parts[1].trim();
        String name = parts[2].trim();
        User u = EntitlementServiceAbstractFactory.getInstance().createUser(id, name);
        users.add(u);
        return "Created user " + id;
    }

    private String addUserCredential(String commandText) {
        String[] parts = commandText.split(",");
        if (parts.length < 4) return "ERROR: invalid add_user_credential";
        String userId = parts[1].trim();
        String type = parts[2].trim();
        String value = parts[3].trim();
        Optional<User> u = users.stream().filter(x -> x.getId().equals(userId)).findFirst();
        if (!u.isPresent()) return "ERROR: user not found";
        boolean isPassword = "password".equalsIgnoreCase(type);
        Credential c = EntitlementServiceAbstractFactory.getInstance().createCredential(userId, isPassword, value);
        u.get().addCredential(c);
        return "Added credential for " + userId;
    }

    private String addRoleToUser(String commandText) {
        String[] parts = commandText.split(",");
        if (parts.length < 3) return "ERROR: invalid add_role_to_user";
        String userId = parts[1].trim();
        String roleId = parts[2].trim();
        Optional<User> u = users.stream().filter(x -> x.getId().equals(userId)).findFirst();
        Optional<Entitlement> r = entitlements.stream().filter(e -> e.getId().equals(roleId)).findFirst();
        if (!u.isPresent()) return "ERROR: user not found";
        if (!r.isPresent() || !(r.get() instanceof Role)) return "ERROR: role not found";
        u.get().addEntitlement(r.get());
        return "Added role to user";
    }

    private String createResourceRole(String commandText) {
        String[] parts = commandText.split(",");
        if (parts.length < 4) return "ERROR: invalid create_resource_role";
        String name = parts[1].trim();
        String roleId = parts[2].trim();
        String resourceName = parts[3].trim();
        Optional<Role> r = entitlements.stream().filter(e -> e instanceof Role && e.getId().equals(roleId)).map(e -> (Role)e).findFirst();
        Resource resource = resources.stream().filter(x -> x.getName().equals(resourceName)).findFirst().orElseGet(() -> {
            Resource res = EntitlementServiceAbstractFactory.getInstance().createResource(resourceName);
            resources.add(res);
            return res;
        });
        if (!r.isPresent()) return "ERROR: role not found";
        ResourceRole rr = EntitlementServiceAbstractFactory.getInstance().createResourceRole(name, r.get(), resource);
        resourceRoles.add(rr);
        return "Created resource role " + name;
    }

    private String addResourceRoleToUser(String commandText) {
        String[] parts = commandText.split(",");
        if (parts.length < 3) return "ERROR: invalid add_resource_role_to_user";
        String userId = parts[1].trim();
        String rrName = parts[2].trim();
        Optional<User> u = users.stream().filter(x -> x.getId().equals(userId)).findFirst();
        Optional<ResourceRole> rr = resourceRoles.stream().filter(x -> x.getName().equals(rrName)).findFirst();
        if (!u.isPresent()) return "ERROR: user not found";
        if (!rr.isPresent()) return "ERROR: resource role not found";
        u.get().addResourceRole(rr.get());
        return "Added resource role to user";
    }

    private String login(String commandText) {
        // supports: login user <userId>, password <password>
        if (commandText.contains("password")) {
            String[] parts = commandText.split(",");
            // parts: "login user <id>", " password <pw>"
            if (parts.length < 2) return "ERROR: invalid login";
            String left = parts[0];
            String[] leftParts = left.split("\\s+");
            if (leftParts.length < 3) return "ERROR: invalid login";
            String userId = leftParts[2].trim();
            String pwPart = parts[1];
            String[] rightParts = pwPart.split("\\s+");
            String password = rightParts[rightParts.length-1].trim();
            Optional<User> u = users.stream().filter(x -> x.getId().equals(userId)).findFirst();
            if (!u.isPresent()) return "ERROR: user not found";
            for (Credential c : u.get().getCredentials()) {
                if (c.isPassword() && c.isMatch(password)) {
                    AccessToken t = EntitlementServiceAbstractFactory.getInstance().createAccessToken(u.get(), c);
                    accessTokens.add(t);
                    return new String(t.getToken());
                }
            }
            return "ERROR: authentication failed";
        }
        // voiceprint login: login voiceprint <voiceprint>
        if (commandText.contains("voiceprint")) {
            String[] parts = commandText.split("\\s+");
            String voice = parts[parts.length-1].trim();
            for (User u : users) {
                for (Credential c : u.getCredentials()) {
                    if (!c.isPassword() && c.isMatch(voice)) {
                        AccessToken t = EntitlementServiceAbstractFactory.getInstance().createAccessToken(u, c);
                        accessTokens.add(t);
                        return new String(t.getToken());
                    }
                }
            }
            return "ERROR: authentication failed";
        }
        return "ERROR: invalid login format";
    }

    private String logout(String commandText) {
        String[] parts = commandText.split("\\s+");
        if (parts.length < 2) return "ERROR: invalid logout";
        String tokenStr = parts[1].trim();
        Optional<AccessToken> t = accessTokens.stream().filter(x -> new String(x.getToken()).equals(tokenStr)).findFirst();
        if (!t.isPresent()) return "ERROR: token not found";
        accessTokens.remove(t.get());
        return "Logged out";
    }

    private String checkAccess(String commandText) {
        // check_access <auth_token>, <permission>, <resource>
        String[] parts = commandText.split(",");
        if (parts.length < 3) return "ERROR: invalid check_access";
        String tokenStr = parts[0].substring(parts[0].indexOf(" ")+1).trim();
        String permissionId = parts[1].trim();
        String resourceName = parts[2].trim();
        Optional<AccessToken> t = accessTokens.stream().filter(x -> new String(x.getToken()).equals(tokenStr)).findFirst();
        if (!t.isPresent()) return "Access Denied: invalid token";
        AccessToken token = t.get();
        // find permission
        Optional<Permission> p = entitlements.stream().filter(e -> e instanceof Permission && e.getId().equals(permissionId)).map(e -> (Permission)e).findFirst();
        if (!p.isPresent()) return "Access Denied: permission not found";
        Resource resource = resources.stream().filter(r -> r.getName().equals(resourceName)).findFirst().orElseGet(() -> {
            Resource rNew = EntitlementServiceAbstractFactory.getInstance().createResource(resourceName);
            resources.add(rNew);
            return rNew;
        });

        User user = token.getUser();
        CheckAccessVisitor v = new CheckAccessVisitor(p.get(), resource);
        user.accept(v);
        if (v.hasAccess() || token.isAdmin()) {
            token.touch();
            return "Access Granted";
        }
        return "Access Denied";
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitAccessToken(null); // not meaningful here
    }
}
