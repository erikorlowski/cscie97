# Housemate Model Service Design Document
__Date:__ 09/22/2025
__Author:__ Erik Orlowski
__Reviewers:__ Gaston Tourn, Kyriaki Avgerinou

## Introduction
This document outlines the requirements and designs for the Housemates Model Service.

## Overview
The Housemate Model Service is used to model houses, rooms, occupants, sensors and appliances for Housemate enabled homes. The model service provides the ability to configure, update and view these objects. The model service also exposes a command line interface (CLI) that allows users to interact with the model service.

## Requirements

### Objects
__Requirement: House Model__
A House shall model an individual House. A House shall include the following pieces of information:
* A globally unique name
* An address
* Zero or more occupants
* One or more rooms
* Zero or more devices (appliances or sensors)
* The current energy consumption of the House in Watts

__Requirement: House Name Uniqueness__
Any attempt to create or modify a House such that two or more Houses would have duplicate names shall be rejected.

__Requirement: Room Model__
A Room shall model an individual Room. Each Room will be associated with one and only one House. A Room shall contain the following pieces of information:
* The type of Room
* The floor of the House the Room is located in
* A unique name of the Room within the House
* The number of windows
* The current aggregate energy consumption of all the Appliances in the Room that are turned on, in Watts.
* The Occupants known to be in the Room

__Requirement: Room Name Uniqueness__
Any attempt to create or modify a Room such that two or more Rooms within a House would have duplicate names shall be rejected.

__Requirement: Occupant Model__
An occupant shall model an adult, child or animal. An Occupant shall contain the following pieces of information:
* The type of Occupant (adult, child, animal)
* Whether the Occupant is known (e.g. family, friend) or unknown (e.g. guest, burglar)
* A globally unique name
* A status (active or sleeping)

__Requirement: Occupant Name Uniqueness__
Any attempt to create or modify an Occupant such that two or more Occupants would have duplicate names shall be rejected.
_Rationale:_ A single Occupant could be associated with multiple Houses, so to reliably identify a given occupant, a globally unique name is required.

__Requirement: Device Model__
A Device is a Sensor or Appliance that can be controlled and/or monitored by the Housemate system. A device shall include the following pieces of information:
* Unique identifier within a Room
* A Status (e.g. active or idle)
* A Value (e.g. a temperature reading)
* Room
* Device type (e.g. humidity sensor, thermostat, television)

__Requirement: Device Name Uniqueness__
Any attempt to create or modify a Device such that two or more Devices within a Room would have duplicate names shall be rejected.
_Rationale:_ All specified CLI commands that interact with a specific Device specify the Room, so a Device name only needs to be unique within a specific Room.

__Requirement: Appliance Control__
Appliances are a specific type of Device. In addition to the properties and attributes of all Devices, appliances shall have the ability to be controlled to perform one or more actions.

__Requirement: Appliance Energy Consumption__
Appliances shall track their current energy consumption in Watts.

### API
The Housemates Model service exposes an Application Programmer Interface (API) that allows objects to be configured, viewed and controlled.

__Requirement: API Call Auth_Tokens__
All API calls shall require an authorization token (auth_token) to be provided. This will be used in a future release.

__Requirement: Command Line Interface__
The Housemate Model Service shall expose a Command Line Interface (CLI) that will enable used to specify a file of specified commands to interact with the Housemate Model Service.

__Requirement: Define House Command__
A CLI command to define a house shall be defined with the following syntax:
```define house <house_name> address <address>```
When executed, this command shall create a new House with the given name and given address.

__Requirement: Define Room Command__
A CLI command to define a Room shall be defined with the following syntax:
```define room <room_name> floor <floor> type <room_type> house <house_name> windows <window_count>```
When executed, this command shall create a new Room with the given name, floor, room type, window count. The Room will be associated with the specified House.

__Requirement: Define Occupant Command__
A CLI command to define an Occupant shall be defined with the following syntax:
```define occupant <occupant_name> type <occupant_type>```
When executed, this command shall create a new occupant with the given name and type.

