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
            System.out.println("\n==================================================");
            System.out.println("  CUSTOMER MENU - " + customer.getName());
            System.out.println("==================================================");
            System.out.println("  [1] Make a reservation");
            System.out.println("  [2] View my reservations");
            System.out.println("  [3] Update a reservation");
            System.out.println("  [4] Cancel a reservation");
            System.out.println("  [5] View payment status");
            System.out.println("  [6] View my profile");
            System.out.println("  [0] Logout");
            System.out.println("==================================================");
            System.out.println("  (Type \\ at a question to go back)");

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
                System.out.println("  Returned to the customer menu.");
            } catch (ReservationException e) {
                System.out.println("\n  [Error] " + e.getMessage());
                Input.pause();
            }
        }
        System.out.println("You have been logged out.");
    }

    private void makeReservation() {
        printTitle("MAKE A RESERVATION");
        printBookingRules();
        String reservationName = Input.readReservationName("Reservation name (used by staff to find your booking)");
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
        store.addReservation(reservation);
        System.out.println("\n  Reservation request saved. Staff must verify the reservation name before confirming it.");
        Display.printDetails(reservation);
        System.out.println("\n  Please remember your Reservation name: " + reservation.getReservationName());
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
        System.out.println("  Booking hours: " + Reservation.OPENING_TIME + " to " + Reservation.LAST_BOOKING_TIME);
        System.out.println("  Latest booking date: " + LocalDate.now().plusYears(1));
    }

    private void viewMyReservations() {
        printTitle("My Reservations");
        List<Reservation> mine = store.getReservationsOf(customer.getCustomerId());

        if (mine.isEmpty()) {
            System.out.println("No reservations yet.");
            return;
        }
            new SortByDate().sort(mine);
            Display.printReservations(mine);

            boolean viewings = true;
            System.out.println("==================================================");

            while (viewings) {
                System.out.println("\nEnter your reservation name for details, or press Enter to finish.");
                String name = Input.readTextOrSkip("Reservation name");

                if (name.isEmpty()) {
                    viewings = false;
                } else {
                    Reservation reservation = findInList(mine, name);

                    if (reservation == null) {
                        System.out.println("  [Invalid] You have no reservation named " + name + ".");
                    } else {
                        Display.printDetails(reservation);
                        viewings = false;
                    }
                }
            }
        

        boolean viewing = true;
        System.out.println("==================================================");
        while (viewing) {
            System.out.println("\nEnter your reservation name for details, or press Enter to finish.");
            String name = Input.readTextOrSkip("Reservation name");
            if (name.isEmpty()) {
                viewing = false;
            } else {
                Reservation reservation = findInList(mine, name);
                if (reservation == null) {
                    System.out.println("  [Invalid] You have no reservation named " + name + ".");
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
                            System.out.println("A reschedule fee of " + Display.money(fee)
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
        System.out.println("Update request sent. It is now Pending Update until staff approve or reject it.");
    }

    private void cancelReservation() throws ReservationException {
        printTitle("Cancel Reservation");
            List<Reservation> mine = store.getReservationsOf(customer.getCustomerId());
            boolean hasCancellableReservation = false;

            for (Reservation reservation : mine) {
                ReservationStatus status = reservation.getStatus();

                if (status == ReservationStatus.PENDING
                        || status == ReservationStatus.CONFIRMED
                        || status == ReservationStatus.PENDING_UPDATE) {
                    hasCancellableReservation = true;
                    break;
                }
            }

            if (!hasCancellableReservation) {
                System.out.println("No reservations available to cancel yet.");
                return;
            }
        Reservation reservation = null;
        boolean confirmed = false;
        int step = 1;
        while (step <= 2) {
            try {
                // Step 1 selects the booking; step 2 confirms the request.
                switch (step) {
                    case 1:
                        reservation = askMyReservation(true);
                        double fee = new CancellationCharge().computeCharge(reservation);
                        System.out.println("A cancellation fee of " + Display.money(fee)
                                + " (20% of table cost) applies if the manager approves.");
                        break;
                    case 2:
                        confirmed = Input.readYesNo("Request cancellation?");
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown cancellation form step: " + step);
                }
                step++;
            } catch (BackException e) {
                if (step == 1) throw e;
                step--;
            }
        }
        if (!confirmed) {
            System.out.println("Cancellation request discarded.");
            return;
        }
        reservation.requestCancel();
        store.save();
        System.out.println("Cancellation request sent to the manager for approval.");
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
                System.out.println("  [Invalid] You have no reservation named " + name + ".");
                continue;
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
        List<Reservation> mine = store.getReservationsOf(customer.getCustomerId());
        if (mine.isEmpty()) {
            System.out.println("No reservations yet.");
            return;
        }
        new SortByDate().sort(mine);
        Display.printPaymentSummary(mine);
        System.out.println("Payments are made at the counter and recorded by staff.");
    }

    private void viewProfile() {
        printTitle("My Profile");
        System.out.println("  Full name   : " + customer.getName());
        System.out.println("  Address     : " + customer.getAddress());
        System.out.println("  Email       : " + customer.getEmail());
    }

    private void printTitle(String title) {
        String border = "==================================================";
        System.out.println(border);
        int padding = Math.max(0, (border.length() - title.length()) / 2);
        System.out.println(" ".repeat(padding) + title);
        System.out.println(border);
    }

}
