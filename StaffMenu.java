import java.util.List;

/** Menu for day-to-day reservation operations performed by restaurant staff. */
public class StaffMenu extends EmployeeMenu {
    public void run() {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n==================== STAFF MENU ====================");
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

            try {
                int choice = Input.readMenuChoice(0, 11);
                // Switch-case avoids a long chain of comparisons for menu numbers.
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
                        Display.printStatusLog(store.getStatusLog());
                        break;
                    default:
                        System.out.println("  Invalid menu option.");
                }
                if (choice != 0) Input.pause();
            } catch (BackException e) {
                System.out.println("  Returned to the staff menu.");
            } catch (ReservationException e) {
                System.out.println("  [Error] " + e.getMessage());
                Input.pause();
            }
        }
    }

    private void reviewUpdates() throws ReservationException {
        List<Reservation> pending = store.getReservationsByStatus(ReservationStatus.PENDING_UPDATE);
        System.out.println("--- Update Requests Waiting for Approval ---");
        Display.printReservations(pending);
        if (pending.isEmpty()) return;

        Reservation selected = selectReservation(pending, "Enter the reservation name to review");
        Display.printDetails(selected);
        if (Input.readYesNo("Approve this update request?")) {
            // Strategy Pattern: choose the charge rule based on the requested change.
            ChargeStrategy charge = selected.requestChangesSchedule() ? new RescheduleCharge() : new NoCharge();
            selected.approveUpdate(charge.computeCharge(selected));
            System.out.println("Update approved. The requested details are now active.");
        } else {
            selected.rejectUpdate();
            System.out.println("Update rejected. The original details remain.");
        }
        store.save();
    }

    /** Staff ask for the saved reservation name before confirming a Pending booking. */
    private void confirmReservation() throws ReservationException {
        List<Reservation> pending = store.getReservationsByStatus(ReservationStatus.PENDING);
        System.out.println("--- Confirm a Reservation ---");
        Display.printReservations(pending);
        if (pending.isEmpty()) return;

        Reservation reservation = selectReservation(pending, "Enter the reservation name");
        Display.printDetails(reservation);
        String givenName = Input.readReservationName("Ask the customer for the reservation name");
        if (!givenName.equalsIgnoreCase(reservation.getReservationName())) {
            System.out.println("Reservation name does not match. The booking remains Pending.");
            return;
        }
        if (Input.readYesNo("Does the reservation information match? Confirm this booking?")) {
            reservation.confirm();
            store.save();
            System.out.println("Reservation confirmed successfully.");
        } else {
            System.out.println("Confirmation cancelled. No record was changed.");
        }
    }

    private void checkInCustomer() throws ReservationException {
        System.out.println("--- Check In Customer ---");
        Reservation reservation = findActiveReservationByName();
        Display.printDetails(reservation);
        if (Input.readYesNo("Confirm customer arrival and check-in?")) {
            reservation.checkIn();
            store.logStatus(reservation);
            System.out.println("Customer checked in successfully.");
        } else {
            System.out.println("Check-in cancelled. No record was changed.");
        }
    }

    private void checkOutCustomer() throws ReservationException {
        System.out.println("--- Check Out Customer ---");
        Reservation reservation = findActiveReservationByName();
        Display.printDetails(reservation);
        if (Input.readYesNo("Confirm check-out?")) {
            reservation.checkOut();
            store.logStatus(reservation);
            System.out.println("Customer checked out successfully.");
        } else {
            System.out.println("Check-out cancelled.");
        }
    }

    private void recordPayment() throws ReservationException {
        System.out.println("--- Record Payment ---");
        Reservation reservation = findActiveReservationByName();
        Display.printDetails(reservation);
        if (reservation.getBalance() <= 0.005) {
            System.out.println("This reservation is already fully paid.");
            return;
        }
        double amount = Input.readAmount("Payment amount in PHP", reservation.getBalance());
        reservation.pay(amount);
        store.save();
        System.out.println("Payment recorded. Updated balance: " + Display.money(reservation.getBalance()));
        System.out.println("Payment status: " + reservation.getPaymentStatus());
    }
}
