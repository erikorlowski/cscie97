# Housemate Controller Service Design Document
__Date:__ 10/14/2025
__Author:__ Erik Orlowski
__Reviewers:__ Abderrahmane Fadli, Ian Mariner

## Introduction
The document captures the requirements, design and test plan for the Housemate Controller Service.

## Overview
The Housemate Controller Service receives input on the state of objects from the Housemate Model Service. Based on a defined set of rules, the Housemate Controller Service uses the state of objects in the Housemate Model Service to perform certain actions, again, utilizing the Housemate Model Service. The Housemate Controller Service also tracks the location of Occupants using a KnowledgeMap and uses this information to perform actions on the Housemate Model Service.

## Requirements
__Requirement: Rule Execution Logging__
All rule executions and resulting actions shalled by logged.

### Rules
This section defines the rules for specific behaviors of the Housemate Controller Service. In each case, when the given stimulus occurs, the states action shall be taken.

When text is given in parentheses in the stimulus column, this is the exact text that will be passed to the CLI for stimuli that don't directly map to the Housemate Model Service.

__Requirement: Exception Handling__
When one of the rules below cannot be executed, either because it is specified with incorrect syntax or because a referenced object cannot be found, script execution shall be stopped and the invalid command shall not be executed.

__Requirement: Ava Device Rules__
The following rules shall be enforced when stimuli of a voice command in the specified form is encountered on a Device with the type of "Ava":

| Stimulus | Action |
|-|-|
| open door (ava <full_qualified_ava_name> open door) | Sets the "open" status of all Devices of type "door" to in the same Room as the Ava Device to "opened". |
| close door (ava <full_qualified_ava_name> close door) | Sets the "open" status of all Devices of type "door" in the same Room as the Ava Device to "closed". |
| lights off (ava <full_qualified_ava_name> lights off) | Sets the "power" status of all Devices of type "light" in the same Room as the Ava Device to "ON". |
| lights on (ava <full_qualified_ava_name> lights on) | Sets the "power" status of all Devices of type "light" in the same Room as the Ava Device to "ON". |
| <appliance_type> <status_name> <value> (ava <room_fully_qualified_name> type <appliance_type> status <status_name> value <value>) | Sets the given status of all Devices of the specified type in the same Room as the Ava Device to the specified value. |
| where is <occupant_name> (ava where is <occupant_name>) | Return a response in the form of "<occupant_name> is_located_in <room_name>". |

__Requirement: Camera Rules__
The following rules shall be enforced when the described action is detected by a Device of type "Camera":

| Stimulus | Action |
|-|-|
| Occupant Detected (occupant <occupant_name> in_room <room_fully_qualified_name>) | Sets the "power" status of all Devices of type "light" in the Room to "ON". Sets the "setpoint" status of all Devices of type "thermostat" in the Room to 2 degrees warmer if this is the only Occupant in the Room. Update the location of the Occupant with the current Room. |
| Occupant Leaving (occupant <occupant_name> leaving_room <room_fully_qualified_name>) | Sets the "power" status of all Devices of type "light" in the Room to "OFF". Sets the "setpoint" status of all Devices of type "thermostat" in the Room to 2 degrees cooler if there are no more Occupants remaining in the Room. Update the location of the Occupant. |
| Occupant Inactive (occupant <occupant_name> inactive) | Tracks that the Occupant is sleeping. |
| Occupant Active (occupant <occupant_name> active) | Tracks that the Occupant is awake. |

__Requirement: Smoke Detector Rule__
The following rule shall be enforced when the described action is detected by a Device of type "SmokeDetector":

| Stimulus | Action |
|-|-|
| The "fire" status is set to a value of "active". | Sets the "power" status of all Devices of type "light" in the Room to "ON". Send text to speech to all Devices of type "Ava" in the House: “Fire in the <room_name>, please leave the house immediately”. The Housemate ControllerService initiates a 911 call. |

__Requirement: Refrigerator Rule__
The following rule shall be enforced when the described action is detected by a Device of type "Refrigerator":

| Stimulus | Action |
|-|-|
| The value of the "beer_count" status changes. | If the new beer count is less than 3, the Occupant is prompted if they would like to order more beers. If the Occupant responds "yes", the Housemate Controller Service sends an email requesting more beer. |

## Use Cases
The Housemate Controller Service works with the Housemate Model Service and the KnowledgeGraph to events that occur in the Housemate system. The Housemate Model Service is responsible for creating, configuring and managing Housemate Model Objects. Housemate Controller Service is responsible for interpreting events and performing actions based on those events.

