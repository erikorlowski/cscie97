# Next Generation Air Traffic Control (NGATC) System

## Overview

The NGATC is a safety-critical, high-availability air traffic control system designed to manage air traffic throughout the United States. The system is composed of multiple modules that communicate via REST APIs.

## Architecture

The system follows a microservices architecture with the following modules:

### Modules

1. **System Monitor** (`ngatc.systemmonitor`)
   - Tracks health and status of all modules
   - Logs system events for auditing
   - Provides module availability information

2. **Weather Service** (`ngatc.weather`)
   - Ingests and provides real-time weather data
   - Manages severe weather warnings
   - Provides weather information for flight planning

3. **Static Map Service** (`ngatc.staticmap`)
   - Manages waypoints, airports, and landmarks
   - Tracks terrain and building obstacles
   - Manages restricted airspace

4. **Flight Tracker** (`ngatc.flighttracker`) **[SAFETY CRITICAL]**
   - Tracks aircraft positions in real-time
   - Validates flight plans
   - Detects loss of separation conflicts
   - Generates counter-measure instructions
   - Predicts aircraft trajectories

5. **Controller Service** (`ngatc.controller`)
   - Primary interface for air traffic controllers
   - Manages control sectors
   - Facilitates communication between controllers and pilots
   - Displays alerts and flight information

6. **Simulator** (`ngatc.simulator`)
   - Generates mock data for testing and training
   - Administrator access only
   - Not used in production

### Common Components (`ngatc.common`)

- **Location**: GPS coordinates with altitude
- **Severity**: Event/alert severity levels
- **Status**: Module health status
- **LogEvent**: System event logging
- **Auth**: Authentication and authorization using Entitlement Service
- **Config**: System-wide configuration
- **Bootstrap**: System initialization

## Authentication and Authorization

The NGATC system integrates with the Housemate Entitlement Service for authentication and authorization. All inter-module communication requires valid authentication tokens.

### Roles

- **Administrator**: Full system access, configuration capabilities
- **Controller**: Access to flight data and sector management
- **Internal Module**: Inter-module communication
- **Pilot**: Submit flight plans, receive communications

## Communication

All modules communicate via REST APIs with the following characteristics:

- **Protocol**: HTTPS with TLS encryption
- **Format**: JSON
- **Authentication**: Token-based using Entitlement Service
- **High-Priority Ports**: Available for emergency communications

## Key Features

### Safety-Critical Design

The Flight Tracker module is developed as a safety-critical component:

- Deterministic conflict detection algorithms
- Automatic loss of separation alerts (< 100ms response)
- Comprehensive validation of all flight plans
- Redundant deployment with automatic failover

### Real-Time Performance

- Aircraft position updates: ≥ 1 Hz
- Surveillance data processing: < 500ms latency
- Controller UI refresh: ≥ 1 Hz
- Module status updates: every 1 second

### High Availability

- 99.999% availability target (5 nines)
- Automatic failover within 250ms
- Horizontal scalability for all modules
- Persistent data storage with Hibernate/MySQL

## Running the System

### Prerequisites

- Java 8 or higher
- MySQL database (for production deployment)
- Housemate Entitlement Service (from asn4)

### Compiling the System

**IMPORTANT**: All compilation commands below must be run from the `cscie97` directory (the root of the project).

```bash
# Navigate to the correct directory first
cd c:\harvard\cscie97\cscie97
# Or on Unix/Linux/Mac:
cd /path/to/cscie97
```

#### Compile All Modules

**Run these commands from: `cscie97` directory**

```bash
# Compile common package
javac -d . asn5/ngatc/common/*.java

# Compile System Monitor module
javac -cp ".;asn4" -d . asn5/ngatc/systemmonitor/*.java

# Compile Weather module
javac -cp ".;asn4" -d . asn5/ngatc/weather/*.java

# Compile Static Map module
javac -cp ".;asn4" -d . asn5/ngatc/staticmap/*.java

# Compile Flight Tracker module
javac -cp ".;asn4" -d . asn5/ngatc/flighttracker/*.java

# Compile Controller module
javac -cp ".;asn4" -d . asn5/ngatc/controller/*.java

# Compile Simulator module
javac -cp ".;asn4" -d . asn5/ngatc/simulator/*.java

# Compile Main application
javac -cp ".;asn4" -d . asn5/ngatc/Main.java
```

**Note for Unix/Linux/Mac**: Replace semicolons (`;`) with colons (`:`) in classpath:
```bash
javac -cp ".:asn4" -d . asn5/ngatc/systemmonitor/*.java
```

#### Compile with Single Command

**Run from: `cscie97` directory**

Alternatively, compile all Java files at once:

```bash
# Windows
javac -cp ".;asn4" -d . asn5/ngatc/**/*.java asn5/ngatc/*.java

# Unix/Linux/Mac
javac -cp ".:asn4" -d . asn5/ngatc/**/*.java asn5/ngatc/*.java
```

#### Dependencies

The NGATC system requires:
- **Entitlement Service** (asn4): Located at `cscie97/asn4/housemate/entitlement`
- The entitlement service classes must be compiled and in the classpath

To compile the Entitlement Service first (run from `cscie97` directory):
```bash
javac -d . asn4/housemate/entitlement/*.java
```

#### Classpath Notes

