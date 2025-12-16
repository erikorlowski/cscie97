package cscie97.asn5.ngatc.common;

/**
 * Configuration class for NGATC system settings.
 * Provides centralized configuration management for all modules.
 */
public class Config {
    // System Monitor configuration
    public static final String SYSTEM_MONITOR_URL = "http://localhost:8080";
    public static final int MODULE_TIMEOUT_MS = 2000;
    public static final int STATUS_UPDATE_INTERVAL_MS = 1000;

    // Flight Tracker configuration
    public static final String FLIGHT_TRACKER_URL = "http://localhost:8081";
    public static final double SEPARATION_MINIMUM_MILES = 5.0;
    public static final double SEPARATION_MINIMUM_FEET = 1000.0;
    public static final int CONFLICT_DETECTION_INTERVAL_MS = 1000;

    // Controller configuration
    public static final String CONTROLLER_URL = "http://localhost:8082";
    public static final int UI_REFRESH_INTERVAL_MS = 1000;

    // Weather configuration
    public static final String WEATHER_URL = "http://localhost:8083";
    public static final int WEATHER_UPDATE_INTERVAL_MS = 30000;

    // Static Map configuration
    public static final String STATIC_MAP_URL = "http://localhost:8084";

    // Simulator configuration
    public static final String SIMULATOR_URL = "http://localhost:8085";

    // Entitlement Service configuration
    public static final String ENTITLEMENT_SERVICE_PACKAGE = "cscie97.asn4.housemate.entitlement";

    // Database configuration
    public static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    public static final String DB_URL_BASE = "jdbc:mysql://localhost:3306/";

    private Config() {
        // Prevent instantiation
    }
}
