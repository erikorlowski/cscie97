package cscie97.asn4.asn5.ngatc.weather;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import cscie97.asn4.asn5.ngatc.common.Auth;
import cscie97.asn4.asn5.ngatc.common.Location;

/**
 * Main service for the Weather module.
 * Manages weather reports and severe weather warnings.
 * Provides REST API for other modules to access weather data.
 */
public class WeatherService {
    private static WeatherService instance;
    private final List<WeatherReport> weatherReports;
    private final List<SevereWeather> severeWeatherList;

    private WeatherService() {
        this.weatherReports = new CopyOnWriteArrayList<>();
        this.severeWeatherList = new CopyOnWriteArrayList<>();
        initialize();
    }

    /**
     * Gets the singleton instance.
     */
    public static synchronized WeatherService getInstance() {
        if (instance == null) {
            instance = new WeatherService();
        }
        return instance;
    }

    /**
     * Initializes the Weather service.
     */
    private void initialize() {
        System.out.println("Weather Service initialized at " + java.time.Instant.now());
        
        // Start background thread to report status to System Monitor
        startStatusReporting();
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
     * Ingests a new weather report.
     * 
     * @param report The weather report
     * @param accessToken Authentication token
     */
    public void ingestWeatherReport(WeatherReport report, String accessToken) {
        try {
            // Validate access
            Auth.checkAccess(accessToken, "internal_module", "system");
            
            weatherReports.add(report);
            System.out.println("Ingested weather report: " + report);
            
            // Clean up old reports (keep last 1000)
            if (weatherReports.size() > 1000) {
                weatherReports.remove(0);
            }
        } catch (Exception e) {
            System.err.println("Error ingesting weather report: " + e.getMessage());
        }
    }

    /**
     * Ingests a severe weather warning.
     * 
     * @param weather The severe weather
     * @param accessToken Authentication token
     */
    public void ingestSevereWeather(SevereWeather weather, String accessToken) {
        try {
            // Validate access
            Auth.checkAccess(accessToken, "internal_module", "system");
            
            severeWeatherList.add(weather);
            System.out.println("Ingested severe weather: " + weather);
        } catch (Exception e) {
            System.err.println("Error ingesting severe weather: " + e.getMessage());
        }
    }

    /**
     * Gets weather reports near a location.
     * 
     * @param location The location to search near
     * @param radiusMiles Search radius in miles
     * @param accessToken Authentication token
     * @return List of nearby weather reports
     */
    public List<WeatherReport> getWeatherNearLocation(Location location, double radiusMiles, String accessToken) {
        try {
            // Validate access
            Auth.checkAccess(accessToken, "internal_module", "system");
            
            // Simple distance calculation (would be more sophisticated in real implementation)
            return weatherReports.stream()
                .filter(report -> isWithinRadius(report.getLocation(), location, radiusMiles))
                .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error getting weather: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    /**
     * Gets active severe weather warnings.
     * 
     * @param accessToken Authentication token
     * @return List of active severe weather warnings
     */
    public List<SevereWeather> getActiveSevereWeather(String accessToken) {
        try {
            // Validate access
            Auth.checkAccess(accessToken, "internal_module", "system");
            
            return severeWeatherList.stream()
                .filter(SevereWeather::isActive)
                .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error getting severe weather: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    /**
     * Simple distance check (Euclidean approximation).
     * Real implementation would use Haversine formula.
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
        WeatherService service = WeatherService.getInstance();
        System.out.println("Weather Service running...");
        
        // Keep service running
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            System.err.println("Weather Service interrupted");
        }
    }
}
