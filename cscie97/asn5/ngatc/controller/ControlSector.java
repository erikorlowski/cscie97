package cscie97.asn4.asn5.ngatc.controller;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import cscie97.asn4.asn5.ngatc.flighttracker.Flight;
import cscie97.asn4.asn5.ngatc.staticmap.Airspace;

/**
 * Represents a control sector managed by a controller.
 */
public class ControlSector extends Airspace {
    private ArrayList<String> controllerMessages;
    private int numberOfCurrentFlights;

    public ControlSector() {
        super();
        this.controllerMessages = new ArrayList<>();
    }

    public ControlSector(long id, String name, double lowerLimitFeet, double upperLimitFeet) {
        super(id, "CONTROL_SECTOR", name, lowerLimitFeet, upperLimitFeet);
        this.controllerMessages = new ArrayList<>();
    }

    public ArrayList<String> getControllerMessages() {
        return controllerMessages;
    }

    public void setControllerMessages(ArrayList<String> controllerMessages) {
        this.controllerMessages = controllerMessages;
    }

    public void addMessage(String message) {
        this.controllerMessages.add(message);
    }

    public int getNumberOfCurrentFlights() {
        return numberOfCurrentFlights;
    }

    public void setNumberOfCurrentFlights(int numberOfCurrentFlights) {
        this.numberOfCurrentFlights = numberOfCurrentFlights;
    }

    @Override
    public String toString() {
        return String.format("ControlSector{id=%d, name='%s', flights=%d, messages=%d}",
            id, name, numberOfCurrentFlights, controllerMessages.size());
    }
}
