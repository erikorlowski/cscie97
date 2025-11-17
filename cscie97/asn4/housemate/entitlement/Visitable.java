package cscie97.asn4.housemate.entitlement;

/**
 * Interface implemented by objects that can be visited by a Visitor.
 */
public interface Visitable {
    void accept(Visitor visitor);
}
