package cscie97.asn4.housemate.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cscie97.asn4.housemate.entitlement.EntitlementException;
import cscie97.asn4.housemate.entitlement.EntitlementServiceApi;

/**
 * Parses a single line of the housemate script and dispatches to the appropriate
 * handler for commands such as "define", "add occupant", "set", and "show".
 * The parser performs simple pattern matching and delegates creation/modification
 * operations to the ModelServiceApiImpl.
 */
class CommandParser {
    private String scriptLineText;

    private static CommandParser instance = null;

    private CommandParser() {
        // Private constructor to prevent instantiation
    }

    /**
     * Get the singleton instance of the CommandParser.
     *
     * @return the singleton CommandParser instance
     */
    public static CommandParser getInstance() {
        if (instance == null) {
            instance = new CommandParser();
        }
        return instance;
    }

    /**
     * Construct a parser for a single script line and execute the matching command.
     *
     * @param scriptLineText the raw text of a single script line; may contain leading/trailing whitespace
     * @throws IllegalArgumentException if the script line is null, unrecognized, or invalid for a matched command
     */
    /**
     * Parse and execute a single script line. For commands that produce output (show ...)
     * the generated output is returned as a String. For commands that only mutate state
     * the method returns null.
     *
     * @param scriptLineText the single script line to execute
     * @return the output produced by the command, or null if none
     */
    String executeCommand(String scriptLineText, long accessToken) {

        if (scriptLineText != null && !scriptLineText.isEmpty() && (scriptLineText.charAt(0) == '\uFEFF' || (int) scriptLineText.charAt(0) == 239)) {
            // Remove BOM if present
            scriptLineText = scriptLineText.substring(1);
        }

        this.scriptLineText = scriptLineText;

        // If the line starts with "define" (allow leading whitespace, case-insensitive),
        // capture the remaining text and pass it to define(...)
        if (scriptLineText != null) {
            if(scriptLineText.trim().isEmpty() || scriptLineText.trim().startsWith("#")
                || scriptLineText.trim().startsWith("_") || scriptLineText.trim().startsWith("-")
                || scriptLineText.trim().startsWith("*")) {
                // Bypass empty line or comment
                return null;
            }
            
            Pattern p = Pattern.compile("^\\s*define\\b(.*)$", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(scriptLineText);
            if (m.find()) {
                String remaining = m.group(1).trim();
                define(remaining);
                return null;
            }

            p = Pattern.compile("^\\s*add\\s*occupant\\b(.*)$", Pattern.CASE_INSENSITIVE);
            m = p.matcher(scriptLineText);
            if (m.find()) {
                String remaining = m.group(1).trim();
                addOccupantToHouse(remaining);
                return null;
            }

            p = Pattern.compile("^\\s*set\\s*(sensor|appliance)\\b(.*)$", Pattern.CASE_INSENSITIVE);
            m = p.matcher(scriptLineText);
            if (m.find()) {
                String remaining = m.group(2).trim();
                setValue(remaining, accessToken);
                return null;
            }

            p = Pattern.compile("^\\s*show\\s*sensor\\b(.*)$", Pattern.CASE_INSENSITIVE);
            m = p.matcher(scriptLineText);
            if (m.find()) {
                String remaining = m.group(1).trim();
                return showDevice(remaining, accessToken);
            }

            p = Pattern.compile("^\\s*show\\s*appliance\\b(.*)$", Pattern.CASE_INSENSITIVE);
            m = p.matcher(scriptLineText);
            if (m.find()) {
                String remaining = m.group(1).trim();
                return showDevice(remaining, accessToken);
            }

            p = Pattern.compile("^\\s*show\\s*configuration\\b(.*)$", Pattern.CASE_INSENSITIVE);
            m = p.matcher(scriptLineText);
            if (m.find()) {
                String remaining = m.group(1).trim();
                return showConfiguration(remaining, accessToken);
            }

            p = Pattern.compile("^\\s*show\\s*energy-use\\b(.*)$", Pattern.CASE_INSENSITIVE);
            m = p.matcher(scriptLineText);
            if (m.find()) {
                String remaining = m.group(1).trim();
                return showEnergyUse(remaining, accessToken);
            }

            throw new IllegalArgumentException("Unrecognized command: " + scriptLineText);
        } else {
            throw new IllegalArgumentException("Command cannot be null");
        }
    }

    /**
     * Handle "define ..." commands. Determines the specific define subtype
     * (house, room, occupant, sensor, appliance) and delegates to the corresponding
     * define* helper.
     *
     * @param remainingText the text following the initial "define" token
     * @throws IllegalArgumentException if the remainingText does not match any known define form
     */
    private void define(String remainingText) {
        remainingText = remainingText.trim();
        if (remainingText.toLowerCase().startsWith("house")) {
            defineHouse(remainingText);
        } else if (remainingText.toLowerCase().startsWith("room")) {
            defineRoom(remainingText);
        } else if (remainingText.toLowerCase().startsWith("occupant")) {
            defineOccupant(remainingText);
        } else if (remainingText.toLowerCase().startsWith("sensor")) {
            defineSensor(remainingText);
        } else if (remainingText.toLowerCase().startsWith("appliance")) {
            defineAppliance(remainingText);
        } else {
            // Unrecognized define command
            throw new IllegalArgumentException("Unrecognized define command: " + this.scriptLineText);
        }
    }

    /**
     * Handle "define house ..." commands. Expects the format:
     * "define house <name> address <address>". Creates a new House object
     * and adds it to the model.
     *
     * @param remainingText the text following the "define house" token
     * @throws IllegalArgumentException if the remainingText is invalid
     */
    private void defineHouse(String remainingText) {
        String name = null;
        String address = null;

        Pattern p = Pattern.compile("^house\\s*\\b(.*)\\s*address\\s*[\"]\\s*(.*)\\s*[\"]$", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(remainingText);
        if (m.find()) {
            name = m.group(1).trim();
            address = m.group(2).trim();

            House newHouse = new House("house_" + name, address);

            ModelServiceApiImpl.getInstance().addModelObject(newHouse);
        } else {
            throw new IllegalArgumentException("Invalid house definition: " + this.scriptLineText);
        }
    }

    /**
     * Handle "define room ..." commands.
     * Creates a new Room object and adds it to the model.
     *
     * @param remainingText the text following the "define room" token
     * @throws IllegalArgumentException if the remainingText is invalid
     */
    private void defineRoom(String remainingText) {
        String name = null;
        String floor = null;
        String type = null;
        int windows = 0;
        House house = null;

        Pattern p = Pattern.compile("^\\s*room\\s*\\b(.*) floor \\b(.*) type \\b(.*) house \\b(.*) windows \\b(\\d+)$", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(remainingText);
        if (m.find()) {
            name = m.group(1).trim();
            floor = m.group(2).trim();
            type = m.group(3).trim();
            house = (House) ModelServiceApiImpl.getInstance().getModelObject("house_" + m.group(4).trim());

            if (house == null) {
                throw new IllegalArgumentException("House not found for room definition: " + this.scriptLineText);
            }

            try {
                windows = Integer.parseInt(m.group(5).trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid windows number: " + scriptLineText);
            }

            Room newRoom = new Room(house.getFullyQualifiedName() + ":room_" + name, type, floor, windows);
            ModelServiceApiImpl.getInstance().addModelObject(newRoom);
            ModelServiceApiImpl.getInstance().addOwnership(house, newRoom);
        } else {
            throw new IllegalArgumentException("Invalid room definition: " + this.scriptLineText);
        }
    }

    /**
     * Handle "define occupant ..." commands. Creates a new Occupant object
     * and adds it to the model.
     *
     * @param remainingText the text following the "define occupant" token
     * @throws IllegalArgumentException if the remainingText is invalid
     */
    private void defineOccupant(String remainingText) {
        String name = null;
        String type = null;

        Pattern p = Pattern.compile("^occupant\\s*\\b(.*) type \\b(.*)$", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(remainingText);
        if (m.find()) {
            name = m.group(1).trim();
            type = m.group(2).trim();

            OccupantType occupantType;
            try {
                occupantType = OccupantType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid occupant type: " + scriptLineText);
            }

            Occupant newOccupant = new Occupant("occupant_" + name, occupantType);

            ModelServiceApiImpl.getInstance().addModelObject(newOccupant);

            try {
                EntitlementServiceApi.getInstance().executeCommand("create_user " + newOccupant.getName() + ", " + newOccupant.getName());
                EntitlementServiceApi.getInstance().executeCommand("add_user_credential " + newOccupant.getName() + ", voice_print, --" + newOccupant.getName() + "--");
            } catch (EntitlementException e) {
                throw new IllegalArgumentException("Failed to create Entitlement user for occupant at: " + this.scriptLineText + " Cause: " + e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("Invalid occupant definition: " + this.scriptLineText);
        }
    }

    /**
     * Handle "define sensor ..." commands. Creates a new Sensor object
     * and adds it to the model.
     *
     * @param remainingText the text following the "define sensor" token
     * @throws IllegalArgumentException if the remainingText is invalid
     */
    private void defineSensor(String remainingText) {
        String name = null;
        String type = null;
        House house = null;
        Room room = null;

        Pattern p = Pattern.compile("^sensor\\s*\\b(.*) type \\b(.*) room \\b(.*):\\b(.*)$", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(remainingText);
        if (m.find()) {
            name = m.group(1).trim();
            type = m.group(2).trim();
            house = (House) ModelServiceApiImpl.getInstance().getModelObject("house_" + m.group(3).trim());

            if (house == null) {
                throw new IllegalArgumentException("House not found for sensor definition: " + this.scriptLineText);
            }

            room = (Room) ModelServiceApiImpl.getInstance().getModelObject(house.getFullyQualifiedName() + ":room_" + m.group(4).trim());

            if (room == null) {
                throw new IllegalArgumentException("Room not found for sensor definition: " + this.scriptLineText);
            }

            Sensor newSensor = new Sensor(room.getFullyQualifiedName() + ":sensor_" + name, type);
            ModelServiceApiImpl.getInstance().addModelObject(newSensor);
            ModelServiceApiImpl.getInstance().addOwnership(room, newSensor);
        } else {
            throw new IllegalArgumentException("Invalid sensor definition: " + this.scriptLineText);
        }
    }

    /**
     * Handle "define appliance ..." commands. Creates a new Appliance object
     * and adds it to the model.
     *
     * @param remainingText the text following the "define appliance" token
     * @throws IllegalArgumentException if the remainingText is invalid
     */
    private void defineAppliance(String remainingText) {
        String name = null;
        String type = null;
        House house = null;
        Room room = null;
        double energyUseWhenOnWatts = 0.0;

        Pattern p = Pattern.compile("^appliance\\s*\\b(.*)\\s*type\\s*\\b(.*)\\s*room\\s*\\b(.*):(.*)\\s*energy-use\\s*\\b(.*)$", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(remainingText);
        if (m.find()) {
            name = m.group(1).trim();
            type = m.group(2).trim();
            house = (House) ModelServiceApiImpl.getInstance().getModelObject("house_" + m.group(3).trim());

            if (house == null) {
                throw new IllegalArgumentException("House not found for appliance definition: " + this.scriptLineText);
            }

            room = (Room) ModelServiceApiImpl.getInstance().getModelObject(house.getFullyQualifiedName() + ":room_" + m.group(4).trim());

            if (room == null) {
                throw new IllegalArgumentException("Room not found for appliance definition: " + this.scriptLineText);
            }

            try {
                energyUseWhenOnWatts = Double.parseDouble(m.group(5).trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid energy-use number: " + scriptLineText);
            }

            Appliance newAppliance = new Appliance(room.getFullyQualifiedName() + ":appliance_" + name, type, energyUseWhenOnWatts);
            ModelServiceApiImpl.getInstance().addModelObject(newAppliance);
            ModelServiceApiImpl.getInstance().addOwnership(room, newAppliance);
        } else {
            p = Pattern.compile("^appliance\\s*\\b(.*)\\s*type\\s*\\b(.*)\\s*room\\s*\\b(.*):(.*)$", Pattern.CASE_INSENSITIVE);
            m = p.matcher(remainingText);

            if (m.find()) {
                name = m.group(1).trim();
                type = m.group(2).trim();
                house = (House) ModelServiceApiImpl.getInstance().getModelObject("house_" + m.group(3).trim());
                room = (Room) ModelServiceApiImpl.getInstance().getModelObject(house.getFullyQualifiedName() + ":room_" + m.group(4).trim());

                if (room == null) {
                    throw new IllegalArgumentException("Room not found for appliance definition: " + this.scriptLineText);
                }

                Appliance newAppliance = new Appliance(room.getFullyQualifiedName() + ":appliance_" + name, type, 0.0);
                ModelServiceApiImpl.getInstance().addModelObject(newAppliance);
            } else {
                throw new IllegalArgumentException("Invalid appliance definition: " + this.scriptLineText);
            }
        }
    }

    /**
     * Handle "add occupant ..." commands to associate an occupant with a house.
     *
     * @param remainingText the text following the "add occupant" token
     * @throws IllegalArgumentException if parsing fails or referenced model objects are not found
     */
    private void addOccupantToHouse(String remainingText) {
        String occupantName = null;
        String houseName = null;

        Pattern p = Pattern.compile("^\\s*\\b(.*) to_house \\b(.*)$", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(remainingText);
        if (m.find()) {
            occupantName = m.group(1).trim();
            houseName = m.group(2).trim();

            Occupant occupant = (Occupant) ModelServiceApiImpl.getInstance().getModelObject("occupant_" + occupantName);
            if (occupant == null) {
                throw new IllegalArgumentException("Occupant not found: " + this.scriptLineText);
            }

            House house = (House) ModelServiceApiImpl.getInstance().getModelObject("house_" + houseName);
            if (house == null) {
                throw new IllegalArgumentException("House not found: " + this.scriptLineText);
            }

            ModelServiceApiImpl.getInstance().addOwnership(house, occupant);

            try {
                EntitlementServiceApi.getInstance().executeCommand("create_resource_role " + house.getName() + occupant.getType().toString() + ", " + occupant.getType().toString().toLowerCase() + "_role" + ", " + house.getName());
                EntitlementServiceApi.getInstance().executeCommand("add_resource_role_to_user " + occupant.getName() + ", " + house.getName() + occupant.getType().toString());
            } catch (EntitlementException e) {
                throw new IllegalArgumentException("Failed to create Entitlement user for occupant at: " + this.scriptLineText + " Cause: " + e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("Invalid add occupant command: " + this.scriptLineText);
        }
    }

    /**
     * Handle "set ..." commands to update a device status/value.
     *
     * @param remainingText the text following the "set" token
     * @param accessToken the authentication key for executing commands
     * @throws IllegalArgumentException if parsing fails, numbers are invalid, or referenced objects are not found
     */
    private void setValue(String remainingText, long accessToken) {
        String deviceName = null;
        String status = null;
        String roomName = null;
        String houseName = null;
        String value = null;
        Device device = null;

        Pattern p = Pattern.compile("^\\b(.*):(.*):(.*)\\s*status\\s*\\b(.*)\\s*value\\s*\\b(.*)$", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(remainingText);
        if (m.find()) {
            houseName = m.group(1).trim();
            roomName = m.group(2).trim();
            deviceName = m.group(3).trim();
            status = m.group(4).trim();
            value = m.group(5).trim();

            House house = (House) ModelServiceApiImpl.getInstance().getModelObject("house_" + houseName);
            if (house == null) {
                throw new IllegalArgumentException("House not found for set command: " + this.scriptLineText);
            }

            Room room = (Room) ModelServiceApiImpl.getInstance().getModelObject(house.getFullyQualifiedName() + ":room_" + roomName);
            if (room == null) {
                throw new IllegalArgumentException("Room not found for set command: " + this.scriptLineText);
            }

            device = null;
            device = (Device) ModelServiceApiImpl.getInstance().getModelObject(room.getFullyQualifiedName() + ":sensor_" + deviceName);
            if(device == null) {
                device = (Device) ModelServiceApiImpl.getInstance().getModelObject(room.getFullyQualifiedName() + ":appliance_" + deviceName);
            }

            if (device == null) {
                throw new IllegalArgumentException("Device not found for set command: " + this.scriptLineText);
            }

            try {
                String permissionName = "control_" + device.getType().toLowerCase();
                EntitlementServiceApi.getInstance().executeCommand("check_access " + Long.toString(accessToken) + ", " + permissionName +", " + ModelServiceApiImpl.getInstance().modelFullyQualifiedNameToControllerFullyQualifiedName(device.getFullyQualifiedName()));
            } catch (EntitlementException e) {
                System.out.println("Access denied: " + e.getMessage());
                return;
            }

            device.setStatus(status, value);
        } else {
            p = Pattern.compile("^\\b(.*):(.*):(.*)\\s*status\\s*\\b(.*)\\s*$", Pattern.CASE_INSENSITIVE);
            m = p.matcher(remainingText);

            if (m.find()) {
                houseName = m.group(1).trim();
                roomName = m.group(2).trim();
                deviceName = m.group(3).trim();
                status = m.group(4).trim();

                House house = (House) ModelServiceApiImpl.getInstance().getModelObject("house_" + houseName);
                if (house == null) {
                    throw new IllegalArgumentException("House not found for set command: " + this.scriptLineText);
                }

                Room room = (Room) ModelServiceApiImpl.getInstance().getModelObject(house.getFullyQualifiedName() + ":room_" + roomName);
                if (room == null) {
                    throw new IllegalArgumentException("Room not found for set command: " + this.scriptLineText);
                }

                device = null;
                device = (Device) ModelServiceApiImpl.getInstance().getModelObject(room.getFullyQualifiedName() + ":sensor_" + deviceName);
                if(device == null) {
                    device = (Device) ModelServiceApiImpl.getInstance().getModelObject(room.getFullyQualifiedName() + ":appliance_" + deviceName);
                }

                if (device == null) {
                    throw new IllegalArgumentException("Device not found for set command: " + this.scriptLineText);
                }
                
                try {
                    String permissionName = "control_" + device.getType().toLowerCase();
                    EntitlementServiceApi.getInstance().executeCommand("check_access " + Long.toString(accessToken) + ", " + permissionName +", " + ModelServiceApiImpl.getInstance().modelFullyQualifiedNameToControllerFullyQualifiedName(device.getFullyQualifiedName()));
                } catch (EntitlementException e) {
                    System.out.println("Access denied: " + e.getMessage());
                    return;
                }

                device.setStatus(status, "active");
            } else {

                throw new IllegalArgumentException("Invalid set command: " + this.scriptLineText);
            }
        }
    }

    /**
     * Handle "show configuration ..." commands. If a specific object name is provided,
     * prints its configuration; if the remainingText is empty, prints configurations for all houses.
     *
     * @param remainingText the text following the "show configuration" token (may be empty)
     * @throws IllegalArgumentException if parsing fails or the named object is not found/appropriate
     */
    private String showConfiguration(String remainingText, long accessToken) {
        String name = null;
        Configurable obj = null;

        Pattern p = Pattern.compile("^\\s*\\b(.*)$", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(remainingText);
        if (m.find()) {
            name = m.group(1).trim();
            String[] objects = name.split(":", 3);

            try {
                if(objects.length == 1) {
                    // It's a house
                    obj = (Configurable) ModelServiceApiImpl.getInstance().getModelObject("house_" + name);
                } else if(objects.length == 2) {
                    // It's a room
                    obj = (Configurable) ModelServiceApiImpl.getInstance().getModelObject("house_" + objects[0] + ":room_" + objects[1]);
                } else if(objects.length == 3) {
                    // It's a device
                    obj = (Configurable) ModelServiceApiImpl.getInstance().getModelObject("house_" + objects[0] + ":room_" + objects[1] + ":sensor_" + objects[2]);

                    if(obj == null) {
                        obj = (Configurable) ModelServiceApiImpl.getInstance().getModelObject("house_" + objects[0] + ":room_" + objects[1] + ":appliance_" + objects[2]);
                    }
                } else {
                    throw new IllegalArgumentException("Invalid object name for show configuration command: " + this.scriptLineText);
                }
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("Object is not configurable for show configuration command: " + this.scriptLineText);
            }

            try {
                String permissionName = "view_configuration";
                EntitlementServiceApi.getInstance().executeCommand("check_access " + Long.toString(accessToken) + ", " + permissionName +", " + name);
            } catch (EntitlementException e) {
                System.out.println("Access denied: " + e.getMessage());
                return "Access denied: " + e.getMessage();
            }

            return obj.getConfiguration();
        } else if(remainingText.trim().isEmpty()) {
            // Show configuration for all houses
            StringBuilder sb = new StringBuilder();
            sb.append("Configuration for all houses:");
            for(ModelObject modelObject : ModelServiceApiImpl.getInstance().getAllModelObjects().values()) {
                if(modelObject instanceof House) {
                    sb.append(System.lineSeparator()).append(((House) modelObject).getConfiguration());
                }
            }

            try {
                String permissionName = "view_configuration";
                EntitlementServiceApi.getInstance().executeCommand("check_access " + Long.toString(accessToken) + ", " + permissionName +", " + "all_houses");
            } catch (EntitlementException e) {
                System.out.println("Access denied: " + e.getMessage());
                return "Access denied: " + e.getMessage();
            }

            return sb.toString();
        } else {
            throw new IllegalArgumentException("Invalid show configuration command: " + this.scriptLineText);
        }
    }

    /**
     * Handle "show energy-use ..." commands. If a specific object name is provided,
     * prints its energy usage; if the remainingText is empty, prints total energy use for all houses.
     *
     * @param remainingText the text following the "show energy-use" token (may be empty)
     * @throws IllegalArgumentException if parsing fails or the named object is not energy-readable
     */
    private String showEnergyUse(String remainingText, long accessToken) {
        String name = null;
        EnergyReadable obj = null;

        Pattern p = Pattern.compile("^\\s*\\b(.*)$", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(remainingText);
        if (m.find()) {
            name = m.group(1).trim();
            String[] objects = name.split(":", 3);

            try {
                if(objects.length == 1) {
                    // It's a house
                    obj = (EnergyReadable) ModelServiceApiImpl.getInstance().getModelObject("house_" + name);
                } else if(objects.length == 2) {
                    // It's a room
                    obj = (EnergyReadable) ModelServiceApiImpl.getInstance().getModelObject("house_" + objects[0] + ":room_" + objects[1]);
                } else if(objects.length == 3) {
                    // It's an appliance
                    obj = (EnergyReadable) ModelServiceApiImpl.getInstance().getModelObject("house_" + objects[0] + ":room_" + objects[1] + ":appliance_" + objects[2]);
                } else {
                    throw new IllegalArgumentException("Invalid object name for show energy-use command: " + this.scriptLineText);
                }
            } catch (ClassCastException e) {
                    throw new IllegalArgumentException("Object is not energy-readable for show energy-use command: " + this.scriptLineText);
            }

            try {
                String permissionName = "view_energy";
                EntitlementServiceApi.getInstance().executeCommand("check_access " + Long.toString(accessToken) + ", " + permissionName +", " + name);
            } catch (EntitlementException e) {
                System.out.println("Access denied: " + e.getMessage());
                return "Access denied: " + e.getMessage();
            }

            return ((ModelObject) obj).getName() + " energy use: " + obj.getEnergyConsumptionWatts();
        } else if(remainingText.trim().isEmpty()) {
            // Show energy use for all houses
            double totalEnergyUse = 0.0;
            for(ModelObject modelObject : ModelServiceApiImpl.getInstance().getAllModelObjects().values()) {
                if(modelObject instanceof House) {
                    totalEnergyUse += ((House) modelObject).getEnergyConsumptionWatts();
                }
            }

            try {
                String permissionName = "view_configuration";
                EntitlementServiceApi.getInstance().executeCommand("check_access " + Long.toString(accessToken) + ", " + permissionName +", " + "all_houses");
            } catch (EntitlementException e) {
                System.out.println("Access denied: " + e.getMessage());
                return "Access denied: " + e.getMessage();
            }

            return "Total energy use for all houses: " + totalEnergyUse;
        } else {
            throw new IllegalArgumentException("Invalid show energy-use command: " + this.scriptLineText);
        }
    }

    /**
     * Handle "show ... status ..." commands for devices.
     * Prints either a specific status value or all statuses for a device.
     *
     * @param remainingText the text following the "show sensor|appliance" token
     * @throws IllegalArgumentException if parsing fails, the device is not found, or a requested status is missing
     */
    private String showDevice(String remainingText, long accessToken) {
        String name = null;
        Device obj = null;

        Pattern p = Pattern.compile("^\\s*\\b(.*) status \\b(.*)$", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(remainingText);
        if (m.find()) {
            name = m.group(1).trim();
            String statusName = m.group(2).trim();
            String[] objects = name.split(":", 3);

            try {
                if(objects.length == 3) {
                    // It's a device
                    obj = (Device) ModelServiceApiImpl.getInstance().getModelObject("house_" + objects[0] + ":room_" + objects[1] + ":sensor_" + objects[2]);

                    if(obj == null) {
                        obj = (Device) ModelServiceApiImpl.getInstance().getModelObject("house_" + objects[0] + ":room_" + objects[1] + ":appliance_" + objects[2]);
                    }

                    if(obj == null) {
                        throw new IllegalArgumentException("Device not found for show device command: " + this.scriptLineText);
                    }
                } else {
                    throw new IllegalArgumentException("Invalid object name for show device command: " + this.scriptLineText);
                }
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("Object is not a device for show device command: " + this.scriptLineText);
            }

            String statusValue = obj.getStatus(statusName);
            if(statusValue != null) {

                try {
                    Device device = (Device) obj;

                    String permissionName = "view_" + device.getType().toLowerCase();
                    EntitlementServiceApi.getInstance().executeCommand("check_access " + Long.toString(accessToken) + ", " + permissionName +", " + ModelServiceApiImpl.getInstance().modelFullyQualifiedNameToControllerFullyQualifiedName(device.getFullyQualifiedName()));
                } catch (EntitlementException e) {
                    System.out.println("Access denied: " + e.getMessage());
                    return "Access denied: " + e.getMessage();
                }

                return obj.getName() + " status " + statusName + ": " + statusValue;
            } else {
                        throw new IllegalArgumentException("Status " + statusName + " not found for device " + obj.getName() + " in command: " + this.scriptLineText);
            }
        } else {
            p = Pattern.compile("^\\s*\\b(.*)$", Pattern.CASE_INSENSITIVE);
            m = p.matcher(remainingText);
            if (m.find()) {
                name = m.group(1).trim();
                String[] objects = name.split(":", 3);

                if(objects.length == 3) {
                    obj = (Device) ModelServiceApiImpl.getInstance().getModelObject("house_" + objects[0] + ":room_" + objects[1] + ":sensor_" + objects[2]);

                    if(obj == null) {
                        obj = (Device) ModelServiceApiImpl.getInstance().getModelObject("house_" + objects[0] + ":room_" + objects[1] + ":appliance_" + objects[2]);
                    }

                    if(obj == null) {
                        throw new IllegalArgumentException("Device not found for show device command: " + this.scriptLineText);
                    }
                } else {
                    throw new IllegalArgumentException("Invalid object name for show device command: " + this.scriptLineText);
                }

                try {
                    Device device = (Device) obj;

                    String permissionName = "view_" + device.getType().toLowerCase();
                    EntitlementServiceApi.getInstance().executeCommand("check_access " + Long.toString(accessToken) + ", " + permissionName +", " + ModelServiceApiImpl.getInstance().modelFullyQualifiedNameToControllerFullyQualifiedName(device.getFullyQualifiedName()));
                } catch (EntitlementException e) {
                    System.out.println("Access denied: " + e.getMessage());
                    return "Access denied: " + e.getMessage();
                }

                return obj.getName() + " statuses: " + obj.getStatuses();
            } else {
                throw new IllegalArgumentException("Invalid show device command: " + this.scriptLineText);
            }
        }
    }
}
