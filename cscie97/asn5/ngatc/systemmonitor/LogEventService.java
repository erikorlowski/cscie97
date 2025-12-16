package cscie97.asn5.ngatc.systemmonitor;

import cscie97.asn5.ngatc.common.LogEvent;
import cscie97.asn5.ngatc.common.Auth;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Service layer for LogEvent operations.
 * Handles business logic for event logging.
 */
public class LogEventService {
    private final List<LogEvent> events;

    public LogEventService() {
        this.events = new CopyOnWriteArrayList<>();
    }

    /**
     * Logs a new event.
     * 
     * @param newEvent The event to log
     * @param accessToken Authentication token
     */
    public void logEvent(LogEvent newEvent, String accessToken) {
        try {
            // Validate access
            Auth.checkAccess(accessToken, "internal_module", "system");
            
            // Add event to collection
            events.add(newEvent);
            
            System.out.println("Logged event: " + newEvent);
        } catch (Exception e) {
            System.err.println("Error logging event: " + e.getMessage());
            throw new DatabaseException("Failed to log event: " + e.getMessage());
        }
    }

    /**
     * Retrieves all logged events.
     * Requires administrator access.
     * 
     * @param accessToken Authentication token
     * @return List of all logged events
     */
    public List<LogEvent> readEvents(String accessToken) {
        try {
            // Validate access - only administrators can read events
            Auth.checkAccess(accessToken, "admin", "system");
            
            return new ArrayList<>(events);
        } catch (Exception e) {
            System.err.println("Error reading events: " + e.getMessage());
            throw new DatabaseException("Failed to read events: " + e.getMessage());
        }
    }
}
