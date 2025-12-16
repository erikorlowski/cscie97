package cscie97.asn5.ngatc.systemmonitor;

import cscie97.asn5.ngatc.common.LogEvent;
import cscie97.asn5.ngatc.common.Severity;
import cscie97.asn5.ngatc.common.Status;
import java.time.Instant;

/**
 * Main service class for the System Monitor module.
 * Provides a facade for all System Monitor operations.
 * Implements Singleton pattern.
 */
public class SystemMonitorService {
    private static SystemMonitorService instance;
    private final TrackedModuleService moduleService;
    private final LogEventService logEventService;
    private final ModuleSubject moduleSubject;

    private SystemMonitorService() {
        this.moduleService = new TrackedModuleService();
        this.logEventService = new LogEventService();
        this.moduleSubject = ModuleSubject.getInstance();
        initialize();
    }

    /**
     * Gets the singleton instance.
     */
    public static synchronized SystemMonitorService getInstance() {
        if (instance == null) {
            instance = new SystemMonitorService();
        }
        return instance;
    }

    /**
     * Initializes the System Monitor service.
     */
    private void initialize() {
        // Rehydrate any persisted module observers
        moduleSubject.rehydrate();
        
        System.out.println("System Monitor Service initialized at " + Instant.now());
    }

    /**
     * Registers a new module for monitoring.
     */
    public boolean registerModule(String moduleId, Status initialStatus, String accessToken) {
        TrackedModule module = new TrackedModule(moduleId, initialStatus);
        return moduleService.receiveNewModule(module, accessToken);
    }

    /**
     * Updates the status of a tracked module.
     */
    public boolean updateModuleStatus(String moduleId, Status newStatus, String accessToken) {
        TrackedModule module = new TrackedModule();
        module.setId(moduleId);
        return moduleService.receiveStatusUpdate(newStatus, module, accessToken);
    }

    /**
     * Removes a module from tracking.
     */
    public boolean removeModule(String moduleId, String accessToken) {
        TrackedModule module = new TrackedModule();
        module.setId(moduleId);
        return moduleService.receiveModuleRemoval(module, accessToken);
    }

    /**
     * Gets the status of all tracked modules.
     */
    public java.util.List<TrackedModule> getModuleStatuses(String accessToken) {
        return moduleService.readModuleStatuses(accessToken);
    }

    /**
     * Logs a new event.
     */
    public void logEvent(Severity severity, String source, String info, String accessToken) {
        LogEvent event = new LogEvent(severity, source, info, Instant.now());
        logEventService.logEvent(event, accessToken);
    }

    /**
     * Gets all logged events.
     */
    public java.util.List<LogEvent> getEvents(String accessToken) {
        return logEventService.readEvents(accessToken);
    }

    /**
     * Main method for standalone execution.
     */
    public static void main(String[] args) {
        SystemMonitorService service = SystemMonitorService.getInstance();
        System.out.println("System Monitor Service running...");
        
        // Keep service running
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            System.err.println("System Monitor Service interrupted");
        }
    }
}
