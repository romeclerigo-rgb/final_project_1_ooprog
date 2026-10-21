import java.util.List;

/** Manager menu: record oversight and approval of cancellation requests. */
public class ManagerMenu extends EmployeeMenu {
    public void run() {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n=================== MANAGER MENU ===================");
            System.out.println("  [1] View all customer records");
            System.out.println("  [2] View all active reservations");
            System.out.println("  [3] Search reservations by reservation name");
            System.out.println("  [4] Sort reservations");
            System.out.println("  [5] Filter reservations");
            System.out.println("  [6] View checked-in customers");
            System.out.println("  [7] View checked-out customers");
            System.out.println("  [8] View cancelled reservations");
            System.out.println("  [9] Approve/reject cancellation requests");
            System.out.println("  [10] View check-in/check-out history");
            System.out.println("  [0] Logout");

            try {
                int choice = Input.readMenuChoice(0, 10);
                // Each case represents one manager permission or operation.
                switch (choice) {
                    case 0:
                        loggedIn = false;
                        break;
                    case 1:
                        Display.printCustomers(store.getCustomers());
                        break;
                    case 2:
                        Display.printReservations(store.getReservations());
                        break;
                    case 3:
                        searchReservations(true);
                        break;
                    case 4:
                        sortReservations();
                        break;
                    case 5:
                        filterReservations();
                        break;
                    case 6:
                        Display.printReservations(store.getReservationsByStatus(ReservationStatus.CHECKED_IN));
                        break;
                    case 7:
                        Display.printReservations(store.getReservationsByStatus(ReservationStatus.CHECKED_OUT));
                        break;
                    case 8:
                        Display.printReservations(store.getCancelled());
                        break;
                    case 9:
                        reviewCancellations();
                        break;
                    case 10:
                        Display.printStatusLog(store.getStatusLog());
                        break;
                    default:
                        System.out.println("  Invalid menu option.");
                }
                if (choice != 0) Input.pause();
            } catch (BackException e) {
                System.out.println("  Returned to the manager menu.");
            } catch (ReservationException e) {
                System.out.println("  [Error] " + e.getMessage());
                Input.pause();
            }
        }
    }

    /** Manager approval moves cancelled bookings from RESERVE.txt to CANCELLED.txt. */
    private void reviewCancellations() throws ReservationException {
        List<Reservation> requests = store.getReservationsByStatus(ReservationStatus.CANCEL_REQUESTED);
        System.out.println("--- Cancellation Requests ---");
        Display.printReservations(requests);
        if (requests.isEmpty()) return;

        Reservation selected = selectReservation(requests, "Reservation ID to review (six digits)");
        Display.printDetails(selected);
        double fee = new CancellationCharge().computeCharge(selected);
        System.out.println("Cancellation fee if approved: " + Display.money(fee));
        if (Input.readYesNo("Approve this cancellation?")) {
            selected.approveCancel(fee);
            store.moveToCancelled(selected);
            System.out.println("Cancellation approved and archived in CANCELLED.txt.");
        } else {
            selected.rejectCancel();
            store.save();
            System.out.println("Cancellation rejected. The reservation returned to its previous status.");
        }
    }
}
