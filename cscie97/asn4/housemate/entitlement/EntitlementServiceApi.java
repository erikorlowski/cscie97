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

    private AccessToken currentAccessToken;

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
    public String executeCommand(String commandText) throws EntitlementException {
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

    /**
     * Gets the most recently created access token as a numeric long.
     * Returns 0 if no token currently exists.
     *
     * @return the token as long, or 0 when none available
     */
    public long getCurrentAccessToken() {
        if (currentAccessToken == null) return 0L;
        return currentAccessToken.getToken();
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
    private String definePermission(String commandText) throws EntitlementException {
        // format: define_permission, id, name, description
        String[] parts = commandText.split(",");
        if (parts.length < 4) throw new EntitlementException("invalid define_permission");
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
    private String defineRole(String commandText) throws EntitlementException {
        String[] parts = commandText.split(",");
        if (parts.length < 4) throw new EntitlementException("invalid define_role");
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
    private String addEntitlementToRole(String commandText) throws EntitlementException {
        String[] parts = commandText.split(",");
        if (parts.length < 3) throw new EntitlementException("invalid add_entitlement_to_role");
        String roleId = parts[1].trim();
        String entId = parts[2].trim();
        Optional<Role> role = entitlements.stream().filter(e -> e instanceof Role && e.getId().equals(roleId)).map(e -> (Role)e).findFirst();
        Optional<Entitlement> ent = entitlements.stream().filter(e -> e.getId().equals(entId)).findFirst();
        if (!role.isPresent()) throw new EntitlementException("role not found");
        if (!ent.isPresent()) throw new EntitlementException("entitlement not found");
        role.get().addChild(ent.get());
        return "Added entitlement to role";
    }

    /**
     * Create a new user. Expects: create_user, id, name.
     *
     * @param commandText raw command text
     * @return result message
     */
    private String createUser(String commandText) throws EntitlementException {
        // Expected format: create_user user_id, user_name
        String[] parts = commandText.split(",", 2);
        if (parts.length < 2) throw new EntitlementException("invalid create_user");

        // left side: "create_user user_id" -> extract the id token
        String left = parts[0].trim();
        String[] leftParts = left.split("\\s+");
        if (leftParts.length < 2) throw new EntitlementException("invalid create_user");
        String id = leftParts[1].trim();

        // right side: the user name (may be quoted and may contain spaces)
        String name = unquote(parts[1].trim());
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
    private String addUserCredential(String commandText) throws EntitlementException {
        // Expected format: add_user_credential user_id, type, value
        String[] parts = commandText.split(",", 3);
        if (parts.length < 3) throw new EntitlementException("invalid add_user_credential");

        // left side: "add_user_credential user_id" -> extract the id token
        String left = parts[0].trim();
        String[] leftParts = left.split("\\s+");
        if (leftParts.length < 2) throw new EntitlementException("invalid add_user_credential");
        String userId = leftParts[1].trim();

        String type = parts[1].trim();
        String value = unquote(parts[2].trim());
        Optional<User> u = users.stream().filter(x -> x.getId().equals(userId)).findFirst();
        if (!u.isPresent()) throw new EntitlementException("user not found");
        String typeLower = type.toLowerCase();
        if (!"password".equals(typeLower) && !"voice_print".equals(typeLower) && !"voiceprint".equals(typeLower)) {
            throw new EntitlementException("invalid credential type");
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
    private String addRoleToUser(String commandText) throws EntitlementException {
        // Expected format: add_role_to_user user_id, roleId
        String[] parts = commandText.split(",", 2);
        if (parts.length < 2) throw new EntitlementException("invalid add_role_to_user");

        String left = parts[0].trim();
        String[] leftParts = left.split("\\s+");
        if (leftParts.length < 2) throw new EntitlementException("invalid add_role_to_user");
        String userId = leftParts[1].trim();

        String roleId = parts[1].trim();
        Optional<User> user = users.stream().filter(x -> x.getId().equals(userId)).findFirst();
        Optional<Entitlement> role = entitlements.stream().filter(e -> e.getId().equals(roleId)).findFirst();
        if (!user.isPresent()) throw new EntitlementException("user not found");
        if (!role.isPresent() || !(role.get() instanceof Role)) throw new EntitlementException("role not found");
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
    private String createResourceRole(String commandText) throws EntitlementException {
        // Expected format: create_resource_role <name>, <roleId>, <resourceName>
        String[] parts = commandText.split(",", 3);
        if (parts.length < 3) throw new EntitlementException("invalid create_resource_role");

        // left side: "create_resource_role <name>" -> extract the name token
        String left = parts[0].trim();
        String[] leftParts = left.split("\\s+", 2);
        if (leftParts.length < 2) throw new EntitlementException("invalid create_resource_role");
        String name = unquote(leftParts[1].trim());

        String roleId = parts[1].trim();
        String resourceName = unquote(parts[2].trim());
        Optional<Role> role = entitlements.stream().filter(e -> e instanceof Role && e.getId().equals(roleId)).map(e -> (Role)e).findFirst();
        Resource resource = resources.stream().filter(x -> x.getName().equals(resourceName)).findFirst().orElseGet(() -> {
            Resource newResource = EntitlementServiceAbstractFactory.getInstance().createResource(resourceName);
            resources.add(newResource);
            return newResource;
        });
        if (!role.isPresent()) throw new EntitlementException("role " + roleId + " not found");
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
    private String addResourceRoleToUser(String commandText) throws EntitlementException {
        // Expected format: add_resource_role_to_user <user_id>, <resource_role>
        String[] parts = commandText.split(",", 2);
        if (parts.length < 2) throw new EntitlementException("invalid add_resource_role_to_user");

        // left side: "add_resource_role_to_user <user_id>" -> extract the id token
        String left = parts[0].trim();
        String[] leftParts = left.split("\\s+");
        if (leftParts.length < 2) throw new EntitlementException("invalid add_resource_role_to_user");
        String userId = leftParts[1].trim();

        // right side: resource role name (may be quoted)
        String resourceRoleName = unquote(parts[1].trim());

        Optional<User> user = users.stream().filter(x -> x.getId().equals(userId)).findFirst();
        Optional<ResourceRole> resourceRole = resourceRoles.stream().filter(x -> x.getName().equals(resourceRoleName)).findFirst();
        if (!user.isPresent()) throw new EntitlementException("user not found");
        if (!resourceRole.isPresent()) throw new EntitlementException("resource role not found");
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
    private String login(String commandText) throws AuthenticationException, EntitlementException {
        // supports: login user <userId>, password <password>

        if (commandText.contains("password")) {
            // Build sign-in text: everything after the 'login' token
            int firstSpace = commandText.indexOf(' ');
            if (firstSpace == -1) throw new EntitlementException("invalid login");
            String signInText = commandText.substring(firstSpace + 1).trim();

            // parts: "user <id>", ... we still need the user id to lookup the user
            String[] leftParts = signInText.split("\\s+");
            if (leftParts.length < 2) throw new EntitlementException("invalid login");
            String userId = leftParts[1].trim();
            // strip trailing comma if present (e.g., "controller,") without mutating the id variable used in lambdas
            final String parsedUserId = userId.endsWith(",") ? userId.substring(0, userId.length() - 1).trim() : userId;

            Optional<User> user = users.stream().filter(x -> x.getId().equals(parsedUserId)).findFirst();
            if (!user.isPresent()) throw new AuthenticationException(userId, "User not found");

            for (Credential credential : user.get().getCredentials()) {
                if (credential.isPassword() && credential.isMatch(signInText)) {
                    AccessToken newToken = EntitlementServiceAbstractFactory.getInstance().createAccessToken(user.get(), credential);
                    accessTokens.add(newToken);
                    currentAccessToken = newToken;
                    return Long.toString(newToken.getToken());
                }
            }

            throw new AuthenticationException(userId, "Admin Credential not found");
        }

        // voiceprint login: login voiceprint <voiceprint>

        if (commandText.contains("voiceprint")) {
            int firstSpace = commandText.indexOf(' ');
            if (firstSpace == -1) throw new EntitlementException("invalid login");
            String signInText = commandText.substring(firstSpace + 1).trim();

            for (User user : users) {
                for (Credential credential : user.getCredentials()) {
                    if (!credential.isPassword() && credential.isMatch(signInText)) {
                        AccessToken newToken = EntitlementServiceAbstractFactory.getInstance().createAccessToken(user, credential);
                        accessTokens.add(newToken);
                        currentAccessToken = newToken;
                        return Long.toString(newToken.getToken());
                    }
                }
            }

            throw new AuthenticationException("unknown", "Voiceprint Credential not found");
        }

        throw new EntitlementException("invalid login format");
    }

    /**
     * Invalidate an access token provided in the command text.
     *
     * @param commandText raw command text containing the token
     * @return result message
     */
    private String logout(String commandText) throws EntitlementException {
        String[] parts = commandText.split("\\s+");
        if (parts.length < 2) throw new EntitlementException("invalid logout");
        String tokenStr = parts[1].trim();
        long tokenLong;
        try {
            tokenLong = Long.parseLong(tokenStr);
        } catch (NumberFormatException e) {
            throw new EntitlementException("invalid token");
        }
        Optional<AccessToken> t = accessTokens.stream().filter(x -> x.getToken() == tokenLong).findFirst();
        if (!t.isPresent()) throw new EntitlementException("token not found");
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
    private String checkAccess(String commandText) throws AccessDeniedException, EntitlementException {
        // check_access <auth_token>, <permission>, <resource>
        String[] parts = commandText.split(",");
        if (parts.length < 3) throw new EntitlementException("invalid check_access");
        String tokenStr = parts[0].substring(parts[0].indexOf(" ")+1).trim();
        String permissionId = parts[1].trim();
        String resourceName = parts[2].trim();
        long tokenLong;
        try {
            tokenLong = Long.parseLong(tokenStr);
        } catch (NumberFormatException e) {
            throw new EntitlementException("invalid token");
        }
        Optional<AccessToken> accessToken = accessTokens.stream().filter(x -> x.getToken() == tokenLong).findFirst();
        if (!accessToken.isPresent()) throw new AccessDeniedException(permissionId, resourceName, "token not found");
        AccessToken token = accessToken.get();
        // find permission
        Optional<Permission> permission = entitlements.stream().filter(e -> e instanceof Permission && e.getId().equals(permissionId)).map(e -> (Permission)e).findFirst();
        if (!permission.isPresent()) throw new EntitlementException("permission " + permissionId + " not found");
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
        throw new AccessDeniedException(permissionId, resourceName, "permission not granted");
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