```plantuml
@startuml
actor Occupant
actor Device

rectangle ControllerService as "Housemate Controller Service" {
    usecase RunCommand as "Execute Command"
}

rectangle Service as "Housemate Model Service" {
    usecase RunScript as "Run API Services"
    usecase Define as "Define Component"
    usecase Set as "Modify Component"
    usecase View as "View Component"
    actor Models as "Housemate Model Objects"
}

Occupant -- RunCommand
Device -- RunCommand
RunCommand -- RunScript
RunScript <.. Define : <<Extends>>
RunScript <.. Set  : <<Extends>>
RunScript <.. View : <<Extends>>
Models -up- Define
Models -up- Set
Models -up- View
@enduml
```

This diagram depicts how an Occupant or Device has its actions or inputs interpreted by the Housemate Controller Service to execute a command. The Housemate Controller Service then calls on the Housemate Model Service to update Model Objects based on the result of the command.

## Implementation
A high level view of the use of the Housemate Model Service and Housemate Controller Service is shown below.

Activities colored in pink are the responsibility of the Housemate Controller Service.

```plantuml
@startuml
start
:Command Script Entered;
repeat :TestDriver Interprets Line;
    if (Line Is Model Service Command) then (yes)
        if (Error) then
            :Display Error Message;
            stop
        endif
        :Housemate Model Service Executes Command;
        #Pink:if (Device Status Change) then (yes)
            #Pink:Notify Status Observers;
        else (no)
        endif
        #Pink:if (Occupant Defined) then (yes)
            #Pink:Set Occupant Location to Unknown;
        else (no)
        endif
    elseif (Line is Controller Service Command) then (yes)
        #Pink:if (Error) then
            #Pink:Display Error Message;
            stop
        endif
        #Pink:CommandFactory Chooses Correct Command Class;
        #Pink:Command is Created;
        #Pink:Command is Executed;
        :Housemate Model Service Responds to API Call(s);
    else (no)
        :Display Error Message;
        stop
    endif
repeat while (Last Line?) is (no)
->yes;
stop
@enduml
```

When a command is run by the Housemate Model Service, the output of that command is then sent to the Housemate Controller Service to process it.

If a Smoke Detector or Refrigerator are created, then they must register to observe status changes in order to respond to fires or changes in the beer counts respectively.

If a Device status changes, then all registered observers are notified with details of the new status and these observers can decide if a Command needs to be executed in response to the new status.

If an Occupant is defined, the Housemate Controller Service's KnowledgeGraph needs to be updated with the new Occupant and an unknown location.

When a command is run directly by the Housemate Controller Service, first, the command is validated. If the line is validated, the type of Command class is classified. Then, the Command is Executed.

Many Command executions require calling the Housemate Model Service API to make necessary updates to Model Objects.

## Class Diagram
The class diagram for the Housemate Controller Service is shown below.

