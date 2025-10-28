package cscie97.asn3.housemate.controller;

import cscie97.asn2.housemate.model.ModelServiceApiImpl;

public class FireCommand implements Command {
    private final String fullyQualifiedSmokeDetectorName;

    public FireCommand(String fullyQualifiedSmokeDetectorName) {
        this.fullyQualifiedSmokeDetectorName = fullyQualifiedSmokeDetectorName;
    }

    @Override
    public String execute() {
        int firstColon = fullyQualifiedSmokeDetectorName.indexOf(':');
        String house = firstColon != -1 ? fullyQualifiedSmokeDetectorName.substring(0, firstColon) : "";
        int secondColon = firstColon == -1 ? -1 : fullyQualifiedSmokeDetectorName.indexOf(':', firstColon + 1);
        String room = (firstColon != -1 && secondColon != -1)
            ? fullyQualifiedSmokeDetectorName.substring(firstColon + 1, secondColon)
            : "";

        boolean occupantsPresent = !OccupantTracker.getInstance().getOccupantsInHouse(house).isEmpty();
        if (occupantsPresent) {
            setAvaAlerts(house, room);
            ApplicationTypeCommand turnOffLightsCmd = new ApplicationTypeCommand(house, "light", "power", "OFF");
            turnOffLightsCmd.execute();
        }


        return String.format("Fire detected at %s calling 911!", fullyQualifiedSmokeDetectorName);
    }

    /**
     * Set AVA alerts in all rooms of the house to notify occupants of the fire.
     * @param originatingHouse The house where the fire is detected
     * @param originatingRoom The room where the fire is detected
     */
    private void setAvaAlerts(String originatingHouse, String originatingRoom) {
        String houseConfiguration = ModelServiceApiImpl.getInstance()
            .executeCommand("show configuration " + originatingHouse, new char[] {'a', 'd', 'm', 'i', 'n'});

        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.StringReader(houseConfiguration))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(line.startsWith("Room: ")) {
                    String roomName = "";
                    java.util.regex.Pattern namePattern = java.util.regex.Pattern.compile("\\bname\\s*=\\s*([^,\\s]+)", java.util.regex.Pattern.CASE_INSENSITIVE);
                    java.util.regex.Matcher nameMatcher = namePattern.matcher(line);
                    if (nameMatcher.find()) {
                        roomName = nameMatcher.group(1).trim();
                        // strip surrounding quotes if present
                        if ((roomName.startsWith("\"") && roomName.endsWith("\"")) || (roomName.startsWith("'") && roomName.endsWith("'"))) {
                            roomName = roomName.substring(1, roomName.length() - 1);
                        }
                    }

                    int floorNumber = 0;
                    int windowsCount = 0;

                    java.util.regex.Pattern floorPattern = java.util.regex.Pattern.compile("floor\\s*[:=]?\\s*(-?\\d+)", java.util.regex.Pattern.CASE_INSENSITIVE);
                    java.util.regex.Matcher floorMatcher = floorPattern.matcher(line);
                    if (floorMatcher.find()) {
                        try {
                            floorNumber = Integer.parseInt(floorMatcher.group(1));
                        } catch (NumberFormatException e) {
                            floorNumber = 0;
                        }
                    }

                    java.util.regex.Pattern windowsPattern = java.util.regex.Pattern.compile("numWindows?\\s*[:=]?\\s*(-?\\d+)", java.util.regex.Pattern.CASE_INSENSITIVE);
                    java.util.regex.Matcher windowsMatcher = windowsPattern.matcher(line);
                    if (windowsMatcher.find()) {
                        try {
                            windowsCount = Integer.parseInt(windowsMatcher.group(1));
                        } catch (NumberFormatException e) {
                            windowsCount = 0;
                        }
                    }

                    boolean canUseWindows = (floorNumber == 1) && (windowsCount > 0);

                    String escapeMessage = canUseWindows
                        ? "Fire in the " + originatingRoom + ". Please evacuate immediately using the windows."
                        : "Fire in the " + originatingRoom + ". Please evacuate immediately.";

                    ApplicationTypeCommand avaCmd = new ApplicationTypeCommand(originatingHouse + ":" + roomName, "ava", "Text to Speech", escapeMessage);
                }
            }
        } catch (java.io.IOException e) {
            System.out.println("An error occurred responding to a fire. Please evacuate the house immediately.");
            System.out.println("Error reading house configuration: " + e.getMessage());
        }
    }
}