When compiling with `-d .` from the `cscie97/cscie97` directory, the class files are placed in `cscie97/cscie97/cscie97/asn5/ngatc/...` (nested structure). To run the application, you need to:
1. Navigate to the parent `cscie97` directory (one level up from where you compiled)
2. Use `-cp cscie97` to point to the directory containing the compiled classes

### Starting the System

**Navigate to: `C:\harvard\cscie97` (or equivalent parent directory)**

```bash
cd C:\harvard\cscie97

# Production mode (without simulator)
java -cp cscie97 cscie97.asn5.ngatc.Main

# Testing mode (with simulator)
java -cp cscie97 cscie97.asn5.ngatc.Main --simulator
```

### Starting Individual Modules

**Run these commands from: `C:\harvard\cscie97` directory**

Each module can be run independently for testing:

```bash
# System Monitor
java -cp cscie97 cscie97.asn5.ngatc.systemmonitor.SystemMonitorService

# Weather Service
java -cp cscie97 cscie97.asn5.ngatc.weather.WeatherService

# Static Map Service
java -cp cscie97 cscie97.asn5.ngatc.staticmap.StaticMapService

# Flight Tracker Service (Safety Critical)
java -cp cscie97 cscie97.asn5.ngatc.flighttracker.FlightTrackerService

# Controller Service
java -cp cscie97 cscie97.asn5.ngatc.controller.ControllerService

# Simulator (Testing Only)
java -cp cscie97 cscie97.asn5.ngatc.simulator.SimulatorService
```

## Module APIs

### System Monitor

- **POST /event**: Log a system event
- **GET /events**: Read all logged events (admin only)
- **POST /module**: Register a module for tracking
- **PUT /module**: Update module status
- **DELETE /module**: Remove module from tracking
- **GET /modules**: Get all module statuses

### Weather Service

- **POST /weather**: Ingest weather report
- **POST /severe-weather**: Ingest severe weather warning
- **GET /weather**: Get weather near location
- **GET /severe-weather**: Get active severe weather warnings

### Static Map Service

- **POST /waypoint**: Add waypoint
- **GET /waypoints**: Get all waypoints
- **GET /waypoints/near**: Get waypoints near location
- **POST /area**: Add area (terrain, building, airspace)
- **GET /areas**: Get all areas
- **GET /restricted**: Get restricted airspace

### Flight Tracker Service

- **POST /flight**: Add flight
- **PUT /flight**: Update flight dynamics
- **POST /flight/validate**: Validate flight plan
- **GET /flights**: Get all flights
- **GET /conflicts**: Get detected conflicts

### Controller Service

- **POST /sector/flight**: Add flight to sector
- **GET /sectors**: Get all sectors
- **GET /sector/{id}/flights**: Get flights in sector
- **POST /message/pilot**: Send message to pilot
- **POST /message/sector**: Send message to sector
- **POST /warning**: Receive flight warning

### Simulator Service (Admin Only)

- **POST /simulate/flight**: Create simulated flight
- **POST /simulate/weather**: Generate weather data
- **POST /simulate/scenario**: Run scenario
- **DELETE /simulate**: Clear simulation data

## Configuration

Module configuration is centralized in `Config.java`:

```java
// Module URLs
SYSTEM_MONITOR_URL = "http://localhost:8080"
FLIGHT_TRACKER_URL = "http://localhost:8081"
CONTROLLER_URL = "http://localhost:8082"
WEATHER_URL = "http://localhost:8083"
STATIC_MAP_URL = "http://localhost:8084"
SIMULATOR_URL = "http://localhost:8085"

// Safety parameters
SEPARATION_MINIMUM_MILES = 5.0
SEPARATION_MINIMUM_FEET = 1000.0
CONFLICT_DETECTION_INTERVAL_MS = 1000

// Module timeout
MODULE_TIMEOUT_MS = 2000
```

## Design Patterns

### Singleton Pattern
- Used in all service classes to ensure single instance
- Examples: `SystemMonitorService`, `FlightTrackerService`, etc.

### Observer Pattern
- `ModuleSubject` and `ModuleObserver` for module status tracking
- `TrackedModule` implements observer for timeout monitoring

### Factory Pattern
- Entitlement Service uses Abstract Factory pattern
- Consistent object creation across modules

## Testing

### Unit Testing
- CCN ≤ 3: Unit test not required
- CCN ≤ 9: Unit test with reasonable branch coverage
- CCN ≥ 10: 100% branch coverage for safety-critical code

### Module Testing
- Use Simulator to inject test data
- Validate module responses and behavior
- Test with mock REST API calls

### System Testing
- End-to-end scenarios using Simulator
- Happy path, loss of separation, invalid flight plans
- Failover and redundancy testing

## Security

- All communication encrypted with TLS
- Role-based access control via Entitlement Service
- Authentication required for all non-read operations
- Security event logging for auditing

## Deployment

The system is designed for deployment as a Kubernetes cluster:

- Each module deployed with dedicated database
- Multiple instances for redundancy
- Geographic distribution for resilience
- Automatic load balancing
- Health monitoring and auto-restart

## Documentation

- Design document: `cscie97/asn5/design.md`
- Entitlement Service documentation: `cscie97/asn4/Design.md`

## License

Harvard CSCI E-97 Assignment 5
Date: December 16, 2025

## Notes

- The Flight Tracker module is designated as safety-critical and follows SC3 development rigor
- AI agents are used for conflict prediction and route optimization, but all decisions are validated deterministically
- The Simulator module is for testing only and should not be enabled in production
- This implementation uses the Entitlement Service from Assignment 4 for authentication