```plantuml
@startuml
scale max 800 width
set separator :
top to bottom direction

package cscie97.asn2.housemate.controller {
    interface ControllerServiceApi {
        + executeCommand(in commandText: String) : String
    }

    class ControllerServiceApiImpl << (S,#FF7700) Singleton >> {
        
    }

    class OccupantTracker << (S,#FF7700) Singleton >> {
        - occupantKnowledgeGraph : KnowledgeGraph
        + addOccupantToRoom(in occupantName: String, in fullyQualifiedRoomName: String) : void
        + removeOccupantFromRoom(in occupantName: String, in fullyQualifiedRoomName: String) : void
        + makeOccupantActive(in occupantName: String) : void
        + makeOccupantInactive(in occupantName: String) : void
    }

    interface StatusObserver {
        + onStatusUpdate(in device: String, in status: String, in newValue: String) : void
    }

    class FireObserver {
    }

    class BeerCountObserver {
    }

    class OvenDoneObserver {

    }

    class CommandFactory << (S,#FF7700) Singleton >> {
        + getCommand(in commandText: String) : Command
    }

    interface Command {
        + execute() : String
    }

    class ApplicationTypeCommand {
        - fullyQualifiedContainerName : String
        - applianceType : String
        - statusName : String
        - newValue : String
        + ApplicationTypeCommand(in fullyQualifiedContainerName: String,\nin applianceType: String, in statusName: String, in newValue: String)
    }

    class OccupantRoomCommand {
        - occupantName : String
        - fullyQualifiedRoomName : String
        - isEntering : bool
        + OccupantRoomCommand(in occupantName: String, in fullyQualifiedRoomName: String, in isEntering: boolean)
    }

    class OccupantStatusCommand {
        - occupantName : String
        - isActive : bool
        + OccupantStatusCommand(in occupantName: String, in isActive: boolean)
    }

    class BeerNotificationCommand {
        - fullyQualifiedRefrigeratorName : String
        - beerCount : int
        + BeerNotificationCommand(in fullyQualifiedRefrigeratorName: String, in beerCount: int)
    }

    class FireCommand {
        - fullyQualifiedSmokeDetectorName : String
        + FireCommand(in fullyQualifiedSmokeDetectorName: String)
    }

    class OvenDoneCommand {
        - fullyQualifiedOvenName : String
        + OvenDoneCommand(in fullyQualifiedOvenName: String)
    }

    ControllerServiceApi <-- ControllerServiceApiImpl
    ControllerServiceApiImpl "1" o-- "*" StatusObserver
    StatusObserver <-- FireObserver
    StatusObserver <--- BeerCountObserver
    StatusObserver <-- OvenDoneObserver
    Command <-- ApplicationTypeCommand
    Command <--- OccupantRoomCommand
    Command <--- OccupantStatusCommand
    Command <--- BeerNotificationCommand
    Command <-- FireCommand
    Command <- OvenDoneCommand
    CommandFactory ..> Command
    ControllerServiceApiImpl ..> CommandFactory
    OccupantRoomCommand ....> OccupantTracker
    OccupantStatusCommand ..> OccupantTracker
    FireObserver ..> FireCommand
    BeerCountObserver ..> BeerNotificationCommand
    OvenDoneObserver ..> OvenDoneCommand
}

package cscie97.asn2.housemate.model {
    interface ModelServiceApi {
        - statusObservers : ArrayList<StatusObserver>
        + executeCommand(in commandText: String) : String
        + attachStatusObserver(in observer : StatusObserver) : void
        + notifyStatusObservers(in device: String, in status: String, in newValue: String) : void
    }
}

package cscie97.asn1.knowledge.engine{
    class KnowledgeGraph {
        + importTriple(in subject : String, in predicate String, in object String) : void
        + executeQuery(in subject : String, in predicate String, in object String) : Set<Triple>
    }

    class Triple {
        - subject : Node
        - predicate : Predicate
        - object : Node
    }

    class Node {
        - identifier : String
    }

    class Predicate {
        -identifier : String
    }

    KnowledgeGraph ..> Triple
    Triple ..> Node
    Triple ..> Predicate
}

Command ..down.....> ModelServiceApi
OccupantTracker ..> KnowledgeGraph
ModelServiceApi "1" o-- "*" StatusObserver



@enduml
```

The ControllerServiceApi is the interface through which the Housemate Controller Service is interacted with. It is implemented by the ControllerServiceApiImpl which provides the top level functionality for the service. The primary way the ControllerServiceApiImpl class performs actions is through the CommandFactory class.

The CommandFactory class is a singleton class that gets a specific command instance based on command text input into its getCommand method.

Other Commands, such as the FireCommand and the BeerNotificationCommand are triggered by actions in the ModelServiceApi. To enable this notification, the observer pattern is used, with a BeerCountObserver and FireObserver registering for specific events.

Commands dealing with Occupants interact with the OccupantTracker singleton class. This class is a wrapper around the KnowledgeGraph, providing an easy way for Commands to gain information about Occupant state.

## Class Dictionary
### ControllerServiceApi
The ControllerServiceApi is the public interface that other packages use to interact with the Housemate Controller Service.

__Methods:__
| Method Name | Signature | Description |
|---|---|---|
| executeCommand | String executeCommand(String commandText) | Executes a command specific to the Housemate Controller Service. The output of the Command is returned. |

### ControllerServiceApiImpl
The ControllerServiceApiImpl is the concrete implementation of the ControllerServiceApi and provides the implementation of the executeCommand method.

### StatusObserver
The StatusObserver interface is used to receive updates from the Housemate Model Service when a Device has changed its status. Concrete implementations of this interface look for specific status changes and perform Commands based on these changes.

__Methods:__
| Method Name | Signature | Description |
|---|---|---|
| onStatusUpdate | void onStatusUpdate(String device, String status, String newValue) | Receives a status update event from the HousemateModelService. A concrete implementation of this interface can use the arguments of this method to perform meaningful actions. |

### FireObserver
The FireObserver is a concrete implementation of the StatusObserver interface which looks for smoke detectors indicating a fire.

