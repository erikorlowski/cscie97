package cscie97.asn4.asn5.ngatc.simulator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cscie97.asn4.asn5.ngatc.common.Auth;
import cscie97.asn4.asn5.ngatc.common.Location;
import cscie97.asn4.asn5.ngatc.flighttracker.Flight;
import cscie97.asn4.asn5.ngatc.flighttracker.FlightDynamics;
import cscie97.asn4.asn5.ngatc.flighttracker.FlightPlan;
import cscie97.asn4.asn5.ngatc.staticmap.Airport;
import cscie97.asn4.asn5.ngatc.weather.WeatherReport;

/**
 * Simulator module for testing and training purposes.
 * Generates mock aircraft, weather, and surveillance data.
 * Only accessible to administrators.
 */
public class SimulatorService {
    private static SimulatorService instance;
    private final List<Flight> simulatedFlights;
    private final Random random;
    private long nextFlightId = 1000;

    private SimulatorService() {
        this.simulatedFlights = new ArrayList<>();
        this.random = new Random();
        initialize();
    }

    /**
     * Gets the singleton instance.
     */
    public static synchronized SimulatorService getInstance() {
        if (instance == null) {
            instance = new SimulatorService();
        }
        return instance;
    }

    /**
     * Initializes the Simulator service.
     */
    private void initialize() {
        System.out.println("Simulator Service initialized at " + Instant.now());
        System.out.println("WARNING: Simulator mode active - for training/testing only");
    }

    /**
     * Creates a simulated flight.
     * Only accessible to administrators.
     */
    public Flight createSimulatedFlight(String callSign, Location origin, Location destination, String accessToken) {
        try {
            // Validate admin access
            Auth.checkAccess(accessToken, "admin", "system");
            
            Flight flight = new Flight(nextFlightId++, callSign);
            flight.setDepartureTime(Instant.now());
            flight.setEta(Instant.now().plusSeconds(3600)); // 1 hour flight
            
            // Create flight plan
            FlightPlan plan = new FlightPlan(flight.getId(), flight.getDepartureTime(), flight.getEta());
            flight.setFlightPlan(plan);
            
            // Create initial dynamics
            FlightDynamics dynamics = new FlightDynamics(0, 90, 450, origin);
            flight.setActualFlightDynamics(dynamics);
            flight.setRequestedFlightDynamics(dynamics);
            
            simulatedFlights.add(flight);
            
            System.out.println("Created simulated flight: " + callSign);
            return flight;
        } catch (Exception e) {
            System.err.println("Error creating simulated flight: " + e.getMessage());
            throw new RuntimeException("Simulator access denied or error occurred");
        }
    }

    /**
     * Generates simulated weather data.
     */
    public WeatherReport generateWeatherData(Location location, String accessToken) {
        try {
            Auth.checkAccess(accessToken, "admin", "system");
            
            double temp = 15 + (random.nextDouble() * 20); // 15-35°C
            double windSpeed = random.nextDouble() * 30; // 0-30 knots
            double windHeading = random.nextDouble() * 360; // 0-360°
            
            WeatherReport report = new WeatherReport(temp, windSpeed, windHeading, location);
            
            System.out.println("Generated weather: " + report);
            return report;
        } catch (Exception e) {
            System.err.println("Error generating weather data: " + e.getMessage());
            return null;
        }
    }

    /**
     * Updates simulated flight position.
     */
    public void updateFlightPosition(Flight flight, Location newLocation, String accessToken) {
        try {
            Auth.checkAccess(accessToken, "admin", "system");
            
            FlightDynamics dynamics = flight.getActualFlightDynamics();
            if (dynamics != null) {
                dynamics.setLocation(newLocation);
                System.out.println("Updated flight " + flight.getCallSign() + " position to " + newLocation);
            }
        } catch (Exception e) {
            System.err.println("Error updating flight position: " + e.getMessage());
        }
    }

    /**
     * Gets all simulated flights.
     */
    public List<Flight> getSimulatedFlights(String accessToken) {
        try {
            Auth.checkAccess(accessToken, "admin", "system");
            return new ArrayList<>(simulatedFlights);
        } catch (Exception e) {
            System.err.println("Error getting simulated flights: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Clears all simulated data.
     */
    public void clearSimulationData(String accessToken) {
        try {
            Auth.checkAccess(accessToken, "admin", "system");
            
            simulatedFlights.clear();
            System.out.println("Cleared all simulation data");
        } catch (Exception e) {
            System.err.println("Error clearing simulation data: " + e.getMessage());
        }
    }

    /**
     * Runs a simulation scenario.
     */
    public void runScenario(String scenarioName, String accessToken) {
        try {
            Auth.checkAccess(accessToken, "admin", "system");
            
            System.out.println("Running simulation scenario: " + scenarioName);
            
            // Example: Create two flights that will potentially conflict
            Location jfkLocation = new Location(40.6413, -73.7781, 10000);
            Location laxLocation = new Location(33.9416, -118.4085, 10000);
            
            createSimulatedFlight("SIM001", jfkLocation, laxLocation, accessToken);
            createSimulatedFlight("SIM002", laxLocation, jfkLocation, accessToken);
            
            System.out.println("Scenario '" + scenarioName + "' initialized with " + simulatedFlights.size() + " flights");
        } catch (Exception e) {
            System.err.println("Error running scenario: " + e.getMessage());
        }
    }

    /**
     * Main method for standalone execution.
     */
    public static void main(String[] args) {
        SimulatorService service = SimulatorService.getInstance();
        System.out.println("Simulator Service running...");
        System.out.println("WARNING: This is a simulation/training environment only!");
        
        // Keep service running
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            System.err.println("Simulator Service interrupted");
        }
    }
}
