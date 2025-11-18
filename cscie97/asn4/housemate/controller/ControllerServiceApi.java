package cscie97.asn4.housemate.controller;

import cscie97.asn4.housemate.model.ModelServiceApiImpl;
import cscie97.asn4.housemate.entitlement.EntitlementException;
import cscie97.asn4.housemate.entitlement.EntitlementServiceApi;
import cscie97.asn4.housemate.model.ModelServiceApi;

public class ControllerServiceApi {
    public static ControllerServiceApi instance = null;
    private long controllerAccessToken;
    
    private ControllerServiceApi() {
    }

    public static ControllerServiceApi getInstance() {
        if (instance == null) {
            instance = new ControllerServiceApi();
        }
        return instance;
    }

    public void initialize() {
        ModelServiceApi modelApi = ModelServiceApiImpl.getInstance();

        modelApi.attachStatusObserver(new AvaObserver());
        modelApi.attachStatusObserver(new BeerCountObserver());
        modelApi.attachStatusObserver(new CameraObserver());
        modelApi.attachStatusObserver(new FireObserver());
        modelApi.attachStatusObserver(new OvenDoneObserver());

        try {
            EntitlementServiceApi.getInstance().executeCommand("create_user controller, controller");
            EntitlementServiceApi.getInstance().executeCommand("add_user_credential controller, password, sdkvj3349fjsd");
            controllerAccessToken = Long.parseLong(EntitlementServiceApi.getInstance().executeCommand("login user controller, password sdkvj3349fjsd"));
            EntitlementServiceApi.getInstance().executeCommand("define_role, controller_role, controller_role, \"Starter Admin role for the HouseMate controller service\"");
            EntitlementServiceApi.getInstance().executeCommand("add_role_to_user controller, controller_role");
        } catch (EntitlementException e) {
            System.out.println("Error creating controller user: " + e.getMessage());
        }
    }

    /**
     * Gets the Admin AccessToken associated with the Controller Service.
     * @return
     */
    long getControllerAccessToken() {
        return controllerAccessToken;
    }
}