__Methods:__
| Method Name | Signature | Description |
|---|---|---|
| onStatusUpdate | void onStatusUpdate(String device, String status, String newValue) | If the status update is a smoke detector indicating a fire, calls the FireCommand execute method to respond to the fire. |

### BeerCountObserver
The BeerCountObserver is a concrete implementation of the StatusObserver interface which looks for refrigerators indicating a beer count of less than 3.

__Methods:__
| Method Name | Signature | Description |
|---|---|---|
| onStatusUpdate | void onStatusUpdate(String device, String status, String newValue) | If the status update is a refrigerator indicating a beer count of less than 3, prompts the user if they would like to order more beer and places the order if necessary. |

### OvenDoneObserver
The OvenDoneObserver is a concrete implementation of the StatusObserver interface which looks for ovens indicating a time to cook of 0.

__Methods:__
| Method Name | Signature | Description |
|---|---|---|
| onStatusUpdate | void onStatusUpdate(String device, String status, String newValue) | If the status update is an oven indicating a time to cook of 0, turn the oven off and have all Ava devices in the room indicate that food is ready. |

### Command
The Command interface represents some action that can be taken soon or long after when it is created. Concrete implementations of this interface will use the execute method to perform relevant behaviors for the Housemate Controller Service.

__Methods:__
| Method Name | Signature | Description |
|---|---|---|
| execute | String execute() | Performs the relevant actions for a specific type of Command. The output of the Command execution is returned. |

### ApplicationTypeCommand
This is a generic Command that sets the statuses all devices of a given type in a given room or house to a given value.

__Methods:__
| Method Name | Signature | Description |
|---|---|---|
| ApplicationTypeCommand | ApplicationTypeCommand(String fullyQualifiedContainerName,
String applianceType, String statusName, String newValue) | Creates a new ApplicationTypeCommand with the provided information. |

__Properties:__
| Property Name | Type | Description |
|---|---|---|
| fullyQualifiedContainerName | String | The fully qualified name of the Room or House. |
| applianceType | String | The type of Appliance to set statuses for. |
| statusName | String | The name of the status to set. |
| newValue | String | The value to set the status to. |

### FireCommand
This Command performs all the actions necessary to respond to a fire as described in the requirements.

__Methods:__
| Method Name | Signature | Description |
|---|---|---|
| FireCommand | FireCommand(String fullyQualifiedSmokeDetectorName) | Creates a new FireCommand with the provided information. |

__Properties:__
| Property Name | Type | Description |
|---|---|---|
| fullyQualifiedSmokeDetectorName | String | The fully qualified name of the smoke detector indicating the fire. This can be used to find the house and room of the fire. |

### OvenDoneCommand
This Command indicates when an oven is done cooking.

__Methods:__
| Method Name | Signature | Description |
|---|---|---|
| OvenDoneCommand | OvenDoneCommand(String fullyQualifiedOvenName) | Creates a new OvenDoneCommand with the provided information. |

__Properties:__
| Property Name | Type | Description |
|---|---|---|
| fullyQualifiedOvenName | String | The fully qualified name of the oven that is finished cooking. |

### BeerNotificationCommand
This Command indicates when a refrigerator is low on beer.

__Methods:__
| Method Name | Signature | Description |
|---|---|---|
| BeerNotificationCommand | BeerNotificationCommand(String fullyQualifiedRefrigeratorName, int beerCount) | Creates a new BeerNotificationCommand with the provided information. |

__Properties:__
| Property Name | Type | Description |
|---|---|---|
| fullyQualifiedRefrigeratorName | String | The fully qualified name of the refrigerator that is low on beer. |
| beerCount | int | The current amount of beer in the refrigerator. |

### OccupantRoomCommand
This Command handles an Occupant entering or leaving a Room.

__Methods:__
| Method Name | Signature | Description |
|---|---|---|
| OccupantRoomCommand | OccupantRoomCommand(String occupantName, String fullyQualifiedRoomName, bool isEntering) | Creates a new OccupantRoomCommand with the provided information. |

__Properties:__
| Property Name | Type | Description |
|---|---|---|
| occupantName | String | The name of the Occupant entering or leaving the Room. |
| fullyQualifiedRoomName | String | The fully qualified name of the Room the Occupant is entering or leaving. |
| isEntering | bool | Whether the Occupant is entering or leaving the Room. |

### OccupantStatusCommand
This Command handles an Occupant entering or leaving a Room.

__Methods:__
| Method Name | Signature | Description |
|---|---|---|
| OccupantStatusCommand | OccupantStatusCommand(String occupantName, bool isActive) | Creates a new OccupantStatusCommand with the provided information. |

