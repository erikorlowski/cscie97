package cscie97.asn4.asn5.ngatc.systemmonitor;

import java.time.Duration;
import java.time.Instant;

import cscie97.asn4.asn5.ngatc.common.Status;

/**
 * Represents a tracked module in the NGATC system.
 * Implements the Observer pattern to receive and process status updates.
 * Monitors for timeouts and automatically sets modules to OFFLINE if no updates received.
 */
public class TrackedModule implements ModuleObserver {
    private String id;
    private Instant mostRecentStatusUpdate;
    private Status moduleStatus;
    private Thread timeoutMonitorThread;
    private volatile boolean monitoring = true;

    public TrackedModule() {
        this.mostRecentStatusUpdate = Instant.now();
    }

    public TrackedModule(String id, Status moduleStatus) {
        this.id = id;
        this.moduleStatus = moduleStatus;
        this.mostRecentStatusUpdate = Instant.now();
        startTimeoutMonitoring();
    }

    /**
     * Starts a background thread to monitor for timeouts.
     */
    private void startTimeoutMonitoring() {
        timeoutMonitorThread = new Thread(this::pollForTimeout);
        timeoutMonitorThread.setDaemon(true);
        timeoutMonitorThread.start();
    }

    /**
     * Polls every 250ms to check if module has timed out.
     * If no status update received in 2 seconds, sets status to OFFLINE.
     */
    private void pollForTimeout() {
        while (monitoring) {
            try {
                Thread.sleep(250);
                Duration timeSinceLastUpdate = Duration.between(mostRecentStatusUpdate, Instant.now());
                if (timeSinceLastUpdate.toMillis() > 2000 && moduleStatus != Status.OFFLINE) {
                    System.out.println("Module " + id + " timed out - setting to OFFLINE");
                    moduleStatus = Status.OFFLINE;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    @Override
    public boolean onStatusUpdate(String id, Status moduleStatus) {
        if (this.id.equals(id)) {
            this.moduleStatus = moduleStatus;
            this.mostRecentStatusUpdate = Instant.now();
            return true;
        }
        return false;
    }

    @Override
    public boolean onModuleRemoval(String id) {
        if (this.id.equals(id)) {
            monitoring = false;
            if (timeoutMonitorThread != null) {
                timeoutMonitorThread.interrupt();
            }
            return true;
        }
        return false;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Instant getMostRecentStatusUpdate() {
        return mostRecentStatusUpdate;
    }

    public void setMostRecentStatusUpdate(Instant mostRecentStatusUpdate) {
        this.mostRecentStatusUpdate = mostRecentStatusUpdate;
    }

    public Status getModuleStatus() {
        return moduleStatus;
    }

    public void setModuleStatus(Status moduleStatus) {
        this.moduleStatus = moduleStatus;
        this.mostRecentStatusUpdate = Instant.now();
    }

    @Override
    public String toString() {
        return String.format("TrackedModule{id='%s', status=%s, lastUpdate=%s}", 
            id, moduleStatus, mostRecentStatusUpdate);
    }
}
