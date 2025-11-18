package cscie97.asn4.housemate.model;

import cscie97.asn4.housemate.controller.StatusObserver;

/**
 * The ModelServiceApi interface provides methods to interact with the model service of the housemate system.
 */
public interface ModelServiceApi {
    /**
     * Execute a script file to configure the model service.
     *
     * @param commandText the command text to execute
     * @param accessToken the authentication key for executing the command
     */
    String executeCommand(String commandText, long accessToken);

    /**
     * Attach a status observer to the model service.
     *
     * @param observer the status observer to attach
     */
    void attachStatusObserver(StatusObserver observer);

    /**
     * Notify all attached status observers of a status change.
     */
    void notifyStatusObservers(String device, String status, String newValue, String deviceType);
}
