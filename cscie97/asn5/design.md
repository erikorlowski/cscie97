# Next Generation Air Traffic Controller - Software Design
__Date:__ 12/3/2025
__Author:__ Erik Orlowski
__Reviewers:__ Jefferson Dang, Kyriaki Avgerinou

## Introduction
The document captures the requirements, design and test plan for the Next Generation Air Traffic Control (NGATC) system. The NGATC is composed of several modules, this document will discuss the architectural design of how these modules work together, as well as each of these modules individually.

## Overview
The NGATC is a scalable, high availability and safety critical system that will manage air traffic throughout the United States. The system supports the ability to:
* Track aircraft position in US airspace
* Use flight plan data and current aircraft data to predict aircraft flight paths
* Use flight path predictions to predict and prevent any aircraft collisions
* Monitor and reject flight plans with invalid routes (e.g. through restricted airspace or into obstacles)
* Display information about flights to Air Traffic Controllers
* Support communication between the NGATC and pilots
* Allow administrators to configure the NGATC system as needed
* Monitor and log events throughout the system

Each of the modules in the NGATC will communicate with each other through the REST API protocol. The NGATC will be created as a Kubernetes cluster, enabling redundancy of the modules making up the system as well as allowing the system to be easily scaled and reconfigured.

The system will be tested at a functional level (verifying and validating the system as a whole), a module level (verifying individual modules) and at a unit level (unit testing of individual methods).

This document includes a discussion of the NGATC system as a whole, as well as each of the modules that make it up. This document also discusses the functional and non-functional requirements of the system and modules, as well as a high level test plan.

## System Level Requirements
The following section discusses the system level requirements of the NGATC. More granular requirements are discusses in the module level requirements below.

### Functional Requirements

#### Aircraft Tracking and Surveillance
| Requirement: Real-Time Aircraft Data Ingestion |
|--|
| The NGATC shall ingest real-time position data from surveillance sources including radar, ADS-B, satellite and potential future sources through a well-defined API. |

| Requirement: Real-Time Aircraft Position Update |
|--|
| The NGATC shall update the real-time position of all tracked aircraft at a rate of at least once per second. |

| Requirement: Unified Aircraft Track |
|--|
| The NGATC shall synthesize surveillance inputs for a single aircraft into a single, unified aircraft track. |

| Requirement: Aircraft Data Display |
|--|
| The NGATC shall display the position, pitch, altitude, speed and heading of each tracked aircraft to controllers. |

#### Flight Data Management
| Requirement: Flight Plan Ingestion |
|--|
| The NGATC shall ingest aircraft flight plans through a well defined API. |

| Requirement: Flight Plan Association |
|--|
| The NGATC shall associate aircraft flight plans with aircraft tracks. |

| Requirement: Flight Plan Ammendments |
|--|
| The NGATC shall support the ability for pilots and controllers to make ammendments to flight plans. |

| Requirement: Pilot Flight Plan Ammendment Acceptance |
|--|
| When the NGATC receives a proposed flight plan ammendment from a pilot, the NGATC shall have the ability to accept or reject this proposal. |

| Requirement: Aircraft Trajectory Prediction |
|--|
| The NGATC shall predict aircraft trajectories based on the aircraft's speed, altitude and route. |

| Requirement: Flight Plan Deviation Notifications |
|--|
| The NGATC shall notify controllers of deviations between an aircraft's flight plan and its actual behavior beyond acceptable limits. |

#### Communication Services
| Requirement: Secure Pilot Messaging Interface |
|--|
| The NGATC shall provide a secure messaging interface between controllers and pilots. |

| Requirement: Communication Log Audits and Playback |
|--|
| The NGATC shall persistently store communication logs to enable audits and playback. |

| Requirement: Controller to Controller Messaging |
|--|
| The NGATC shall support the ability for any two controllers to message each other. |

| Requirement: Controller Status Indicators |
|--|
| The NGATC will make available to controllers, supervisors and administrators, status indicators showing which controller is responsible for which sector of airspace. |

| Requirement: High Priority Ports |
|--|
| Each module of the NGATC shall provide high priority communication ports to be only to be used to ensure real-time communication in emergency situations. |

