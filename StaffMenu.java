import java.util.List;

/** Menu for day-to-day reservation operations performed by restaurant staff. */
public class StaffMenu extends EmployeeMenu {
    @Override
    public void run() {
        boolean loggedIn = true;
        while (loggedIn) {
            printTitle("STAFF MENU");
            System.out.println("  [1] View customer information");
            System.out.println("  [2] View reservations");
            System.out.println("  [3] Search by reservation name");
            System.out.println("  [4] Approve/reject reservation updates");
            System.out.println("  [5] Verify reservation name and confirm booking");
            System.out.println("  [6] Check in customer");
            System.out.println("  [7] Check out customer");
            System.out.println("  [8] Record payment");
            System.out.println("  [9] Sort reservations");
            System.out.println("  [10] Filter reservations");
            System.out.println("  [11] View check-in/check-out history");
            System.out.println("  [0] Logout");
            System.out.println("============================================================");

            try {
                int choice = Input.readMenuChoice(0, 11);
                // Switch-case avoids a long chain of comparisons for menu numbers.
                switch (choice) {
                    case 0:
                        loggedIn = false;
                        break;
                    case 1:
                        Display.printHeader("CUSTOMER INFORMATION");
                        Display.printCustomers(store.getCustomers());
                        break;
                    case 2:
                        Display.printHeader("ALL RESERVATIONS");
                        Display.printReservations(store.getReservations());
                        break;
                    case 3:
                        searchReservations(false);
                        break;
                    case 4:
                        reviewUpdates();
                        break;
                    case 5:
                        confirmReservation();
                        break;
                    case 6:
                        checkInCustomer();
                        break;
                    case 7:
                        checkOutCustomer();
                        break;
                    case 8:
                        recordPayment();
                        break;
                    case 9:
                        sortReservations();
                        break;
                    case 10:
                        filterReservations();
                        break;
                    case 11:
                        Display.printHeader("CHECK-IN / CHECK-OUT HISTORY");
                        Display.printStatusLog(store.getStatusLog());
                        break;
                    default:
                        Display.printError("Invalid menu option.");
                }
                if (choice != 0) Input.pause();
            } catch (BackException e) {
                Display.printNotice("Returned to the staff menu.");
            } catch (ReservationException e) {
                Display.printError(e.getMessage());
                Input.pause();
            }
        }
    }

    private void reviewUpdates() throws ReservationException {
        List<Reservation> pending = store.getReservationsByStatus(ReservationStatus.PENDING_UPDATE);
        Display.printHeader("UPDATE REQUESTS - PENDING APPROVAL");
        Display.printReservations(pending);
        if (pending.isEmpty()) return;

        Reservation selected = selectReservation(pending, "Enter the reservation name to review");
        Display.printDetails(selected);
        if (Input.readYesNo("Approve this update request?")) {
            // Strategy Pattern: choose the charge rule based on the requested change.
            ChargeStrategy charge = selected.requestChangesSchedule() ? new RescheduleCharge() : new NoCharge();
            selected.approveUpdate(charge.computeCharge(selected));
            Display.printSuccess("Update approved. The requested details are now active.");
        } else {
            selected.rejectUpdate();
            Display.printNotice("Update rejected. The original details remain.");
        }
        store.save();
    }

    /** Staff ask for the saved reservation name before confirming a Pending booking. */
    private void confirmReservation() throws ReservationException {
        List<Reservation> pending = store.getReservationsByStatus(ReservationStatus.PENDING);
        Display.printHeader("CONFIRM A RESERVATION");
        Display.printReservations(pending);
        if (pending.isEmpty()) return;

        Reservation reservation = selectReservation(pending, "Enter the reservation name");
        Display.printDetails(reservation);
        String givenName = Input.readReservationName("Ask the customer for the reservation name");
        if (!givenName.equalsIgnoreCase(reservation.getReservationName())) {
            Display.printError("Reservation name does not match. The booking remains Pending.");
            return;
        }
        if (Input.readYesNo("Does the reservation information match? Confirm this booking?")) {
            reservation.confirm();
            store.save();
            Display.printSuccess("Reservation confirmed successfully.");
        } else {
            Display.printNotice("Confirmation cancelled. No record was changed.");
        }
    }

    private void checkInCustomer() throws ReservationException {
        Display.printHeader("CUSTOMER CHECK-IN");
        Reservation reservation = findActiveReservationByName();
        Display.printDetails(reservation);
        if (Input.readYesNo("Confirm customer arrival and check-in?")) {
            reservation.checkIn();
            store.logStatus(reservation);
            Display.printSuccess("Customer checked in successfully.");
        } else {
            Display.printNotice("Check-in cancelled. No record was changed.");
        }
    }

    private void checkOutCustomer() throws ReservationException {
        Display.printHeader("CUSTOMER CHECK-OUT");
        Reservation reservation = findActiveReservationByName();
        Display.printDetails(reservation);
        if (Input.readYesNo("Confirm check-out?")) {
            reservation.checkOut();
            store.logStatus(reservation);
            Display.printSuccess("Customer checked out successfully.");
        } else {
            Display.printNotice("Check-out cancelled.");
        }
    }

    private void recordPayment() throws ReservationException {
        Display.printHeader("RECORD PAYMENT");
        Reservation reservation = findActiveReservationByName();
        Display.printDetails(reservation);
        if (reservation.getBalance() <= 0.005) {
            Display.printNotice("This reservation is already fully paid.");
            return;
        }
        double amount = Input.readAmount("Payment amount in PHP", reservation.getBalance());
        reservation.pay(amount);
        store.save();
        Display.printSuccess("Payment recorded successfully.");
        System.out.println("  Updated balance : " + Display.money(reservation.getBalance()));
        System.out.println("  Payment status  : " + reservation.getPaymentStatus());
    }
}
