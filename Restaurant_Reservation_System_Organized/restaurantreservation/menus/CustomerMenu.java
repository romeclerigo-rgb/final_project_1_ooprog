package restaurantreservation.menus;

import restaurantreservation.models.*;
import restaurantreservation.strategies.*;
import restaurantreservation.storage.*;
import restaurantreservation.ui.*;
import restaurantreservation.exceptions.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Customer-facing operations. Each customer can only see and modify their own bookings.
 * A step counter lets the user type backslash to return to the previous question.
 */
public class CustomerMenu {
    private final Customer customer;
    private final DataStore store = DataStore.getInstance();
    private LocalDate date;
    private LocalTime time;
    private int guests;
    private TableType tableType;
    private int tables;

    public CustomerMenu(Customer customer) {
        this.customer = customer;
    }

    public void run() {
        boolean loggedIn = true;
        while (loggedIn) {
            Display.printHeader("CUSTOMER MENU - " + customer.getName());
            System.out.println("  [1] Make a reservation");
            System.out.println("  [2] View my reservations");
            System.out.println("  [3] Update a reservation");
            System.out.println("  [4] Cancel a reservation");
            System.out.println("  [5] View payment status");
            System.out.println("  [6] View my profile");
            System.out.println("  [0] Logout");
            System.out.println("============================================================");
            System.out.println("  Tip: Type \\ at a question to return to the previous step.");

            int choice;
            try {
                choice = Input.readMenuChoice(0, 6);
            } catch (BackException e) {
                break;
            }
            try {
                // Keep every customer menu choice, including logout, in one switch.
                switch (choice) {
                    case 0:
                        loggedIn = false;
                        break;
                    case 1:
                        makeReservation();
                        break;
                    case 2:
                        viewMyReservations();
                        break;
                    case 3:
                        updateReservation();
                        break;
                    case 4:
                        cancelReservation();
                        break;
                    case 5:
                        viewPaymentStatus();
                        break;
                    case 6:
                        viewProfile();
                        break;
                    default:
                        System.out.println("  Invalid menu option.");
                }
                // Do not pause after logout; the next screen is the main menu.
                if (choice != 0) Input.pause();
            } catch (BackException e) {
                Display.printNotice("Returned to the customer menu.");
            } catch (ReservationException e) {
                Display.printError(e.getMessage());
                Input.pause();
            }
        }
        Display.printNotice("You have been logged out.");
    }

    private void makeReservation() throws ReservationException {
        printTitle("MAKE A RESERVATION");
        printBookingRules();
        System.out.println();
        String reservationName = Input.readReservationName("Enter Reservation Name (used by staff to find your booking)");
        int step = 1;
        while (step <= 5) {
            try {
                askDetail(step, null);
                step++;
            } catch (BackException e) {
                if (step == 1) throw e;
                step--;
            }
        }

        Reservation reservation = new Reservation(store.newReservationId(), customer.getCustomerId(),
                reservationName, date, time, guests, tableType, tables);

        double deposit = reservation.getTableCost() * 0.10;
        Display.printSection("10% Reservation Security Deposit");
        System.out.println("  Total reservation amount : " + Display.money(reservation.getTableCost()));
        System.out.println("  Required deposit (10%)   : " + Display.money(deposit));
        double paidDeposit = Input.readAmount("Pay security deposit in PHP", deposit);
        if (Math.abs(paidDeposit - deposit) > 0.005) {
            throw new ReservationException(String.format("The full 10%% security deposit of PHP %,.2f is required.", deposit));
        }
        reservation.pay(paidDeposit);
        store.addReservation(reservation);
        Display.printSuccess("Reservation created. Your 10% security deposit has been recorded.");
        Display.printReservationConfirmation(reservation);
    }

    /** Steps: date, time, guest count, table type, and number of tables. */
    private void askDetail(int step, Reservation current) {
        boolean updating = current != null;
        // A switch keeps the form order easy to read and maintain.
        switch (step) {
            case 1:
                date = Input.readDate("Reservation date (YYYY-MM-DD)",
                        updating ? current.getDate().toString() : null);
                break;
            case 2:
                time = Input.readTime("Reservation time (24-hour HH:mm)", date,
                        updating ? current.getTime().toString() : null);
                break;
            case 3:
                guests = Input.readNumber("Number of guests (1-" + Reservation.MAX_GUESTS + ")",
                        1, Reservation.MAX_GUESTS, updating ? String.valueOf(current.getGuests()) : null);
                break;
            case 4:
                tableType = askTableType(updating ? String.valueOf(current.getTableType().ordinal() + 1) : null);
                break;
            case 5:
                tables = askTables(updating ? String.valueOf(current.getTables()) : null);
                break;
            default:
                throw new IllegalArgumentException("Unknown reservation form step: " + step);
        }
    }

