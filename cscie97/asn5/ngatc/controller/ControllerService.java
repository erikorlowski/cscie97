package cscie97.asn4.asn5.ngatc.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import cscie97.asn4.asn5.ngatc.common.Auth;
import cscie97.asn4.asn5.ngatc.flighttracker.Flight;
import cscie97.asn4.asn5.ngatc.flighttracker.FlightWarning;
import cscie97.asn4.asn5.ngatc.staticmap.Area;
import cscie97.asn4.asn5.ngatc.staticmap.Waypoint;

/**
 * Main service for the Controller module.
 * Manages control sectors, flights, and communication with controllers and pilots.
 * Implements Singleton pattern.
 */
public class ControllerService {
    private static ControllerService instance;
    private final ConcurrentHashMap<Long, ControlSector> sectors;
    private final ConcurrentHashMap<Long, ConcurrentHashMap<Long, Flight>> flightMap;
    private final ConcurrentHashMap<Long, ConcurrentHashMap<Long, Area>> areas;
    private final ConcurrentHashMap<Long, ConcurrentHashMap<Long, Waypoint>> waypoints;
    private long nextSectorId = 1;

    private ControllerService() {
        this.sectors = new ConcurrentHashMap<>();
        this.flightMap = new ConcurrentHashMap<>();
        this.areas = new ConcurrentHashMap<>();
        this.waypoints = new ConcurrentHashMap<>();
        initialize();
    }

    /**
     * Gets the singleton instance.
     */
    public static synchronized ControllerService getInstance() {
        if (instance == null) {
            instance = new ControllerService();
        }
        return instance;
    }

    /**
     * Initializes the Controller service.
     */
    private void initialize() {
        System.out.println("Controller Service initialized at " + java.time.Instant.now());
        
        // Create default sectors
        createDefaultSectors();
        
        // Start status reporting
        startStatusReporting();
    }

    /**
     * Creates default control sectors.
     */
    private void createDefaultSectors() {
        ControlSector sector1 = new ControlSector(nextSectorId++, "Sector 1", 0, 20000);
        ControlSector sector2 = new ControlSector(nextSectorId++, "Sector 2", 20000, 40000);
        
        sectors.put(sector1.getId(), sector1);
        sectors.put(sector2.getId(), sector2);
        
        System.out.println("Created default control sectors");
    }

    /**
     * Starts background status reporting.
     */
    private void startStatusReporting() {
        Thread statusThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    // Would send status to System Monitor via REST API
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
     * Adds a flight to a sector.
     */
    public void addFlight(Flight flight, long sectorId, String accessToken) {
        try {
            Auth.checkAccess(accessToken, "controller", "system");
            
            flightMap.computeIfAbsent(sectorId, k -> new ConcurrentHashMap<>())
                .put(flight.getId(), flight);
            
            ControlSector sector = sectors.get(sectorId);
            if (sector != null) {
                sector.setNumberOfCurrentFlights(
                    flightMap.get(sectorId).size());
            }
            
            System.out.println("Added flight " + flight.getCallSign() + " to sector " + sectorId);
        } catch (Exception e) {
            System.err.println("Error adding flight to sector: " + e.getMessage());
        }
    }

    /**
     * Sends a message to a pilot.
     */
    public void sendMessageToPilot(Flight flight, String message, String accessToken) {
        try {
            Auth.checkAccess(accessToken, "controller", "system");
            
            System.out.println("Sending message to flight " + flight.getCallSign() + ": " + message);
            // In real implementation, would send via REST API or messaging system
        } catch (Exception e) {
            System.err.println("Error sending message to pilot: " + e.getMessage());
        }
    }

    /**
     * Sends a message to a sector (all controllers in that sector).
     */
    public void sendMessageToSector(ControlSector sector, String message, String accessToken) {
        try {
            Auth.checkAccess(accessToken, "controller", "system");
            
            sector.addMessage(message);
            System.out.println("Sent message to sector " + sector.getName() + ": " + message);
        } catch (Exception e) {
            System.err.println("Error sending message to sector: " + e.getMessage());
        }
    }

    /**
     * Handles a flight warning from the Flight Tracker.
     */
    public void receiveFlightWarning(FlightWarning warning, String accessToken) {
        try {
            Auth.checkAccess(accessToken, "internal_module", "system");
            
            System.out.println("ALERT RECEIVED: " + warning.getWarningMessage());
            
            // Automatically send warning to affected flights
            for (Flight flight : warning.getFlightsInDanger()) {
                sendMessageToPilot(flight, warning.getWarningMessage(), accessToken);
            }
        } catch (Exception e) {
            System.err.println("Error handling flight warning: " + e.getMessage());
        }
    }

    /**
     * Gets all sectors.
     */
    public List<ControlSector> getAllSectors(String accessToken) {
        try {
            Auth.checkAccess(accessToken, "controller", "system");
            return new ArrayList<>(sectors.values());
        } catch (Exception e) {
            System.err.println("Error getting sectors: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Gets flights in a sector.
     */
    public List<Flight> getSectorFlights(long sectorId, String accessToken) {
        try {
            Auth.checkAccess(accessToken, "controller", "system");
            
            ConcurrentHashMap<Long, Flight> sectorFlights = flightMap.get(sectorId);
            if (sectorFlights != null) {
                return new ArrayList<>(sectorFlights.values());
            }
            return new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Error getting sector flights: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Main method for standalone execution.
     */
    public static void main(String[] args) {
        ControllerService service = ControllerService.getInstance();
        System.out.println("Controller Service running...");
        
        // Keep service running
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            System.err.println("Controller Service interrupted");
        }
    }
}
