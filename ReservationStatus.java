/**
 * Every possible reservation state. The menu methods control valid transitions.
 * PENDING reservations are waiting for staff to verify the reservation name.
 */
public enum ReservationStatus {
    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    PENDING_UPDATE("Pending Update"),
    CANCEL_REQUESTED("Cancel Requested"),
    CHECKED_IN("Checked-In"),
    CHECKED_OUT("Checked-Out"),
    CANCELLED("Cancelled");

    private final String label;

    ReservationStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