    private TableType askTableType(String currentValue) {
        TableType[] types = TableType.values();
        System.out.println("  Table types:");
        for (int i = 0; i < types.length; i++) {
            System.out.printf("  [%d] %-8s | %d seats | %s per table | at least %d needed%n",
                    i + 1, types[i].getLabel(), types[i].getSeats(),
                    Display.money(types[i].getPrice()), types[i].tablesNeeded(guests));
        }
        boolean valid = false;
        TableType selected = types[0];
        while (!valid) {
            int choice = Input.readNumber("Choose table type", 1, types.length, currentValue);
            selected = types[choice - 1];
            if (selected.tablesNeeded(guests) > Reservation.MAX_TABLES) {
                System.out.println("  [Invalid] This table type cannot seat " + guests + " guests within the table limit.");
                currentValue = null;   // Enter can't keep a table type that no longer fits
            } else {
                valid = true;
            }
        }
        return selected;
    }

    private int askTables(String currentValue) {
        int needed = tableType.tablesNeeded(guests);
        boolean valid = false;
        int count = needed;
        while (!valid) {
            count = Input.readNumber("Number of tables (" + needed + "-" + Reservation.MAX_TABLES + ")",
                    1, Reservation.MAX_TABLES, currentValue);
            if (count < needed) {
                System.out.println("  [Invalid] " + guests + " guests need at least " + needed
                        + " " + tableType.getLabel() + " table(s).");
                currentValue = null;
            } else {
                valid = true;
            }
        }
        return count;
    }

    private void printBookingRules() {
        Display.printSection("Booking Guidelines");
        System.out.println("  Booking hours       : " + Reservation.OPENING_TIME + " to " + Reservation.LAST_BOOKING_TIME);
        System.out.println("  Latest booking date : " + LocalDate.now().plusYears(1));
        System.out.println("  Maximum guests      : " + Reservation.MAX_GUESTS);
    }

    private void viewMyReservations() {
        printTitle("My Reservations");
        List<Reservation> mine = store.getReservationsOf(customer.getCustomerId());

        if (mine.isEmpty()) {
            Display.printNotice("No reservations yet.");
            return;
        }
        new SortByDate().sort(mine);
        Display.printReservations(mine);
        System.out.println("============================================================");

        boolean viewing = true;
        while (viewing) {
            System.out.println("\nEnter your [Reservation Name] to view details, or press [Enter] to finish.");
            String name = Input.readTextOrSkip("Reservation name");
            if (name.isEmpty()) {
                viewing = false;
            } else {
                Reservation reservation = findInList(mine, name);
                    if (reservation == null) {
                        Display.printError("You have no reservation named " + name + ".");
                        viewing = false;
                    } else {
                        Display.printDetails(reservation);
                        viewing = false;
                    }
            }
        }
    }

    /** Searches the customer's reservations by reservation name. */
    private Reservation findInList(List<Reservation> list, String name) {
        for (Reservation reservation : list) {
            if (reservation.getReservationName().equalsIgnoreCase(name)) return reservation;
        }
        return null;
    }

    private void updateReservation() throws ReservationException {
        printTitle("Update Reservation");
        List<Reservation> mine = store.getReservationsOf(customer.getCustomerId());
            boolean hasConfirmedReservation = false;

            for (Reservation reservation : mine) {
                if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
                    hasConfirmedReservation = true;
                    break;
                }
            }

            if (!hasConfirmedReservation) {
                System.out.println("No reservations available to update yet.");
                return;
            }
        Reservation reservation = null;
        boolean submit = false;
        int step = 1;
        while (step <= 7) {
            try {
                // Step 1 selects the booking, steps 2-6 edit its five details,
                // and step 7 asks the customer to confirm the request.
                switch (step) {
                    case 1:
                        reservation = askMyReservation(false);
                        System.out.println("\nEnter new details. Press Enter to keep the value in brackets.");
                        printBookingRules();
                        break;
                    case 7:
                        if (date.equals(reservation.getDate()) && time.equals(reservation.getTime())
                                && guests == reservation.getGuests() && tableType == reservation.getTableType()
                                && tables == reservation.getTables()) {
                            System.out.println("Nothing was changed.");
                            return;
                        }
                        // Choose the fee rule according to whether the schedule changed.
                        ChargeStrategy charge;
                        if (!date.equals(reservation.getDate()) || !time.equals(reservation.getTime())) {
                            charge = new RescheduleCharge();
                        } else {
                            charge = new NoCharge();
                        }
                        double fee = charge.computeCharge(reservation);
                        if (fee > 0) {
                            Display.printNotice("A reschedule fee of " + Display.money(fee)
                                    + " will apply if staff approve this request.");
                        }
                        submit = Input.readYesNo("Submit this update request?");
                        break;
                    default:
                        askDetail(step - 1, reservation);
                        break;
                }
                step++;
            } catch (BackException e) {
                if (step == 1) throw e;
                step--;
            }
        }

