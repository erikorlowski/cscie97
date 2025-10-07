package cscie97.asn2.housemate.model;

/**
 * The ModelServiceApi interface provides methods to interact with the model service of the housemate system.
 */
public interface ModelServiceApi {
    /**
     * Execute a script file to configure the model service.
     *
     * @param file the path to the script file
     * @param authenticationKey the authentication key for executing the script
     */
    void executeScript(String file, char[] authenticationKey);
}
