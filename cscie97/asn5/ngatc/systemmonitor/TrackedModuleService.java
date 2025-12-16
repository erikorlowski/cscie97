package cscie97.asn5.ngatc.systemmonitor;

import cscie97.asn5.ngatc.common.Status;
import cscie97.asn5.ngatc.common.Auth;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for TrackedModule operations.
 * Handles business logic and coordinates between controller and data access.
 */
public class TrackedModuleService {
    private final ModuleSubject moduleSubject;

    public TrackedModuleService() {
        this.moduleSubject = ModuleSubject.getInstance();
    }

    /**
     * Adds a new module to track.
     * 
     * @param newModule The module to track
     * @param accessToken Authentication token
     * @return true if module was added successfully
     */
    public boolean receiveNewModule(TrackedModule newModule, String accessToken) {
        try {
            // Validate access
            Auth.checkAccess(accessToken, "internal_module", "system");
            
            // Register as observer
            moduleSubject.registerModuleObserver(newModule);
            
            System.out.println("Registered new module: " + newModule.getId());
            return true;
        } catch (Exception e) {
            System.err.println("Error registering module: " + e.getMessage());
            throw new DatabaseException("Failed to register module: " + e.getMessage());
        }
    }

    /**
     * Processes a module status update.
     * 
     * @param newStatus The new status
     * @param module The module being updated
     * @param accessToken Authentication token
     * @return true if module was found and updated
     */
    public boolean receiveStatusUpdate(Status newStatus, TrackedModule module, String accessToken) {
        try {
            // Validate access
            Auth.checkAccess(accessToken, "internal_module", "system");
            
            // Notify observers of status update
            boolean found = moduleSubject.notifyOfStatusUpdate(module.getId(), newStatus);
            
            if (found) {
                System.out.println("Updated module " + module.getId() + " to status " + newStatus);
            }
            
            return found;
        } catch (Exception e) {
            System.err.println("Error updating module status: " + e.getMessage());
            throw new DatabaseException("Failed to update module status: " + e.getMessage());
        }
    }

    /**
     * Removes a module from tracking.
     * 
     * @param module The module to remove
     * @param accessToken Authentication token
     * @return true if module was found and removed
     */
    public boolean receiveModuleRemoval(TrackedModule module, String accessToken) {
        try {
            // Validate access
            Auth.checkAccess(accessToken, "internal_module", "system");
            
            // Notify observers of removal
            boolean found = moduleSubject.notifyOfModuleRemoval(module.getId());
            
            if (found) {
                System.out.println("Removed module: " + module.getId());
            }
            
            return found;
        } catch (Exception e) {
            System.err.println("Error removing module: " + e.getMessage());
            throw new DatabaseException("Failed to remove module: " + e.getMessage());
        }
    }

    /**
     * Returns status of all tracked modules.
     * 
     * @param accessToken Authentication token
     * @return List of all tracked modules
     */
    public List<TrackedModule> readModuleStatuses(String accessToken) {
        try {
            // Validate access - any authenticated user can read statuses
            Auth.checkAccess(accessToken, "internal_module", "system");
            
            // Get all observers that are TrackedModules
            return moduleSubject.getObservers().stream()
                .filter(observer -> observer instanceof TrackedModule)
                .map(observer -> (TrackedModule) observer)
                .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error reading module statuses: " + e.getMessage());
            throw new DatabaseException("Failed to read module statuses: " + e.getMessage());
        }
    }
}
