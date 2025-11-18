package cscie97.asn4.housemate.controller;

import cscie97.asn4.housemate.entitlement.EntitlementException;
import cscie97.asn4.housemate.entitlement.EntitlementServiceApi;

/**
 * Command that attempts to authenticate a user using a voiceprint.
 *
 * This simple command delegates to the EntitlementServiceApi by constructing
 * a "login voiceprint <voiceprint>" command string. On success it returns a
 * short success message including the returned access token string; on
 * failure it returns an error message describing the failure.
 */
public class VoiceprintCommand implements Command {
    /** The voiceprint string used to authenticate. */
    private String voiceprint;

    /**
     * Create a new VoiceprintCommand for the provided voiceprint.
     *
     * @param voiceprint the voiceprint data to use for authentication
     */
    public VoiceprintCommand(String voiceprint) {
        this.voiceprint = voiceprint;
    }

    /**
     * Execute the voiceprint login. Calls the EntitlementServiceApi with
     * a "login voiceprint <voiceprint>" command and returns a textual result.
     *
     * @return a short success message containing the access token on success
     *         or an error message on failure
     */
    @Override
    public String execute() {
        // Here we would interact with the EntitlementServiceApi to perform login
        try {
            String accessToken = EntitlementServiceApi.getInstance().executeCommand("login voiceprint " + voiceprint);
            return "Voiceprint login successful for voiceprint: " + voiceprint + " with AccessToken: " + accessToken;
        } catch (EntitlementException e) {
            return "Voiceprint login failed: " + e.getMessage();
        }
    }

}
