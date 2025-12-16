package cscie97.asn4.asn5.ngatc.flighttracker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import cscie97.asn4.asn5.ngatc.common.Auth;
import cscie97.asn4.asn5.ngatc.common.Config;
import cscie97.asn4.asn5.ngatc.common.Location;
import cscie97.asn4.asn5.ngatc.staticmap.Area;
import cscie97.asn4.asn5.ngatc.staticmap.Waypoint;

/**
 * Main service for the Flight Tracker module.
 * This is the safety-critical module that manages flights, detects conflicts,
 * and validates flight plans.
 * Implements Singleton pattern.
 */
public class FlightTrackerService {
    private static FlightTrackerService instance;
    private final ConcurrentHashMap<String, ConcurrentHashMap<Long, Flight>> flightMap;
    private final ConcurrentHashMap<String, ConcurrentHashMap<Long, Area>> areaMap;
    private final ConcurrentHashMap<String, ConcurrentHashMap<Long, Waypoint>> waypointMap;
    private long nextFlightId = 1;
    private long nextWarningId = 1;

    private FlightTrackerService() {
        this.flightMap = new ConcurrentHashMap<>();
        this.areaMap = new ConcurrentHashMap<>();
        this.waypointMap = new ConcurrentHashMap<>();
        initialize();
    }

    /**
     * Gets the singleton instance.
     */
    public static synchronized FlightTrackerService getInstance() {
        if (instance == null) {
            instance = new FlightTrackerService();
        }
        return instance;
    }

    /**
     * Initializes the Flight Tracker service.
     */
    private void initialize() {
        System.out.println("Flight Tracker Service initialized at " + java.time.Instant.now());
        
        // Start background conflict detection
        startConflictDetection();
        
        // Start background status reporting
        startStatusReporting();
    }

    /**
     * Starts background conflict detection thread.
     */
    private void startConflictDetection() {
        Thread conflictThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(Config.CONFLICT_DETECTION_INTERVAL_MS);
                    analyzeFlights();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        conflictThread.setDaemon(true);
        conflictThread.start();
    }

    /**
     * Starts background status reporting thread.
     */
    private void startStatusReporting() {
        Thread statusThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(Config.STATUS_UPDATE_INTERVAL_MS);
                    // In real implementation, would send status to System Monitor via REST API
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        statusThread.setDaemon(true);
        statusThread.start();
    }

    /**
     * Adds a new flight to tracking.
     */
    public void addFlight(Flight flight, String accessToken) {
        try {
            Auth.checkAccess(accessToken, "internal_module", "system");
            
            if (flight.getId() == 0) {
                flight.setId(nextFlightId++);
            }
            
            String hashKey = getLocationHashKey(flight.getActualFlightDynamics().getLocation());
            flightMap.computeIfAbsent(hashKey, k -> new ConcurrentHashMap<>()).put(flight.getId(), flight);
            
            System.out.println("Added flight: " + flight);
        } catch (Exception e) {
            System.err.println("Error adding flight: " + e.getMessage());
        }
    }

    /**
     * Updates flight with new dynamics.
     */
    public void updateFlight(FlightDynamics flightDynamics, String accessToken) {
        try {
            Auth.checkAccess(accessToken, "internal_module", "system");
            
            // Find flight and update
            // In real implementation would have proper flight lookup
            System.out.println("Updated flight dynamics: " + flightDynamics);
        } catch (Exception e) {
            System.err.println("Error updating flight: " + e.getMessage());
        }
    }