__Requirement: Add Occupant to House Command__
A CLI command to add an Occupant to a House shall be defined with the following syntax:
```add occupant <occupant_name> to_house <house_name>```
When executed, this command shall associate the specified Occupant with the specified House.

__Requirement: Define Sensor Command__
A CLI command to define a Sensor shall be defined with the following syntax:
```define sensor <name> type <sensor_type> room <house_name>:<room_name>```
When executed, this command shall create a new Sensor with the given name and type, and associate it with the specified House and Room.

__Requirement: Define Appliance Command__
A CLI command to define an Appliance shall be defined with the following syntax:
```define appliance <name> type <sensor_type> room <house_name>:<room_name> energy-use <energy-use>```
When executed, this command shall create a new Appliance with the given name and type. The appliance shall be associated with the specified House and Room. The energy use of the Appliance when turned on shall also be configured as specified.

__Requirement: Set Device Value Command__
A CLI command to set a Device value shall be defined with the following syntax:
```set sensor|appliance <house_name>:<room_name>:<name> status <status_name> value <value>```
When executed, this command shall set the specified Device to the given status and value.

__Requirement: Set Device Status Command__
A CLI command to set a Device status shall be defined with the following syntax:
```show sensor|appliance <house_name>:<room_name>:<name> status <status>```
When executed, this command shall display the specified Device's value and set the Device to the given status.

__Requirement: Show Device Command__
A CLI command to show a Device's value and status shall be defined with the following syntax:
```show sensor|appliance <house_name>:<room_name>:<name>```
When executed, this command shall display the specified Device's status and value.

__Requirement: Show Configuration Command__
A CLI command to show the configuration of all Houses shall be defined with the following syntax:
```show configuration```
When executed, this command shall display a list of all Houses with their names, addresses, number of Rooms and a list of Occupant names.

__Requirement: Show House Configuration Command__
A CLI command to show the configuration of a House shall be defined with the following syntax:
```show configuration <house_name>```
When executed, this command shall display the House's name, address, the number of Rooms and a list of Occupant names.

__Requirement: Show Room Configuration Command__
A CLI command to show the configuration of a Room shall be defined with the following syntax:
```show configuration <house_name>:<room_name>```
When executed, this command shall display the House name, Room name, the Room type, the number of windows in the room and a list of the Devices in the room with each Device's name.

__Requirement: Show Appliance Configuration Command__
A CLI command to show the configuration of an Appliance shall be defined with the following syntax:
```show configuration <house_name>:<room_name>:<appliance>```
When executed, this command shall display the House name, Room name, Appliance name, Appliance type, Appliance status and the energy usage of the device when turned on.

__Requirement: Show Energy Useage of All Houses Command__
A CLI command to show the energy useage for all Houses shall be defined with the following syntax:
```show energy-use```
When executed, this command shall display the name of each House along with its current energy consumption in Watts.

__Requirement: Show Energy Useage of House Command__
A CLI command to show the energy useage of a specific House shall be defined with the following syntax:
```show energy-use <house_name>```
When executed, this command shall display the current energy consumption of the House in Watts.

__Requirement: Show Energy Useage of Room Command__
A CLI command to show the energy useage of a specific Room shall be defined with the following syntax:
```show energy-use <house_name>:<room_name>```
When executed, this command shall display the current energy consumption of the Room in Watts.

__Requirement: Show Energy Useage of Appliance Command__
A CLI command to show the energy useage of a specific Appliance shall be defined with the following syntax:
```show energy-use <house_name>:<room_name>:<appliance>```
When executed, this command shall display the current energy consumption of the Application in Watts.

### Script Execution
__Requirement: Script Validation__
Before executing a command script, the following elements of the script shall be validated:
* The syntax of each command
* The uniqueness property of each name
* The existence of each specified object

If any of these validation checks fail, the script shall not execute and an informative error message shall be displayed to the user.

__Requirement: Script Confirmation__
After successfully executing a command script, a message shall be displayed to the user indicating that the script ran successfully.