/**
 * Thrown when an action breaks a reservation rule.
 * Example: checking out a guest who still owes money.
 * The menus catch it and show the message instead of crashing.
 */
public class ReservationException extends Exception {

    private static final long serialVersionUID = 1L;

    public ReservationException(String message) {
        super(message);
    }
}
