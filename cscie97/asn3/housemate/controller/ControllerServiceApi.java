package cscie97.asn3.housemate.controller;

import cscie97.asn2.housemate.model.ModelServiceApiImpl;
import cscie97.asn2.housemate.model.ModelServiceApi;

public class ControllerServiceApi {
    public static ControllerServiceApi instance = null;
    
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
    }
}
