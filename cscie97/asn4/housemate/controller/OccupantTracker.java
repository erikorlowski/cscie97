package cscie97.asn4.housemate.controller;

import cscie97.asn1.knowledge.engine.KnowledgeGraph;
import cscie97.asn1.knowledge.engine.Triple;
import java.util.HashSet;
import java.util.Set;

/**
 * Singleton wrapper around the KnowledgeGraph used by the
 * controller package to track occupant locations and active/inactive state.
 *
 */
public class OccupantTracker {

    private static final OccupantTracker INSTANCE = new OccupantTracker();

    private final KnowledgeGraph occupantKnowledgeGraph = KnowledgeGraph.getInstance();

    private OccupantTracker() {}

    /**
     * Return the singleton instance of OccupantTracker.
     *
     * @return the single OccupantTracker instance
     */
    public static OccupantTracker getInstance() {
        return INSTANCE;
    }

    /**
     * Add or update the location of an occupant in the knowledge graph.
     *
     * @param occupantName the occupant name
     * @param fullyQualifiedRoomName the fully-qualified room name
     * @implNote This method updates the occupant's location to the specified room.
     */
    public void addOccupantToRoom(String occupantName, String fullyQualifiedRoomName) {
        if (occupantName == null || fullyQualifiedRoomName == null) return;
        occupantKnowledgeGraph.importTriple(occupantName, "is_located_in_room", fullyQualifiedRoomName);
        String houseName;
        int colonIndex = fullyQualifiedRoomName.indexOf(':');
        houseName = (colonIndex >= 0) ? fullyQualifiedRoomName.substring(0, colonIndex) : fullyQualifiedRoomName;
        occupantKnowledgeGraph.importTriple(occupantName, "is_located_in_house", houseName);
    }

    /**
     * Remove an occupant from a specific room.
     *
     * @param occupantName the occupant name
     * @param fullyQualifiedRoomName the fully-qualified room name
     */
    public void removeOccupantFromRoom(String occupantName, String fullyQualifiedRoomName) {
        if (occupantName == null) return;
        
        String houseName;
        int colonIndex = fullyQualifiedRoomName.indexOf(':');
        houseName = (colonIndex >= 0) ? fullyQualifiedRoomName.substring(0, colonIndex) : fullyQualifiedRoomName;

        // KnowledgeGraph has no remove API; mark as unknown location instead.
        // occupantKnowledgeGraph.importTriple(occupantName, "is_located_in_room", "unknown");
        // occupantKnowledgeGraph.importTriple(occupantName, "is_located_in_house", "unknown");
        occupantKnowledgeGraph.removeTriplesBySubjectAndPredicate(occupantName, "is_located_in_room");
        occupantKnowledgeGraph.removeTriplesBySubjectAndPredicate(occupantName, "is_located_in_house");
    }

    /**
     * Mark the occupant as active (awake) in the knowledge graph.
     *
     * @param occupantName the occupant name
     */
    public void makeOccupantActive(String occupantName) {
        if (occupantName == null) return;
        occupantKnowledgeGraph.importTriple(occupantName, "is_active", "true");
    }

    /**
     * Mark the occupant as inactive (sleeping) in the knowledge graph.
     *
     * @param occupantName the occupant name
     */
    public void makeOccupantInactive(String occupantName) {
        if (occupantName == null) return;
        occupantKnowledgeGraph.importTriple(occupantName, "is_active", "false");
    }

    /**
     * Get the set of occupants currently located in the specified house.
     * @param houseName the house name
     * @return A set of occupant names.
     */
    public Set<String> getOccupantsInHouse(String houseName) {
        Set<String> occupants = new HashSet<>();
        if (houseName == null) return occupants;

        Set<Triple> subjects = occupantKnowledgeGraph.executeQuery("?", "is_located_in_house", houseName);
        if (subjects != null) {
            for(Triple triple : subjects) {
                occupants.add(triple.getSubject().getIdentifier());
            }
        }
        return occupants;
    }

    /**
     * Get the set of occupants currently located in the specified room.
     * @param roomName the room name
     * @return A set of occupant names.
     */
    public Set<String> getOccupantsInRoom(String roomName) {
        System.out.println("Getting occupants in room: " + roomName);
        Set<String> occupants = new HashSet<>();
        if (roomName == null) return occupants;

        Set<Triple> subjects = occupantKnowledgeGraph.executeQuery("?", "is_located_in_room", roomName);
        if (subjects != null) {
            for(Triple triple : subjects) {
                occupants.add(triple.getSubject().getIdentifier());
            }
        }
        System.out.println("Occupants in room " + roomName + ": " + occupants);
        return occupants;
    }

    /**
     * Get the current location of the specified occupant.
     * @param occupantName the occupant name
     * @return the current location of the occupant
     */
    public String getOccupantLocation(String occupantName) {
        if (occupantName == null) return "unknown";

        Set<Triple> locations = occupantKnowledgeGraph.executeQuery(occupantName, "is_located_in_room", "?");
        if (locations != null && !locations.isEmpty()) {
            for (Triple triple : locations) {
                return triple.getObject().getIdentifier();
            }
        }
        return "unknown";
    }
}