        if (!submit) {
            System.out.println("Update discarded. The reservation was not changed.");
            return;
        }
        reservation.requestUpdate(date, time, guests, tableType, tables);
        store.save();
        Display.printSuccess("Update request sent. Status: Pending Update until staff review it.");
    }

    private void cancelReservation() throws ReservationException {
        printTitle("Cancel Reservation");
        List<Reservation> mine = store.getReservationsOf(customer.getCustomerId());
        boolean hasCancellableReservation = false;
        for (Reservation reservation : mine) {
            ReservationStatus status = reservation.getStatus();
            if (status == ReservationStatus.PENDING || status == ReservationStatus.CONFIRMED
                    || status == ReservationStatus.PENDING_UPDATE) {
                hasCancellableReservation = true;
                break;
            }
        }
        if (!hasCancellableReservation) {
            System.out.println("No reservations available to cancel yet.");
            return;
        }

        Reservation reservation = askMyReservation(true);
        ReservationStatus status = reservation.getStatus();

        if (!Input.readYesNo("Confirm cancellation of this reservation?")) {
            System.out.println("Cancellation discarded.");
            return;
        }

        if (status == ReservationStatus.PENDING) {
            reservation.cancelPendingWithoutFee();
            store.moveToCancelled(reservation);
            Display.printSuccess("Pending reservation cancelled immediately. No cancellation fee or manager approval is required.");
            return;
        }

        reservation.requestCancel();
        double fee = reservation.getCancellationFee();
        Display.printNotice("A cancellation fee of " + Display.money(fee)
                + " (20% of table cost) is required before manager approval.");
        double paid = Input.readAmount("Pay cancellation fee in PHP", fee);
        if (Math.abs(paid - fee) > 0.005) {
            throw new ReservationException(String.format("The full cancellation fee of PHP %,.2f is required before manager approval.", fee));
        }
        reservation.payCancellationFee(paid);
        store.save();
        Display.printSuccess("Cancellation fee paid. Your cancellation is now waiting for manager approval.");
    }

    private Reservation askMyReservation(boolean forCancel) {
        boolean foundValidReservation = false;
        Reservation selected = null;
        
        while (!foundValidReservation) {
            String name = Input.readReservationName("Reservation name");
            Reservation reservation = null;
            for (Reservation item : store.getReservationsOf(customer.getCustomerId())) {
                if (item.getReservationName().equalsIgnoreCase(name)) {
                    reservation = item;
                    break;
                }
            }
            if (reservation == null) {
                Display.printError("You have no reservation named " + name + ".");
                throw new BackException();
            }
            ReservationStatus status = reservation.getStatus();
            if (!forCancel && status != ReservationStatus.CONFIRMED) {
                System.out.println("  [Invalid] Only confirmed reservations can be updated. Current status: " + status.getLabel());
                continue;
            }
            if (forCancel && status != ReservationStatus.PENDING && status != ReservationStatus.CONFIRMED
                    && status != ReservationStatus.PENDING_UPDATE) {
                System.out.println("  [Invalid] This reservation cannot be cancelled while status is " + status.getLabel() + ".");
                continue;
            }
            Display.printDetails(reservation);
            selected = reservation;
            foundValidReservation = true;
        }
        return selected;
    }

    private void viewPaymentStatus() {
        printTitle("View Payment Status");
        System.out.println();
        List<Reservation> mine = store.getReservationsOf(customer.getCustomerId());
        if (mine.isEmpty()) {
            Display.printNotice("No reservations yet.");
            return;
        }
        new SortByDate().sort(mine);
        Display.printPaymentSummary(mine);
        System.out.println();
        Display.printNotice("Payments are made at the counter and recorded by staff.");
    }

    private void viewProfile() {
        printTitle("My Profile");
        Display.printSection("Account Information");
        System.out.printf("  %-12s: %s%n", "Full name", customer.getName());
        System.out.printf("  %-12s: %s%n", "Address", customer.getAddress());
        System.out.printf("  %-12s: %s%n", "Email", customer.getEmail());
        System.out.println("============================================================");
    }

    private void printTitle(String title) {
        Display.printHeader(title.toUpperCase());
    }

}
