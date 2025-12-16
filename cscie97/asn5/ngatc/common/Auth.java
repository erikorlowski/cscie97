package cscie97.asn5.ngatc.common;

import cscie97.asn4.housemate.entitlement.EntitlementServiceApi;
import cscie97.asn4.housemate.entitlement.InvalidAccessTokenException;
import cscie97.asn4.housemate.entitlement.AccessDeniedException;

/**
 * Authentication utility for NGATC system.
 * Provides centralized access to the Entitlement Service for authentication and authorization.
 */
public class Auth {
    private static final EntitlementServiceApi entitlementService = EntitlementServiceApi.getInstance();

    /**
     * Validates an access token and required permission.
     * 
     * @param accessToken The access token to validate
     * @param permission The permission required
     * @param resource The resource being accessed
     * @return true if access is granted
     * @throws InvalidAccessTokenException if token is invalid
     * @throws AccessDeniedException if access is denied
     */
    public static boolean checkAccess(String accessToken, String permission, String resource) 
            throws InvalidAccessTokenException, AccessDeniedException {
        try {
            // Use entitlement service to check access
            String command = String.format("check_access %s, %s, %s", accessToken, permission, resource);
            entitlementService.executeCommand(command);
            return true;
        } catch (Exception e) {
            if (e instanceof InvalidAccessTokenException) {
                throw (InvalidAccessTokenException) e;
            } else if (e instanceof AccessDeniedException) {
                throw (AccessDeniedException) e;
            }
            throw new RuntimeException("Authentication error: " + e.getMessage(), e);
        }
    }

    /**
     * Authenticates an admin user and returns an access token.
     * 
     * @param userId The user ID
     * @param password The password
     * @return The access token string
     */
    public static String loginAdmin(String userId, String password) {
        try {
            String command = String.format("login user %s, password %s", userId, password);
            String result = entitlementService.executeCommand(command);
            // Extract token from result
            return extractToken(result);
        } catch (Exception e) {
            throw new RuntimeException("Login failed: " + e.getMessage(), e);
        }
    }

    /**
     * Extracts the access token from the entitlement service response.
     */
    private static String extractToken(String response) {
        if (response != null && response.contains("token")) {
            // Simple extraction - in real implementation would parse more carefully
            return response;
        }
        return response;
    }

    /**
     * Validates that the access token has administrator privileges.
     */
    public static boolean isAdmin(String accessToken) {
        try {
            return checkAccess(accessToken, "admin", "system");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validates that the access token has controller privileges.
     */
    public static boolean isController(String accessToken) {
        try {
            return checkAccess(accessToken, "controller", "system");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validates that the access token has internal module privileges.
     */
    public static boolean isInternalModule(String accessToken) {
        try {
            return checkAccess(accessToken, "internal_module", "system");
        } catch (Exception e) {
            return false;
        }
    }
}
