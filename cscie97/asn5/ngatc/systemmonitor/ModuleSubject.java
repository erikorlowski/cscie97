package cscie97.asn4.asn5.ngatc.systemmonitor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cscie97.asn4.asn5.ngatc.common.Status;

/**
 * Subject in the Observer pattern for module status tracking.
 * Implements Singleton pattern to ensure single point of module status management.
 * Manages registration of observers and notification of status changes.
 */
public class ModuleSubject {
    private static ModuleSubject instance;
    private final List<ModuleObserver> observers;

    private ModuleSubject() {
        this.observers = new CopyOnWriteArrayList<>();
    }

    /**
     * Gets the singleton instance of ModuleSubject.
     */
    public static synchronized ModuleSubject getInstance() {
        if (instance == null) {
            instance = new ModuleSubject();
        }
        return instance;
    }

    /**
     * Registers a new observer to receive status updates.
     */
    public void registerModuleObserver(ModuleObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /**
     * Unregisters an observer from receiving status updates.
     */
    public void unregisterModuleObserver(ModuleObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notifies all observers of a status update.
     * 
     * @param id The module ID
     * @param moduleStatus The new status
     * @return true if a matching observer was found
     */
    public boolean notifyOfStatusUpdate(String id, Status moduleStatus) {
        boolean found = false;
        for (ModuleObserver observer : observers) {
            if (observer.onStatusUpdate(id, moduleStatus)) {
                found = true;
            }
        }
        return found;
    }

    /**
     * Notifies all observers that a module has been removed.
     * 
     * @param id The module ID
     * @return true if a matching observer was found
     */
    public boolean notifyOfModuleRemoval(String id) {
        boolean found = false;
        for (ModuleObserver observer : observers) {
            if (observer.onModuleRemoval(id)) {
                found = true;
            }
        }
        return found;
    }

    /**
     * Rehydrates observers from persistent storage on startup.
     * This method would typically load TrackedModule instances from database.
     */
    public void rehydrate() {
        System.out.println("Rehydrating module observers from database...");
        // In a real implementation, would query database and create TrackedModule instances
    }

    /**
     * Gets all registered observers.
     */
    public List<ModuleObserver> getObservers() {
        return new ArrayList<>(observers);
    }
}
