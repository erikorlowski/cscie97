package cscie97.asn2.housemate.test;

import cscie97.asn2.housemate.model.ModelServiceApi;
import cscie97.asn2.housemate.model.ModelServiceApiImpl;

/**
 * Test driver for the HouseMate system.
 */
public class TestDriver {
    /**
     * Main method to execute the test driver.
     *
     * @param args command line arguments, expects the script file path as the first argument
     */
    public static void main(String[] args) {
        ModelServiceApi modelService = ModelServiceApiImpl.getInstance();
        modelService.executeScript(args[0], new char[] {'a','d','m','i','n'});
    }
}