    /**
     * Analyzes a proposed flight plan.
     * Returns null if accepted, FlightWarning if rejected.
     */
    public FlightWarning analyzeFlightPlanProposal(Flight flight, String accessToken) {
        try {
            Auth.checkAccess(accessToken, "internal_module", "system");
            
            // Simplified validation - real implementation would be comprehensive
            if (flight.getFlightPlan() == null || flight.getFlightPlan().getWaypoints().isEmpty()) {
                DeviationWarning warning = new DeviationWarning(nextWarningId++, 
                    "Flight plan must contain at least one waypoint");
                return warning;
            }
            
            // Check for restricted airspace, terrain conflicts, etc.
            // Real implementation would do comprehensive checks
            
            System.out.println("Flight plan approved for: " + flight.getCallSign());
            return null; // No warnings = approved
        } catch (Exception e) {
            System.err.println("Error analyzing flight plan: " + e.getMessage());
            return new DeviationWarning(nextWarningId++, "Error analyzing flight plan: " + e.getMessage());
        }
    }

    /**
     * Analyzes all flights for conflicts.
     * This is safety-critical deterministic code.
     */
    public void analyzeFlights() {
        List<Flight> allFlights = getAllFlights();
        
        // Check for loss of separation between all flight pairs
        for (int i = 0; i < allFlights.size(); i++) {
            for (int j = i + 1; j < allFlights.size(); j++) {
                Flight flight1 = allFlights.get(i);
                Flight flight2 = allFlights.get(j);
                
                FlightWarning warning = checkSeparation(flight1, flight2);
                if (warning != null) {
                    System.out.println("CONFLICT DETECTED: " + warning.getWarningMessage());
                    // In real implementation, would send warning to Controller via REST API
                }
            }
        }
    }

    /**
     * Checks separation between two flights.
     * Returns warning if separation is violated, null otherwise.
     */
    private FlightWarning checkSeparation(Flight flight1, Flight flight2) {
        if (flight1.getActualFlightDynamics() == null || flight2.getActualFlightDynamics() == null) {
            return null;
        }
        
        Location loc1 = flight1.getActualFlightDynamics().getLocation();
        Location loc2 = flight2.getActualFlightDynamics().getLocation();
        
        if (loc1 == null || loc2 == null) {
            return null;
        }
        
        // Calculate horizontal distance (simplified)
        double latDiff = loc1.getLatitude() - loc2.getLatitude();
        double lonDiff = loc1.getLongitude() - loc2.getLongitude();
        double horizontalDistance = Math.sqrt(latDiff * latDiff + lonDiff * lonDiff) * 69; // Miles
        
        // Calculate vertical distance
        double verticalDistance = Math.abs(loc1.getAltitudeFeet() - loc2.getAltitudeFeet());
        
        // Check minimum separation standards
        if (horizontalDistance < Config.SEPARATION_MINIMUM_MILES && 
            verticalDistance < Config.SEPARATION_MINIMUM_FEET) {
            
            MidAirCollisionWarning warning = new MidAirCollisionWarning(
                nextWarningId++, 30, horizontalDistance);
            warning.addFlightInDanger(flight1);
            warning.addFlightInDanger(flight2);
            return warning;
        }
        
        return null;
    }

    /**
     * Gets location hash key for spatial indexing.
     */
    public String getLocationHashKey(Location location) {
        if (location == null) return "unknown";
        
        // Simple grid-based hashing
        int latGrid = (int)(location.getLatitude() / 10);
        int lonGrid = (int)(location.getLongitude() / 10);
        return String.format("%d,%d", latGrid, lonGrid);
    }

    /**
     * Gets all flights currently being tracked.
     */
    public List<Flight> getAllFlights() {
        List<Flight> allFlights = new ArrayList<>();
        for (ConcurrentHashMap<Long, Flight> map : flightMap.values()) {
            allFlights.addAll(map.values());
        }
        return allFlights;
    }

    /**
     * Gets flights near a location.
     */
    public List<Flight> getCloseFlights(Flight flight) {
        // Simplified - real implementation would use spatial indexing
        return getAllFlights();
    }

    /**
     * Main method for standalone execution.
     */
    public static void main(String[] args) {
        FlightTrackerService service = FlightTrackerService.getInstance();
        System.out.println("Flight Tracker Service running...");
        
        // Keep service running
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            System.err.println("Flight Tracker Service interrupted");
        }
    }
}
