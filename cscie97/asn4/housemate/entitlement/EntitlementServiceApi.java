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
public class EntitlementServiceApi {
    private static final EntitlementServiceApi instance = new EntitlementServiceApi();

    private final Set<AccessToken> accessTokens = new HashSet<>();
    private final Set<User> users = new HashSet<>();
    private final Set<Entitlement> entitlements = new HashSet<>();
    private final Set<ResourceRole> resourceRoles = new HashSet<>();
    private final Set<Resource> resources = new HashSet<>();

    private EntitlementServiceApi() {}

    /**
     * Return the singleton instance of the EntitlementServiceApi.
     *
     * @return the singleton EntitlementServiceApi
     */
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
                try {
                    return login(trimmed);
                } catch (AuthenticationException e) {
                    return "ERROR: " + e.getMessage();
                }
            case "logout":
                return logout(trimmed);
            case "check_access":
            try {
                return checkAccess(trimmed);
            } catch (AccessDeniedException e) {
                return e.getMessage();
            }
            case "inventory_entitlement_service":
                return inventory();
            default:
                return null;
        }
    }

    /**
     * Build and return a textual inventory of the entitlement service.
     * Iterates users, entitlements, resource roles, resources and tokens and
     * collects information via the InventoryVisitor.
     *
     * @return serialized inventory information
     */
    private String inventory() {
        InventoryVisitor inventoryVisitor = new InventoryVisitor();
        for (User u : users) u.accept(inventoryVisitor);
        for (Entitlement e : entitlements) e.accept(inventoryVisitor);
        for (ResourceRole rr : resourceRoles) rr.accept(inventoryVisitor);
        for (Resource r : resources) r.accept(inventoryVisitor);
        for (AccessToken t : accessTokens) t.accept(inventoryVisitor);
        return inventoryVisitor.getInventory();
    }

    /**
     * Handle the define_permission command. Expects: define_permission, id,
     * name, description. Creates and registers a Permission.
     *
     * @param commandText raw command text
     * @return result message
     */
    private String definePermission(String commandText) {
        // format: define_permission, id, name, description
        String[] parts = commandText.split(",");
        if (parts.length < 4) return "ERROR: invalid define_permission";
        String id = parts[1].trim();
        String name = unquote(parts[2].trim());
        String desc = unquote(parts[3].trim());
        Permission newPermission = EntitlementServiceAbstractFactory.getInstance().createPermission(id, name, desc);
        entitlements.add(newPermission);
        return "Created permission " + id;
    }

    /**
     * Handle the define_role command. Expects: define_role, id, name,
     * description. Creates and registers a Role.
     *
     * @param commandText raw command text
     * @return result message
     */
    private String defineRole(String commandText) {
        String[] parts = commandText.split(",");
        if (parts.length < 4) return "ERROR: invalid define_role";
        String id = parts[1].trim();
        String name = unquote(parts[2].trim());
        String desc = unquote(parts[3].trim());
        Role newRole = EntitlementServiceAbstractFactory.getInstance().createRole(id, name, desc);
        entitlements.add(newRole);
        return "Created role " + id;
    }

    /**
     * Add an entitlement to a role. Expects: add_entitlement_to_role,
     * roleId, entitlementId.
     *
     * @param commandText raw command text
     * @return result message
     */
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

    /**
     * Create a new user. Expects: create_user, id, name.
     *
     * @param commandText raw command text
     * @return result message
     */
    private String createUser(String commandText) {
        String[] parts = commandText.split(",");
        if (parts.length < 3) return "ERROR: invalid create_user";
        String id = parts[1].trim();
        String name = unquote(parts[2].trim());
        User newUser = EntitlementServiceAbstractFactory.getInstance().createUser(id, name);
        users.add(newUser);
        return "Created user " + id;
    }

    /**
     * Add a credential to an existing user. Expects: add_user_credential,
     * userId, type, value. Valid credential types are 'password' and
     * 'voice_print' (or 'voiceprint').
     *
     * @param commandText raw command text
     * @return result message
     */
    private String addUserCredential(String commandText) {
        String[] parts = commandText.split(",");
        if (parts.length < 4) return "ERROR: invalid add_user_credential";
        String userId = parts[1].trim();
        String type = parts[2].trim();
        String value = parts[3].trim();
        Optional<User> u = users.stream().filter(x -> x.getId().equals(userId)).findFirst();
        if (!u.isPresent()) return "ERROR: user not found";
        String typeLower = type.toLowerCase();
        if (!"password".equals(typeLower) && !"voice_print".equals(typeLower) && !"voiceprint".equals(typeLower)) {
            return "ERROR: invalid credential type";
        }
        boolean isPassword = "password".equals(typeLower);
        Credential newCredential = EntitlementServiceAbstractFactory.getInstance().createCredential(userId, isPassword, value);
        u.get().addCredential(newCredential);
        return "Added credential for " + userId;
    }

    /**
     * Assign a role to a user. Expects: add_role_to_user, userId, roleId.
     *
     * @param commandText raw command text
     * @return result message
     */
    private String addRoleToUser(String commandText) {
        String[] parts = commandText.split(",");
        if (parts.length < 3) return "ERROR: invalid add_role_to_user";
        String userId = parts[1].trim();
        String roleId = parts[2].trim();
        Optional<User> user = users.stream().filter(x -> x.getId().equals(userId)).findFirst();
        Optional<Entitlement> role = entitlements.stream().filter(e -> e.getId().equals(roleId)).findFirst();
        if (!user.isPresent()) return "ERROR: user not found";
        if (!role.isPresent() || !(role.get() instanceof Role)) return "ERROR: role not found";
        user.get().addEntitlement(role.get());
        return "Added role " + role.get().getName() + " to user " + user.get().getName();
    }

    /**
     * Create a resource role binding. Expects: create_resource_role,
     * name, roleId, resourceName. Resource will be created on demand.
     *
     * @param commandText raw command text
     * @return result message
     */
    private String createResourceRole(String commandText) {
        String[] parts = commandText.split(",");
        if (parts.length < 4) return "ERROR: invalid create_resource_role";
        String name = parts[1].trim();
        String roleId = parts[2].trim();
        String resourceName = parts[3].trim();
        Optional<Role> role = entitlements.stream().filter(e -> e instanceof Role && e.getId().equals(roleId)).map(e -> (Role)e).findFirst();
        Resource resource = resources.stream().filter(x -> x.getName().equals(resourceName)).findFirst().orElseGet(() -> {
            Resource newResource = EntitlementServiceAbstractFactory.getInstance().createResource(resourceName);
            resources.add(newResource);
            return newResource;
        });
        if (!role.isPresent()) return "ERROR: role not found";
        ResourceRole newResourceRole = EntitlementServiceAbstractFactory.getInstance().createResourceRole(name, role.get(), resource);
        resourceRoles.add(newResourceRole);
        return "Created resource role " + name;
    }

    /**
     * Assign a resource role to a user. Expects: add_resource_role_to_user,
     * userId, resourceRoleName.
     *
     * @param commandText raw command text
     * @return result message
     */
    private String addResourceRoleToUser(String commandText) {
        String[] parts = commandText.split(",");
        if (parts.length < 3) return "ERROR: invalid add_resource_role_to_user";
        String userId = parts[1].trim();
        String resourceRoleName = parts[2].trim();
        Optional<User> user = users.stream().filter(x -> x.getId().equals(userId)).findFirst();
        Optional<ResourceRole> resourceRole = resourceRoles.stream().filter(x -> x.getName().equals(resourceRoleName)).findFirst();
        if (!user.isPresent()) return "ERROR: user not found";
        if (!resourceRole.isPresent()) return "ERROR: resource role not found";
        user.get().addResourceRole(resourceRole.get());
        return "Added resource role to user";
    }

    /**
     * Authenticate a user and produce an access token. Supports two formats:
     * password login: "login user <userId>, password <password>" and
     * voiceprint login: "login voiceprint <voiceprint>".
     *
     * @param commandText raw command text
     * @return token string on success
     * @throws AuthenticationException when authentication fails
     */
    private String login(String commandText) throws AuthenticationException {
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

            Optional<User> user = users.stream().filter(x -> x.getId().equals(userId)).findFirst();
            if (!user.isPresent()) throw new AuthenticationException(userId);

            for (Credential credential : user.get().getCredentials()) {
                if (credential.isPassword() && credential.isMatch(password)) {
                    AccessToken t = EntitlementServiceAbstractFactory.getInstance().createAccessToken(user.get(), credential);
                    accessTokens.add(t);
                    return new String(t.getToken());
                }
            }

            throw new AuthenticationException(userId);
        }

        // voiceprint login: login voiceprint <voiceprint>

        if (commandText.contains("voiceprint")) {
            String[] parts = commandText.split("\\s+");

            String voice = parts[parts.length-1].trim();

            for (User user : users) {
                for (Credential credential : user.getCredentials()) {
                    if (!credential.isPassword() && credential.isMatch(voice)) {
                        AccessToken t = EntitlementServiceAbstractFactory.getInstance().createAccessToken(user, credential);
                        accessTokens.add(t);
                        return new String(t.getToken());
                    }
                }
            }

            throw new AuthenticationException("unknown");
        }

        return "ERROR: invalid login format";
    }

    /**
     * Invalidate an access token provided in the command text.
     *
     * @param commandText raw command text containing the token
     * @return result message
     */
    private String logout(String commandText) {
        String[] parts = commandText.split("\\s+");
        if (parts.length < 2) return "ERROR: invalid logout";
        String tokenStr = parts[1].trim();
        Optional<AccessToken> t = accessTokens.stream().filter(x -> new String(x.getToken()).equals(tokenStr)).findFirst();
        if (!t.isPresent()) return "ERROR: token not found";
        accessTokens.remove(t.get());
        return "Logged out";
    }

    /**
     * Check whether the provided auth token grants the named permission on
     * the named resource. Returns "Access Granted" on success or throws an
     * AccessDeniedException on failure.
     *
     * @param commandText raw command text
     * @return "Access Granted" when access is allowed
     * @throws AccessDeniedException when access is denied
     */
    private String checkAccess(String commandText) throws AccessDeniedException {
        // check_access <auth_token>, <permission>, <resource>
        String[] parts = commandText.split(",");
        if (parts.length < 3) return "ERROR: invalid check_access";
        String tokenStr = parts[0].substring(parts[0].indexOf(" ")+1).trim();
        String permissionId = parts[1].trim();
        String resourceName = parts[2].trim();
        Optional<AccessToken> accessToken = accessTokens.stream().filter(x -> new String(x.getToken()).equals(tokenStr)).findFirst();
        if (!accessToken.isPresent()) throw new AccessDeniedException(permissionId, resourceName);
        AccessToken token = accessToken.get();
        // find permission
        Optional<Permission> permission = entitlements.stream().filter(e -> e instanceof Permission && e.getId().equals(permissionId)).map(e -> (Permission)e).findFirst();
        if (!permission.isPresent()) return "Access Denied: permission not found";
        Resource resource = resources.stream().filter(r -> r.getName().equals(resourceName)).findFirst().orElseGet(() -> {
            Resource rNew = EntitlementServiceAbstractFactory.getInstance().createResource(resourceName);
            resources.add(rNew);
            return rNew;
        });

        CheckAccessVisitor checkAccessVisitor = new CheckAccessVisitor(permission.get(), resource);
        token.accept(checkAccessVisitor);
        if (checkAccessVisitor.hasAccess()) {
            token.touch();
            return "Access Granted";
        }
        throw new AccessDeniedException(permissionId, resourceName);
    }

    /**
     * Remove surrounding single or double quotes from a string if present.
     */
    private static String unquote(String s) {
        if (s == null || s.length() < 2) return s;
        char first = s.charAt(0);
        char last = s.charAt(s.length() - 1);
        if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }
}
