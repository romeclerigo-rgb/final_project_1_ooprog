import java.util.List;

/**
 * Shared menu helpers for StaffMenu and ManagerMenu.
 * Inheritance avoids maintaining separate copies of search/sort/filter logic.
 */
public abstract class EmployeeMenu {
    protected final DataStore store = DataStore.getInstance();

    /** Runs the menu for the specific employee role. */
    public abstract void run();

    protected void searchReservations(boolean includeCancelled) {
        Display.printHeader("SEARCH RESERVATIONS");
        String query = Input.readReservationName("Reservation name or keyword");
        Display.printSection("Search Results");
        Display.printReservations(store.searchReservationsByName(query, includeCancelled));
    }

    protected void sortReservations() {
        List<Reservation> list = store.getReservations();
        Display.printHeader("SORT RESERVATIONS");
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
        Display.printSuccess("Reservations sorted by " + sorter.getName() + ".");
        Display.printReservations(list);
    }

    /** Both roles can filter by the statuses/payment categories required by the proposal. */
    protected void filterReservations() {
        Display.printHeader("FILTER RESERVATIONS");
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
        Display.printHeader("FILTER BY PAYMENT STATUS");
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
        Display.printSection("Filtered Reservations - " + status);
        Display.printReservations(store.getReservationsByPaymentStatus(status));
    }

    /**
     * Finds a reservation using the name given by the customer.
     * If names are repeated, the user chooses the correct booking by its
     * date and time rather than entering a generated reservation ID.
     */
    protected Reservation findActiveReservationByName() {
        return selectReservationByName(store.getReservations(), "Reservation name");
    }

    protected Reservation selectReservation(List<Reservation> choices, String prompt) {
        return selectReservationByName(choices, prompt);
    }

    private Reservation selectReservationByName(List<Reservation> choices, String prompt) {
        if (choices.isEmpty()) {
            Display.printNotice("No reservations are available to select.");
            return null;
        }

        boolean selectedValid = false;
        Reservation selected = null;

        while (!selectedValid) {
            String name = Input.readReservationName(prompt);
            List<Reservation> matches = new java.util.ArrayList<Reservation>();

            for (Reservation reservation : choices) {
                if (reservation.getReservationName().equalsIgnoreCase(name)) {
                    matches.add(reservation);
                }
            }

            if (matches.isEmpty()) {
                Display.printError("No reservation matches that name in the displayed list.");
                continue;
            }

            if (matches.size() == 1) {
                selected = matches.get(0);
                selectedValid = true;
            } else {
                Display.printSection("Matching Reservations");
                System.out.println("  More than one reservation uses that name. Choose the correct date and time:");
                for (int i = 0; i < matches.size(); i++) {
                    Reservation reservation = matches.get(i);
                    System.out.printf("  [%d] %s at %s | %d guest(s) | %s%n",
                            i + 1, reservation.getDate(), reservation.getTime(),
                            reservation.getGuests(), reservation.getStatus().getLabel());
                }

                int choice = Input.readMenuChoice(1, matches.size());
                selected = matches.get(choice - 1);
                selectedValid = true;
            }
        }

        return selected;
    }


    protected void printTitle(String title) {
        Display.printHeader(title);
    }
}
