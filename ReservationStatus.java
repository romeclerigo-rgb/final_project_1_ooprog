/**
 * Every status a reservation can have.
 *
 *   CONFIRMED -> PENDING_UPDATE   (customer asks for a change, staff approve or reject)
 *   CONFIRMED -> CANCEL_REQUESTED (customer asks to cancel, manager approves or rejects)
 *   CONFIRMED -> CHECKED_IN -> CHECKED_OUT (staff, only when fully paid)
 *   CANCEL_REQUESTED -> CANCELLED (moved to CANCELLED.txt)
 */
public enum ReservationStatus {
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
