package cscie97.asn4.asn5.ngatc.staticmap;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import cscie97.asn4.asn5.ngatc.common.Auth;
import cscie97.asn4.asn5.ngatc.common.Location;

/**
 * Main service for the Static Map module.
 * Manages waypoints, landmarks, airports, terrain, buildings, and restricted airspace.
 * Provides REST API for other modules to access static map data.
 */
public class StaticMapService {
    private static StaticMapService instance;
    private final List<Waypoint> waypoints;
    private final List<Area> areas;
    private long nextWaypointId = 1;
    private long nextAreaId = 1;

    private StaticMapService() {
        this.waypoints = new CopyOnWriteArrayList<>();
        this.areas = new CopyOnWriteArrayList<>();
        initialize();
    }

    /**
     * Gets the singleton instance.
     */
    public static synchronized StaticMapService getInstance() {
        if (instance == null) {
            instance = new StaticMapService();
        }
        return instance;
    }

    /**
     * Initializes the Static Map service with sample data.
     */
    private void initialize() {
        System.out.println("Static Map Service initialized at " + java.time.Instant.now());
        
        // Load some sample data
        loadSampleData();
        
        // Start background thread to report status to System Monitor
        startStatusReporting();
    }

    /**
     * Loads sample static map data.
     */
    private void loadSampleData() {
        // Sample airports
        Airport jfk = new Airport(nextWaypointId++, "John F. Kennedy International Airport", "JFK",
            new Location(40.6413, -73.7781, 13), 13);
        Airport lax = new Airport(nextWaypointId++, "Los Angeles International Airport", "LAX",
            new Location(33.9416, -118.4085, 125), 125);
        
        waypoints.add(jfk);
        waypoints.add(lax);
        
        System.out.println("Loaded sample static map data");
    }

    /**
     * Starts a background thread to report status periodically.
     */
    private void startStatusReporting() {
        Thread statusThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000); // Report every second
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
     * Adds a new waypoint.
     */
    public void addWaypoint(Waypoint waypoint, String accessToken) {
        try {
            Auth.checkAccess(accessToken, "admin", "system");
            waypoint.setId(nextWaypointId++);
            waypoints.add(waypoint);
            System.out.println("Added waypoint: " + waypoint);
        } catch (Exception e) {
            System.err.println("Error adding waypoint: " + e.getMessage());
        }
    }

    /**
     * Adds a new area (terrain, building, airspace, etc.).
     */
    public void addArea(Area area, String accessToken) {
        try {
            Auth.checkAccess(accessToken, "admin", "system");
            area.setId(nextAreaId++);
            areas.add(area);
            System.out.println("Added area: " + area);
        } catch (Exception e) {
            System.err.println("Error adding area: " + e.getMessage());
        }
    }

    /**
     * Gets all waypoints.
     */
    public List<Waypoint> getAllWaypoints(String accessToken) {
        try {
            Auth.checkAccess(accessToken, "internal_module", "system");
            return new java.util.ArrayList<>(waypoints);
        } catch (Exception e) {
            System.err.println("Error getting waypoints: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    /**
     * Gets waypoints near a location.
     */
    public List<Waypoint> getWaypointsNearLocation(Location location, double radiusMiles, String accessToken) {
        try {
            Auth.checkAccess(accessToken, "internal_module", "system");
            
            return waypoints.stream()
                .filter(wp -> isWithinRadius(wp.getLocation(), location, radiusMiles))
                .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error getting nearby waypoints: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    /**
     * Gets all areas.
     */
    public List<Area> getAllAreas(String accessToken) {
        try {
            Auth.checkAccess(accessToken, "internal_module", "system");
            return new java.util.ArrayList<>(areas);
        } catch (Exception e) {
            System.err.println("Error getting areas: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    /**
     * Gets restricted airspace areas.
     */
    public List<RestrictedAirspace> getRestrictedAirspace(String accessToken) {
        try {
            Auth.checkAccess(accessToken, "internal_module", "system");
            
            return areas.stream()
                .filter(area -> area instanceof RestrictedAirspace)
                .map(area -> (RestrictedAirspace) area)
                .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error getting restricted airspace: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    /**
     * Gets terrain and buildings (static hazards).
     */
    public List<Area> getStaticHazards(String accessToken) {
        try {
            Auth.checkAccess(accessToken, "internal_module", "system");
            
            return areas.stream()
                .filter(area -> area instanceof Terrain || area instanceof Building)
                .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error getting static hazards: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    /**
     * Simple distance check (Euclidean approximation).
     */
    private boolean isWithinRadius(Location loc1, Location loc2, double radiusMiles) {
        if (loc1 == null || loc2 == null) return false;
        
        double latDiff = loc1.getLatitude() - loc2.getLatitude();
        double lonDiff = loc1.getLongitude() - loc2.getLongitude();
        double distance = Math.sqrt(latDiff * latDiff + lonDiff * lonDiff) * 69; // Rough miles conversion
        
        return distance <= radiusMiles;
    }

    /**
     * Main method for standalone execution.
     */
    public static void main(String[] args) {
        StaticMapService service = StaticMapService.getInstance();
        System.out.println("Static Map Service running...");
        
        // Keep service running
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            System.err.println("Static Map Service interrupted");
        }
    }
}
