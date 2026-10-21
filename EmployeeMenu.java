import java.util.List;

/**
 * Shared menu helpers for StaffMenu and ManagerMenu.
 * Inheritance avoids maintaining separate copies of search/sort/filter logic.
 */
public abstract class EmployeeMenu {
    protected final DataStore store = DataStore.getInstance();

    protected void searchReservations(boolean includeCancelled) {
        String query = Input.readReservationName("Reservation name or keyword");
        Display.printReservations(store.searchReservationsByName(query, includeCancelled));
    }

    protected void sortReservations() {
        List<Reservation> list = store.getReservations();
        System.out.println("  [1] Sort by date");
        System.out.println("  [2] Sort by payment status");
        int choice = Input.readMenuChoice(1, 2);
        SortStrategy sorter;
        // The selected sorting algorithm is chosen at runtime via the Strategy pattern.
        switch (choice) {
            case 1:
                sorter = new SortByDate();
                break;
            case 2:
                sorter = new SortByPaymentStatus();
                break;
            default:
                // Input validation restricts this menu to options 1 and 2.
                throw new IllegalStateException("Unexpected sort option: " + choice);
        }
        sorter.sort(list);
        System.out.println("Sorted by " + sorter.getName() + ".");
        Display.printReservations(list);
    }

    /** Both roles can filter by the statuses/payment categories required by the proposal. */
    protected void filterReservations() {
        System.out.println("  [1] Pending reservations");
        System.out.println("  [2] Pending update requests");
        System.out.println("  [3] Checked-In reservations");
        System.out.println("  [4] Checked-Out reservations");
        System.out.println("  [5] Filter by payment status");
        int choice = Input.readMenuChoice(1, 5);
        // Status filters are discrete choices, so switch-case is easier to scan.
        switch (choice) {
            case 1:
                Display.printReservations(store.getReservationsByStatus(ReservationStatus.PENDING));
                break;
            case 2:
                Display.printReservations(store.getReservationsByStatus(ReservationStatus.PENDING_UPDATE));
                break;
            case 3:
                Display.printReservations(store.getReservationsByStatus(ReservationStatus.CHECKED_IN));
                break;
            case 4:
                Display.printReservations(store.getReservationsByStatus(ReservationStatus.CHECKED_OUT));
                break;
            case 5:
                printPaymentFilter();
                break;
            default:
                throw new IllegalStateException("Unexpected filter option: " + choice);
        }
    }

    private void printPaymentFilter() {
        System.out.println("  [1] Fully Paid");
        System.out.println("  [2] Partially Paid");
        System.out.println("  [3] Unpaid");
        int choice = Input.readMenuChoice(1, 3);
        String status;
        switch (choice) {
            case 1:
                status = "Fully Paid";
                break;
            case 2:
                status = "Partially Paid";
                break;
            case 3:
                status = "Unpaid";
                break;
            default:
                throw new IllegalStateException("Unexpected payment filter option: " + choice);
        }
        Display.printReservations(store.getReservationsByPaymentStatus(status));
    }

    protected Reservation findActiveReservationById() {
        boolean found = false;
        Reservation selected = null;
        while (!found) {
            String id = Input.readReservationId("Reservation ID (six digits, without RN-)");
            selected = store.findReservation(id);
            if (selected == null) {
                System.out.println("  [Invalid] No active reservation has that ID.");
            } else {
                found = true;
            }
        }
        return selected;
    }

    protected Reservation selectReservation(List<Reservation> choices, String prompt) {
        boolean found = false;
        Reservation selected = null;
        while (!found) {
            String id = Input.readReservationId(prompt);
            for (Reservation reservation : choices) {
                if (reservation.getReservationId().equals(id)) {
                    selected = reservation;
                    found = true;
                    break;
                }
            }
            if (!found) System.out.println("That ID is not in the displayed list. Please try again.");
        }
        return selected;
    }
}
