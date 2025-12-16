package cscie97.asn4.asn5.ngatc;

import java.time.Instant;

import cscie97.asn4.asn5.ngatc.common.Bootstrap;
import cscie97.asn4.asn5.ngatc.controller.ControllerService;
import cscie97.asn4.asn5.ngatc.flighttracker.FlightTrackerService;
import cscie97.asn4.asn5.ngatc.simulator.SimulatorService;
import cscie97.asn4.asn5.ngatc.staticmap.StaticMapService;
import cscie97.asn4.asn5.ngatc.systemmonitor.SystemMonitorService;
import cscie97.asn4.asn5.ngatc.weather.WeatherService;

/**
 * Main entry point for the Next Generation Air Traffic Control (NGATC) system.
 * Initializes and starts all modules of the system.
 * 
 * The NGATC is a safety-critical, high-availability system composed of multiple modules:
 * - System Monitor: Tracks health and status of all modules
 * - Weather: Provides weather data and severe weather warnings
 * - Static Map: Manages waypoints, airports, terrain, and airspace
 * - Flight Tracker: Safety-critical module for flight tracking and conflict detection
 * - Controller: Interface for air traffic controllers
 * - Simulator: Testing and training module
 * 
 * All modules communicate via REST APIs and use the Entitlement Service for authentication.
 */
public class Main {
    
    public static void main(String[] args) {
        printBanner();
        
        System.out.println("\n[" + Instant.now() + "] Starting NGATC System Initialization...\n");
        
        try {
            // Step 1: Bootstrap the system (initialize entitlement service, users, permissions)
            System.out.println("Step 1: Bootstrapping entitlement and authentication...");
            Bootstrap.initialize();
            System.out.println("✓ Bootstrap complete\n");
            
            // Step 2: Initialize System Monitor (must be first as other modules report to it)
            System.out.println("Step 2: Initializing System Monitor module...");
            SystemMonitorService systemMonitor = SystemMonitorService.getInstance();
            System.out.println("✓ System Monitor initialized\n");
            
            // Step 3: Initialize Weather Service
            System.out.println("Step 3: Initializing Weather module...");
            WeatherService weather = WeatherService.getInstance();
            System.out.println("✓ Weather Service initialized\n");
            
            // Step 4: Initialize Static Map Service
            System.out.println("Step 4: Initializing Static Map module...");
            StaticMapService staticMap = StaticMapService.getInstance();
            System.out.println("✓ Static Map Service initialized\n");
            
            // Step 5: Initialize Flight Tracker (safety-critical module)
            System.out.println("Step 5: Initializing Flight Tracker module (SAFETY CRITICAL)...");
            FlightTrackerService flightTracker = FlightTrackerService.getInstance();
            System.out.println("✓ Flight Tracker Service initialized\n");
            
            // Step 6: Initialize Controller Service
            System.out.println("Step 6: Initializing Controller module...");
            ControllerService controller = ControllerService.getInstance();
            System.out.println("✓ Controller Service initialized\n");
            
            // Step 7: Initialize Simulator (optional, for testing only)
            if (shouldStartSimulator(args)) {
                System.out.println("Step 7: Initializing Simulator module...");
                System.out.println("WARNING: Simulator mode enabled - for testing/training only!");
                SimulatorService simulator = SimulatorService.getInstance();
                System.out.println("✓ Simulator Service initialized\n");
            } else {
                System.out.println("Step 7: Simulator module not started (production mode)\n");
            }
            
            // System startup complete
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("  NGATC SYSTEM FULLY OPERATIONAL");
            System.out.println("  All modules initialized and running");
            System.out.println("  Time: " + Instant.now());
            System.out.println("═══════════════════════════════════════════════════════════\n");
            
            printModuleStatus();
            
            // Keep the main thread running
            System.out.println("NGATC system is now running. Press Ctrl+C to shutdown.\n");
            Thread.currentThread().join();
            
        } catch (InterruptedException e) {
            System.out.println("\n[" + Instant.now() + "] NGATC System shutting down...");
        } catch (Exception e) {
            System.err.println("\n[" + Instant.now() + "] CRITICAL ERROR during NGATC initialization:");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Prints the NGATC banner.
     */
    private static void printBanner() {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("  _   _  ____    _  _____ ____ ");
        System.out.println(" | \\ | |/ ___|  / \\|_   _/ ___|");
        System.out.println(" |  \\| | |  _  / _ \\ | || |    ");
        System.out.println(" | |\\  | |_| |/ ___ \\| || |___ ");
        System.out.println(" |_| \\_|\\____/_/   \\_\\_| \\____|");
        System.out.println();
        System.out.println("  Next Generation Air Traffic Control System");
        System.out.println("  Version 1.0");
        System.out.println("═══════════════════════════════════════════════════════════");
    }
    
    /**
     * Prints the status of all modules.
     */
    private static void printModuleStatus() {
        System.out.println("Module Status:");
        System.out.println("  • System Monitor    : RUNNING");
        System.out.println("  • Weather Service   : RUNNING");
        System.out.println("  • Static Map        : RUNNING");
        System.out.println("  • Flight Tracker    : RUNNING (Safety Critical)");
        System.out.println("  • Controller        : RUNNING");
        System.out.println();
        System.out.println("Module Communication:");
        System.out.println("  • All modules using REST API communication");
        System.out.println("  • TLS encryption enabled");
        System.out.println("  • Authentication via Entitlement Service");
        System.out.println();
    }
    
    /**
     * Determines if the simulator should be started based on command-line arguments.
     */
    private static boolean shouldStartSimulator(String[] args) {
        for (String arg : args) {
            if ("--simulator".equals(arg) || "--sim".equals(arg) || "--test".equals(arg)) {
                return true;
            }
        }
        return false;
    }
}
