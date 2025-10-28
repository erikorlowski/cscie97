package cscie97.asn3.housemate.controller;

/** 
 * Command to notify about beer status updates 
*/
public class BeerNotificationCommand implements Command {
    private final String fullyQualifiedRefrigeratorName;
    private final int beerCount;

    /**
     * Constructor for BeerNotificationCommand.
     * @param fullyQualifiedRefrigeratorName
     * @param beerCount
     */
    public BeerNotificationCommand(String fullyQualifiedRefrigeratorName, int beerCount) {
        this.fullyQualifiedRefrigeratorName = fullyQualifiedRefrigeratorName;
        this.beerCount = beerCount;
    }

    /**
     * Execute the beer notification command.
     */
    @Override
    public String execute() {
        String status = String.format("Refrigerator '%s' currently has %d beer(s).", fullyQualifiedRefrigeratorName, beerCount);
        System.out.println(status);
        System.out.print("Would you like to order more beer? (yes/no): ");
        try (java.util.Scanner scanner = new java.util.Scanner(System.in)) {
            String answer = scanner.nextLine().trim().toLowerCase();

            if (answer.equals("yes") || answer.equals("y")) {
                String email = String.format(
                    "To: %s\nSubject: Beer Refill Request\n\nHello,\n\nPlease send additional beer to %s. Current count: %d.\n\nThank you.\n",
                    fullyQualifiedRefrigeratorName, fullyQualifiedRefrigeratorName, beerCount
                );
                return email;
            } else {
                return String.format("No beer will be ordered for %s (current count: %d).", fullyQualifiedRefrigeratorName, beerCount);
            }
        }
    }
}
