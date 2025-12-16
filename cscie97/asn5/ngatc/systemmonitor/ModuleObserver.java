package cscie97.asn4.asn5.ngatc.systemmonitor;

import cscie97.asn4.asn5.ngatc.common.Status;

/**
 * Observer interface for modules being tracked by the System Monitor.
 * Part of the Observer design pattern implementation.
 */
public interface ModuleObserver {
    
    /**
     * Called when a status update is received for a module.
     * 
     * @param id The ID of the module
     * @param moduleStatus The new status
     * @return true if this observer matches the provided ID
     */
    boolean onStatusUpdate(String id, Status moduleStatus);
    
    /**
     * Called when a module is removed from tracking.
     * 
     * @param id The ID of the module to remove
     * @return true if this observer matches the provided ID
     */
    boolean onModuleRemoval(String id);
}