#### Conflict Detection and Safety Alerts
| Requirement: Loss of Separation Detection |
|--|
| The NGATC shall detect any loss of separation conflict with other aircraft or with static hazards with a [Probaility of Failure on Demand](https://www.exida.com/blog/back-to-basics-16-pfdavg) of less than 0.001. |

| Requirement: Loss of Separation Alert to Aircraft |
|--|
| Upon detection of loss of separation, the NGATC shall provide an alert to the involved aircraft including [the position, altitude, heading, and vertical and horizontal speeds] of both aircraft, and avoidance guidance (both aircrafts' avoidance guidance are sent to both aircraft) within 100 ms. |
| Rationale: In loss of separation incidents, it is unacceptable to rely solely on the reaction time of a human operator. Therefore, it is preferable to trust the initial task of alerting the involved aircraft to automated systems. |

| Requirement: Loss of Separation Alert Integrity |
|--|
| To allow aircraft to verify the integrity of loss of separation alerts, the communication from the NGATC shall include a 32 bit CRC for every 4096 bits of data sent and a timestamp (in milliseconds past the Unix Epoch) of when the message was initiated, with an accompanying 32 bit CRC. This communication shall be sent 3 times to increase the chances of all involved aircraft receiving a valid communication. |

| Requirement: Loss of Separation Alert to Controller |
|--|
| When a loss of separation event occurs, the NGATC shall notify the relevant controller(s) with the same information and guidance provided to the involved aircraft. |

| Requirement: Restricted Airspace Alerts |
|--|
| The NGATC shall alert controllers when an aircraft violates restricted airspace. |

#### Weather and External Data Integration
| Requirement: Real-Time Weather Ingestion |
|--|
| The NGATC shall ingest real-time weather and turbulence data. |

| Requirement: Weather Impacts on Tracjectories |
|--|
| The NGATC shall compensate for the impact of weather on flight trajectories. |

| Requirement: UI Weather Overlays |
|--|
| The NGATC shall include a weather overlay on controller displays. |

#### Sector Management
| Requirement: Dynamic Sector Boundaries |
|--|
| The NGATC shall administrators merging and splitting sectors based on workload. |

| Requirement: Aircraft Sector Reassignment |
|--|
| The NGATC shall automatically reassign aircraft when sector boundaries change. |

| Requirement: Sector Workload Threshold Alerts |
|--|
| When a sector exceeds workload thresholds, the NGATC shall alert administrators and supervisors. |

#### Airspace Automation and AI Assistance
| Requirement: AI Assisted Conflict Prediction |
|--|
| The NGATC shall provide AI assisted conflict prediction for at least 3-5 minutes into the future. |

| Requirement: AI Assisted Conflict Alert |
|--|
| When a potential future conflict is identified by the AI assisted conflict prediction, an alert will be given to the controller. |

| Requirement: AI Generated Route Optimization Suggestions |
|--|
| The NGATC shall provide AI generated route optimization suggestions that controllers can either accept or reject. |

| Requirement: AI Detected Abnormal Aircraft Behavior |
|--|
| The NGATC shall provide alerts to the controller for AI detected abnormal aircraft behavior, such as altitude deviations. |

#### Controller User Interface
| Requirement: Real-Time Aircraft Map |
|--|
| The NGATC shall display aircraft on a real-time map with configurable zoom levels. |

| Requirement: Color Coded Alerts |
|--|
| The NGATC shall display color coded visual alerts to controllers. Red alerts indicate an immediate safety concern, yellow alerts indicate a potential future safety concern and white alerts are informational. |

| Requirement: Aircraft Details Window |
|--|
| The NGATC shall provide controllers an aircraft details window, display the aircraft's altitude, route, communication status and ETA. |

| Requirement: Drag and Drop Sector Reassignment |
|--|
| The NGATC shall support the ability for controllers to drag and drop aircraft to reassign their sector. |

#### System Monitoring and Logging
| Requirement: Event Logging |
|--|
| The NGATC shall log all system events, alerts and messaging for auditing. |

| Requirement: Module Health Monitoring |
|--|
| The NGATC shall provide health monitoring for each module, visible to administrators. |

| Requirement: Service Unavailable Alert |
|--|
| The NGATC shall provide an alert to administrators, supervisors and controllers when any module becomes unavailable. |

#### Adminstrative Functions
| Requirement: System Configurability |
|--|
| The NGATC shall allow for the configuration of what inputs to accept as surveillance inputs, the severity of alerts and aircraft separation thresholds. |

| Requirement: Role Based Access |
|--|
| The NGATC shall support role based actions based on the controller, supervisor and administrator roles. |

| Requirement: Simulation Mode |
|--|
| The NGATC shall support a simulation mode for training. |

### Non-Functional Requirements

#### Performance
| Requirement: Surveillance Latency |
|--|
| The NGATC shall process surveillance updates with a latency of less than 500 ms. |

| Requirement: Aircraft Volume |
|--|
| The NGATC shall support at least 5000 simultaneous aircraft tracks. |

| Requirement: Controller UI Refresh Rate |
|--|
| The NGATC controller user interface shall update aircraft positions at least once per second. |

#### Availability and Reliability
| Requirement: 5 Nines of Availability |
|--|
| The NGATC shall be available 99.999% of the time. |

| Requirement: Redundance Services |
|--|
| The NGATC shall provide redundancy with automatic within 250 ms for all modules whose absence could adversely affect aircraft safety. |
| Comment: This system does not necessarily require a "bumpless" switchover, so long as all data used by modules is not more than 2 seconds old (measured from when the NGATC ingested the data) and the module uses the most current available data within 1 second of the switchover event. |

| Requirement: Track Data Preservation |
|--|
| When the NGATC switches to another instance of a module due to a failure, this switchover shall preserve all track data. |

#### Scalability
| Requirement: Horizontal Scalability |
|--|
| The NGATC deployment shall scale horizontally to support increases in aircraft volume. |

| Requirement: Module Scaling |
|--|
| NGATC modules shall have the ability to scale independently based on loading. |

| Requirement: Data Pipeline Support |
|--|
| NGATC data pipelines shall support ingest rates of up to 100,000 messages per second. |

#### Security
| Requirement: Communication Encryption |
|--|
| All communication between modules shall use TLE encryption. |

| Requirement: Role-Based Authentication |
|--|
| The NGATC shall enforce role based authentication. |

| Requirement: Security Logging |
|--|
| The NGATC shall log all security-relevant events, including logins, changes and overrides. |

| Requirement: Flight Data Modification |
|--|
| The NGATC shall protect against the unauthorized modification of flight data. |

#### Maintainability
| Requirement: Microservice Use |
|--|
| NGATC modules shall be implemented as microservices using well-defined APIs. |

| Requirement: Module Failure Isolation |
|--|
| The NGATC shall isolate failures to individual modules to prevent cascading effects. |

| Requirement: Naming and Documentation |
|--|
| Code and service interactions shall follow clear naming conventions and documentation standards. |

#### Usability
| Requirement: Controller Alert Reaction Time |
|--|
| Controllers shall be able to identify conflicts within 2 seconds of an alert being generated. |

| Requirement: Color Coded Alerts |
|--|
| The NGATC UI shall distinguish between alert severity levels using different colors and shapes. |

| Requirement: Night and Day Mode |
|--|
| The NGATC UI shall support both night and day display modes. |

#### Interoperability
| Requirement: Surveillance Data Input Format |
|--|
| The NGATC shall use JSON as the supported input format for surveillance data. |

| Requirement: External REST API |
|--|
| The NGATC shall provide a REST API for integrating with external systems. |

| Requirement: New Data Sources |
|--|
| The NGATC shall support the "plug and play" addition of new data sources. |

#### Testability
| Requirement: Module Testability |
|--|
| Each module shall be independently testable using mock data. |

| Requirement: Simulation Testing |
|--|
| The NGATC shall provide simulation tools that can be used to test conflict detection logic at a system level. |

| Requirement: Automated Test Coverage |
|--|
| At least 80% automated test coverage of the NGATC core logic shall be achieved. |

## Module Summary
The modules that make up the NGATC are shown in the component diagram below and are explained in more detail following the diagram.

```plantuml
@startuml
title NGATC Modules - Component Diagram

component "Pilot Communicator" as comms
component Administrator as admin
component Weather as weather
component Controller as control
component "Flight Tracker" as tracker
component "System Monitor" as monitor
component "Static Map" as map
component "Aircraft Info" as info
component Simulator as sim

interface "Flight Data" as data
interface "Weather Data" as weatherData
interface "Pilot Messages" as msg

control --> tracker : Get flight data
control --> weather : Get weather data for map
control --> map : Get static map elements for controller map
control --> info : Get aircraft info to display
tracker --> weather : Get weather info for flight planning
tracker --> map : Get static hazards for flight planning
tracker --> info : Get aircraft properties for flight planning
control <-> comms : Send and receive pilot messages
monitor ..> comms
monitor <--> admin : Administrator UI displays system health.
monitor ..> weather
monitor <--> control : Controller UI displays system health.
monitor ..> tracker
monitor ..> map
monitor ..> info
admin <--> control : Administrator configures controllers.\nController module receives configuration information from Administrator.
comms --> sim
weather --> sim
tracker --> sim
map --> sim
comms <--> sim
control ..> sim : Sends controller actions to simulator
admin ..> sim : Sends administive actions to simulator

tracker ---> data
weather --> weatherData
comms <----> msg

@enduml
```

### System Monitor
The System Monitor is responsible for monitoring the health of all modules in the NGATC system. The system monitor consumes the health of each module in the NGATC and produces an interface for modules (the Administrator and Controller, although there is nothing to preclude other modules from consuming this interface) to gain information about the NGATC system health.

### Administrator
The Adminstrator module is responsible for managing the NGATC at a "macro" level. It exposes a GUI to system administrators, allowing them to assign supervisors and controllers. Additionally, it allows administrators to configure airspace sectors and view workload information for these sectors.

### Controller
The Controller module is the primary interface that flight controllers will directly interact with. This module exposes a GUI to flight controllers that shows aircraft light data, weather, map information and aircraft type information. The Controller module consumes this information from the Flight Tracker, Static Map, Aircraft Info and Weather subsystems. This module allows bidirectional communication between pilots and controllers, as well as between multiple controllers. It also provides alert information to controllers, as well as AI generated suggestions.

### Pilot Communicator
The Pilot Communicator module is responsible for handling encrypted communications between pilots and flight controllers.

### Flight Tracker
The Flight Tracker module is a safety critical module responsible for consuming all data related to aircraft flights and providing this information to the Controller module. In addition to consuming data, the Flight Tracker module has the responsibility to detect aircraft conflicts and any other unsafe conditions, and to respond appropriately. The module also uses an AI agent to make adjustments to flight plans, using safety critical code to detect for any hazards.

As the safety critical module of the NGATC, the Flight Tracker module shall be developed in compliance with [Systematic Capability 3](https://www.exida.com/blog/Back-to-Basics-14-Systematic-Capability) as defined in [IEC 61508-3](https://www.exida.com/Blog/back-to-basics-06-iec-61508).

### Aircraft Info
The Aircraft Info module is responsible for managing information related to aircraft types, such as their maximum and minimum speeds, ceilings and fuel capacity. This information is consumed by the Flight Tracker module. This information is also exposed in a GUI and can be configured through this GUI by administrators.

### Weather
The Weather module is responsible for ingesting weather reports and providing this information to the Flight Tracker.

### Static Map
The Static Map module is responsible for managing the airspace map, including static hazards (e.g. mountains and tall buildings), restricted airspace and landmarks (e.g. airports). This information is consumed by the Flight Tracker module. This information is also exposed in a GUI and can be configured through this GUI by administrators.

### Simulator
The Simulator module is responsible for providing mock data to the modules of the NGATC, as well as consuming information from the NGATC to inform this mock data. Furthermore, the Simulator module will report to the modules that are consuming it whether the system is in production or simulated mode. An adminstator can interact with the Simulator module through a command line interface to enable and disable simulation mode.