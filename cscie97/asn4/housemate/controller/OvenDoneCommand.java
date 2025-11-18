package cscie97.asn4.housemate.controller;

import cscie97.asn4.housemate.model.ModelServiceApi;
import cscie97.asn4.housemate.model.ModelServiceApiImpl;

/**
 * Command executed when an oven finishes cooking. The command will perform any
 * necessary actions such as turning the oven off via the ModelServiceApi and
 * returning a human-readable status message.
 */
public class OvenDoneCommand implements Command {

    /** Fully-qualified name of the oven device (house:room:oven). */
    private final String fullyQualifiedOvenName;

    /**
     * Create a new OvenDoneCommand for the specified oven.
     *
     * @param fullyQualifiedOvenName the fully-qualified oven name (e.g. "house1:kitchen:oven")
     */
    public OvenDoneCommand(String fullyQualifiedOvenName) {
        this.fullyQualifiedOvenName = fullyQualifiedOvenName;
    }

    /**
     * Execute the oven-done action. The current implementation turns the oven's
     * power status to OFF by invoking the ModelServiceApi and returns a short
     * status message describing the result.
     *
     * @return a human-readable result message
     */
    @Override
    public String execute() {
        ModelServiceApi modelServiceApi = ModelServiceApiImpl.getInstance();

    // Turn oven off. Use current entitlement token.
    long token = ControllerServiceApi.getInstance().getControllerAccessToken();
    modelServiceApi.executeCommand("set appliance " + fullyQualifiedOvenName + " status power value OFF", token);

        int colonIndex = fullyQualifiedOvenName.indexOf(':');
        String houseName = (colonIndex == -1) ? fullyQualifiedOvenName : fullyQualifiedOvenName.substring(0, colonIndex);
        
        ApplicationTypeCommand avaCmd = new ApplicationTypeCommand(houseName, "ava", "Text to Speech", "Food is ready.");
        System.out.println(avaCmd.execute());

        return String.format("Oven %s is done", fullyQualifiedOvenName);
    }
}
