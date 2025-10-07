package cscie97.asn2.housemate.model;

 /**
 * Represents a sensor device in the smart home system.
 * It inherits from the Device class.
 */
public class Sensor extends Device {
    /**
     * Constructs a Sensor with the specified fully qualified name and type.
     *
     * @param fullyQualifiedName the fully qualified name of the sensor ("sensor_<house>:<room>:<sensor>")
     * @param type the type of the sensor
     */
    public Sensor(String fullyQualifiedName, String type) {
        super(fullyQualifiedName, type);
    }
    
}
