package cscie97.asn4.housemate.entitlement;

/**
 * Represents a resource in the Housemate system. Resources are identified by
 * a hierarchical name (for example: "HouseName:RoomName:DeviceName"). The
 * The checkAccess(Resource) method implements a simple hierarchical
 * matching algorithm so that broader resources (house or room) match contained
 * resources.
 */
public class Resource implements Visitable {
    private final String name;

    /**
     * Construct a Resource with the given fully-qualified name.
     *
     * @param name the fully-qualified resource name (non-null)
     */
    public Resource(String name) {
        this.name = name;
    }

    /**
     * Get the resource's name.
     *
     * @return the fully-qualified resource name
     */
    public String getName() { return name; }

    /**
     * Determine whether the provided resource is covered by this
     * Resource. Matching rules (simple heuristic used by the project):
     *
     * Exact match on the fully-qualified name returns true.
     * If this resource has a colon-separated hierarchical name, a request for
     * the house (first segment) or the room (first two segments) also returns
     * true.
     *
     * Examples (when this.name is "MyHouse:LivingRoom:Ava"):
     * - checkAccess(new Resource("MyHouse")) returns true
     * - checkAccess(new Resource("MyHouse:LivingRoom")) returns true
     * - checkAccess(new Resource("MyHouse:OtherRoom")) returns false
     *
     * @param resource the resource to test against (may be null)
     * @return true if resource is the same or contained by this resource
     *         according to the simple hierarchy rules
     */
    public boolean checkAccess(Resource resource) {
        if (resource == null) return false;
        String other = resource.getName();
        if (this.name.equals(other)) {
            return true;
        }
        // Check if the resource is the house of this resource
        int firstColon = this.name.indexOf(':');
        if (firstColon != -1) {
            String beforeFirst = this.name.substring(0, firstColon);
            if (beforeFirst.equals(other)) {
                return true;
            }
            // Check if the resource is the room of this resource
            int secondColon = this.name.indexOf(':', firstColon + 1);
            if (secondColon != -1) {
                String beforeSecond = this.name.substring(0, secondColon);
                if (beforeSecond.equals(other)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
    * Accept a Visitor to operate on this Resource.
     *
     * @param visitor the visitor handling this resource
     */
    @Override
    public void accept(Visitor visitor) {
        visitor.visitResource(this);
    }
}