__Properties:__
| Property Name | Type | Description |
|---|---|---|
| occupantName | String | The name of the Occupant to update the status of. |
| isActive | bool | Whether the Occupant is awake or sleeping. |

### OccupantTracker
The OccupantTracker is a wrapper around the KnowledgeGraph used to allow Commands to easily gain information about the status of Occupants in the Housemate system.

__Methods:__
| Method Name | Signature | Description |
|---|---|---|
| addOccupantToRoom | addOccupantToRoom(String occupantName, String fullyQualifiedRoomName) | Adds the Occupant with a given name to the specified Room. |
| removeOccupantFromRoom | addOccupantToRoom(String occupantName, String fullyQualifiedRoomName) | Removes the Occupant with a given name from the specified Room. If the Occupant was not present, do nothing. |
| makeOccupantActive | makeOccupantActive(String occupantName) | Marks the specified Occupant as active. |
| makeOccupantInactive | makeOccupantInactive(String occupantName) | Marks the specified Occupant as inactive. |

## Implementation Details
Further details about the implementation of the Housemate Controller Service can be seen in the sequence diagrams below.

The following diagram shows details of how the Housemate Controller Service executes a command from an Ava device:

```plantuml
@startuml
title Using Ava Device to Turn on Lights

autonumber
scale max 800 width

actor User
participant TestDriver
participant ModelServiceApi
participant ControllerServiceApiImpl
participant CommandFactory
participant "ApplicationTypeCommand :\nlightsOnCommand" as ApplicationTypeCommand

User -> TestDriver : main()
note right
    Script file name passed in Command Line argument.
end note
loop for each creation command
TestDriver -> ModelServiceApi : executeCommand(commandText)
end
TestDriver -> ControllerServiceApiImpl : executeCommand(commandText)
ControllerServiceApiImpl -> CommandFactory : getCommand(commandText)
CommandFactory -> ApplicationTypeCommand : ApplicationTypeCommand(\n"house1:kitchen","lights", "power", "ON")
ApplicationTypeCommand -> ModelServiceApi : executeCommand("set appliance house1:kitchen:overheadLights status power value ON")
ApplicationTypeCommand -> ModelServiceApi : executeCommand("set appliance house1:kitchen:breakfastBarLights status power value ON")

@enduml
```

Here, a number of commands are executed by the Housemate Model Service to create and configure objects. Eventually, an Ava voice command such as __(3)__ will be received by the Controller Service. From here, the Controller Service classifies the command, interprets its details and uses the Model Service to perform relevant actions.

A different sequence is seen when the Controller Service must respond to status changes on Devices inside the Housemate Model Service. This sequence is shown below:

```plantuml
@startuml
title Responding to Fire

autonumber
scale max 800 width

actor User
participant TestDriver
participant ModelServiceApi
participant "FireCommand :\nfireCommand" as FireCommand

User -> TestDriver : main()
note right
    Script file name passed in Command Line argument.
end note
FireObserver -> ModelServiceApi : attachStatusOberver()
loop for each creation command
    TestDriver -> ModelServiceApi : executeCommand(commandText)
end
ModelServiceApi -> ModelServiceApi : notifyStatusObservers("house1:room1:fireDetector", "fire", "active")
ModelServiceApi -> FireObserver : onStatusUpdate("house1:room1:fireDetector", "fire", "active")
FireObserver -> FireCommand : FireCommand("house1:room1:fireDetector")
FireCommand -> ModelServiceApi : Perform actions for fire.

@enduml
```

Responding to a fire relies on using the observer pattern to alert the Controller Service when a fire has occurred. Once a fire occurs, the business logic for handling the fire is the responsibility of the FireCommand class. The FireCommand class then delegates specific actions to the ModelServiceApi class.

## Exception Handling
There are two types of exceptions that could occur in the Housemate Controller Service.

One type of exception would be if a command was formatted improperly or otherwise could not be parsed.

This type of exception is handled by an __InvalidCommandException__. The properties of this exception are the command text and a message indicating the problem with the command text.

Another type of exception occurs when an object in the Housemate Model Service is referenced that cannot be found. This type of exception is handled by an __ObjectNotFoundException__. The only property of this exception is the specified name of the object that could not be found.

## Risks
The current implementation of the observer pattern could send many updates to the controller service on sensor updates which are not needed. This could potentially increase processor load and make the system less responsive. Because there is nothing in this system that is time critical in intervals of less than seconds, this is likely an acceptable risk.