package cscie97.asn2.housemate.test;

import cscie97.asn2.housemate.model.ModelServiceApi;

public class TestDriver {
    public static void main(String[] args) {
        ModelServiceApi modelService = ModelServiceApi.getInstance();
        modelService.executeScript(args[0]);
    }
}
