package cscie97.asn2.housemate.test;

import cscie97.asn2.housemate.model.ModelServiceApi;
import cscie97.asn2.housemate.model.ModelServiceApiImpl;
import cscie97.asn3.housemate.controller.ControllerServiceApi;

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
        ControllerServiceApi.getInstance().initialize();
        ModelServiceApi modelService = ModelServiceApiImpl.getInstance();
        if (args.length < 1) {
            System.err.println("Usage: TestDriver <script-file>");
            return;
        }

        java.nio.file.Path scriptPath = java.nio.file.Paths.get(args[0]);
        try (java.io.BufferedReader reader = java.nio.file.Files.newBufferedReader(scriptPath, java.nio.charset.StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                String output = modelService.executeCommand(line, new char[] {'a','d','m','i','n'});
                if (output != null && !output.isEmpty()) {
                    System.out.println(output);
                }
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}
