package restaurantreservation.menus;

import restaurantreservation.models.*;
import restaurantreservation.strategies.*;
import restaurantreservation.ui.*;
import restaurantreservation.exceptions.*;
import java.util.List;

/** Manager menu: record oversight and approval of cancellation requests. */
public class ManagerMenu extends EmployeeMenu {
    @Override
    public void run() {
        boolean loggedIn = true;
        while (loggedIn) {
            printTitle("MANAGER MENU");
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
            System.out.println("============================================================");

            try {
                int choice = Input.readMenuChoice(0, 10);
                // Each case represents one manager permission or operation.
                switch (choice) {
                    case 0:
                        loggedIn = false;
                        break;
                    case 1:
                        Display.printHeader("ALL CUSTOMER RECORDS");
                        Display.printCustomers(store.getCustomers());
                        break;
                    case 2:
                        Display.printHeader("ACTIVE RESERVATIONS");
                        Display.printReservations(store.getActiveReservations());
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
                        Display.printHeader("CHECKED-IN CUSTOMERS");
                        Display.printReservations(store.getCheckedInReservations());
                        break;
                    case 7:
                        Display.printHeader("CHECKED-OUT CUSTOMERS");
                        Display.printReservations(store.getReservationsByStatus(ReservationStatus.CHECKED_OUT));
                        break;
                    case 8:
                        Display.printHeader("CANCELLED RESERVATIONS");
                        Display.printReservations(store.getCancelled());
                        break;
                    case 9:
                        reviewCancellations();
                        break;
                    case 10:
                        Display.printHeader("CHECK-IN / CHECK-OUT HISTORY");
                        Display.printStatusLog(store.getStatusLog());
                        break;
                    default:
                        Display.printError("Invalid menu option.");
                }
                if (choice != 0) Input.pause();
            } catch (BackException e) {
                Display.printNotice("Returned to the manager menu.");
            } catch (ReservationException e) {
                Display.printError(e.getMessage());
                Input.pause();
            }
        }
    }

    /** Manager approval moves cancelled bookings from RESERVE.txt to CANCELLED.txt. */
    private void reviewCancellations() throws ReservationException {
        List<Reservation> requests = store.getReservationsByStatus(ReservationStatus.CANCEL_REQUESTED);
        Display.printHeader("CANCELLATION REQUESTS");
        Display.printReservations(requests);
        if (requests.isEmpty()) return;

        Reservation selected = selectReservation(requests, "Enter the reservation name to review");
        Display.printDetails(selected);
        double fee = selected.getCancellationFee();
        System.out.println("\n  Cancellation fee required: " + Display.money(fee));
        System.out.println("  Cancellation fee paid    : " + Display.money(selected.getCancellationFeePaid()));
        System.out.println("  Cancellation fee balance  : " + Display.money(selected.getCancellationFeeBalance()));

        if (!selected.isCancellationFeePaid()) {
            if (Input.readYesNo("Reject this unpaid cancellation request?")) {
                selected.rejectCancel();
                store.save();
                Display.printNotice("Cancellation rejected. The reservation returned to its previous status.");
            } else {
                Display.printNotice("Approval is unavailable until the cancellation fee is fully paid.");
            }
            return;
        }

        if (Input.readYesNo("Approve this PAID cancellation?")) {
            selected.approveCancel();
            store.moveToCancelled(selected);
            Display.printSuccess("Cancellation approved and archived as 'Cancelled - Paid Cancellation Fee'.");
        } else {
            Display.printNotice("Cancellation remains pending because the fee has already been paid.");
        }
    }


}
