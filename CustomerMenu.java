import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
<<<<<<< HEAD
 * Customer-facing operations. Each customer can only see and modify their own bookings.
 * A step counter lets the user type backslash to return to the previous question.
 */
public class CustomerMenu {
    private final Customer customer;
    private final DataStore store = DataStore.getInstance();
=======
 * Everything a customer can do after logging in.
 *
 * HOW "\" WORKS (going back one question):
 * Each form uses a "step" number. A correct answer does step++ (next question).
 * Typing \ throws BackException; we catch it and do step-- (previous question).
 * On the first question we let it go up to the menu.
 */
public class CustomerMenu {

    private final Customer customer;
    private final DataStore store = DataStore.getInstance();

    // Answers of the reservation form (filled in by askDetail)
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
    private LocalDate date;
    private LocalTime time;
    private int guests;
    private TableType tableType;
    private int tables;

    public CustomerMenu(Customer customer) {
        this.customer = customer;
    }

    public void run() {
<<<<<<< HEAD
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n==================================================");
            System.out.println("  CUSTOMER MENU - " + customer.getName());
=======
        while (true) {
            System.out.println();
            System.out.println("==================================================");
            System.out.println("  CUSTOMER MENU  -  " + customer.getName());
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
            System.out.println("==================================================");
            System.out.println("  [1] Make a reservation");
            System.out.println("  [2] View my reservations");
            System.out.println("  [3] Update a reservation");
            System.out.println("  [4] Cancel a reservation");
            System.out.println("  [5] View payment status");
            System.out.println("  [6] View my profile");
            System.out.println("  [0] Logout");
<<<<<<< HEAD
            System.out.println("  (Type \\ at a question to go back)");
=======
            System.out.println("  (Type \\ at any question to go back)");
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8

            int choice;
            try {
                choice = Input.readMenuChoice(0, 6);
            } catch (BackException e) {
<<<<<<< HEAD
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
=======
                break;   // \ on this menu = back to the main menu
            }
            if (choice == 0) {
                break;
            }
            System.out.println();
            try {
                if (choice == 1) makeReservation();
                else if (choice == 2) viewMyReservations();
                else if (choice == 3) updateReservation();
                else if (choice == 4) cancelReservation();
                else if (choice == 5) viewPaymentStatus();
                else viewProfile();
                Input.pause();
            } catch (BackException e) {
                System.out.println("  Back to the customer menu.");
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
            } catch (ReservationException e) {
                System.out.println("\n  [Error] " + e.getMessage());
                Input.pause();
            }
        }
        System.out.println("You have been logged out.");
    }

<<<<<<< HEAD
    private void makeReservation() {
        System.out.println("--- Make a Reservation ---");
        printBookingRules();
        String reservationName = Input.readReservationName("Reservation name (used by staff to find your booking)");
=======
    // ------------------------------------------------------- make reservation

    private void makeReservation() {
        System.out.println("--- Make a Reservation ---");
        printBookingRules();

>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
        int step = 1;
        while (step <= 5) {
            try {
                askDetail(step, null);
                step++;
            } catch (BackException e) {
<<<<<<< HEAD
                if (step == 1) throw e;
=======
                if (step == 1) {
                    throw e;   // first question: back to the menu
                }
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
                step--;
            }
        }

<<<<<<< HEAD
        Reservation reservation = new Reservation(store.newReservationId(), customer.getCustomerId(),
                reservationName, date, time, guests, tableType, tables);
        store.addReservation(reservation);
        System.out.println("\n  Reservation request saved. Staff must verify the reservation name before confirming it.");
        Display.printDetails(reservation);
        System.out.println("\n  Please keep your Reservation ID: " + reservation.getReservationId());
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
=======
        Reservation r = new Reservation(store.newReservationId(), customer.getCustomerId(),
                date, time, guests, tableType, tables);
        store.addReservation(r);
        System.out.println("\n  Reservation confirmed!");
        Display.printDetails(r);
        System.out.println("\n  Please keep your Reservation ID: " + r.getReservationId());
    }

    /**
     * Asks one question of the reservation form.
     * step 1 = date, 2 = time, 3 = guests, 4 = table type, 5 = number of tables.
     * When updating, "current" is the reservation and Enter keeps its value.
     */
    private void askDetail(int step, Reservation current) {
        boolean updating = current != null;
        if (step == 1) {
            date = Input.readDate("Reservation date (YYYY-MM-DD)",
                    updating ? current.getDate().toString() : null);
        } else if (step == 2) {
            time = Input.readTime("Reservation time, 24-hour (HH:mm)", date,
                    updating ? current.getTime().toString() : null);
        } else if (step == 3) {
            guests = Input.readNumber("Number of guests (1-" + Reservation.MAX_GUESTS + ")", 1, Reservation.MAX_GUESTS,
                    updating ? String.valueOf(current.getGuests()) : null);
        } else if (step == 4) {
            tableType = askTableType(updating ? String.valueOf(current.getTableType().ordinal() + 1) : null);
        } else {
            tables = askTables(updating ? String.valueOf(current.getTables()) : null);
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
        }
    }

    private TableType askTableType(String currentValue) {
        TableType[] types = TableType.values();
        System.out.println("  Table types:");
        for (int i = 0; i < types.length; i++) {
<<<<<<< HEAD
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
=======
            System.out.printf("   [%d] %-8s %d seats per table   %s per table   (you need %d)%n",
                    i + 1, types[i].getLabel(), types[i].getSeats(), Display.money(types[i].getPrice()),
                    types[i].tablesNeeded(guests));
        }
        while (true) {
            int choice = Input.readNumber("Choose table type", 1, types.length, currentValue);
            TableType type = types[choice - 1];
            if (type.tablesNeeded(guests) > Reservation.MAX_TABLES) {
                System.out.println("  [Invalid] " + type.getLabel() + " tables can fit at most "
                        + (type.getSeats() * Reservation.MAX_TABLES) + " guests. Pick a bigger table type.");
                continue;
            }
            return type;
        }
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
    }

    private int askTables(String currentValue) {
        int needed = tableType.tablesNeeded(guests);
<<<<<<< HEAD
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

=======
        while (true) {
            int count = Input.readNumber("Number of tables (" + needed + "-" + Reservation.MAX_TABLES + ")",
                    1, Reservation.MAX_TABLES, currentValue);
            if (count < needed) {
                System.out.println("  [Invalid] " + guests + " guests need at least " + needed + " "
                        + tableType.getLabel() + " table(s) (" + tableType.getSeats() + " seats each).");
                continue;
            }
            return count;
        }
    }

    private void printBookingRules() {
        System.out.println("  Booking hours: " + Reservation.OPENING_TIME + " to " + Reservation.LAST_BOOKING_TIME
                + "   Latest date: " + LocalDate.now().plusYears(1));
    }

    // ------------------------------------------------------ view reservations

>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
    private void viewMyReservations() {
        System.out.println("--- My Reservations ---");
        List<Reservation> mine = store.getReservationsOf(customer.getCustomerId());
        new SortByDate().sort(mine);
        Display.printReservations(mine);
<<<<<<< HEAD
        if (mine.isEmpty()) return;

        boolean viewing = true;
        while (viewing) {
            System.out.println("\nType the six ID digits for details, or press Enter to finish.");
            String id = Input.readReservationIdOrSkip("Reservation ID (without RN-)");
            if (id == null) {
                viewing = false;
            } else {
                Reservation reservation = findInList(mine, id);
                if (reservation == null) {
                    System.out.println("  [Invalid] You have no reservation with ID " + id + ".");
                } else {
                    Display.printDetails(reservation);
                    viewing = false;
                }
            }
        }
    }

    /** Searches the customer's displayed list without a nested loop. */
    private Reservation findInList(List<Reservation> list, String id) {
        for (Reservation reservation : list) {
            if (reservation.getReservationId().equals(id)) return reservation;
        }
        return null;
    }

    private void updateReservation() throws ReservationException {
        System.out.println("--- Update a Reservation ---");
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
=======
        if (mine.isEmpty()) {
            return;
        }
        System.out.println("\n  For full details type the 6 digits of a reservation ID, or press Enter to finish.");
        while (true) {
            String id = Input.readReservationIdOrSkip("Reservation ID (6 digits, without RN-)");
            if (id == null) {
                return;
            }
            for (Reservation r : mine) {
                if (r.getReservationId().equals(id)) {
                    System.out.println();
                    Display.printDetails(r);
                    return;
                }
            }
            System.out.println("  [Invalid] You have no reservation with ID " + id + ".");
        }
    }

    // ----------------------------------------------------- update reservation

    private void updateReservation() throws ReservationException {
        System.out.println("--- Update a Reservation ---");
        Reservation r = null;
        boolean submit = false;

        // step 1 = reservation ID, steps 2-6 = the 5 details, step 7 = confirm
        int step = 1;
        while (step <= 7) {
            try {
                if (step == 1) {
                    r = askMyReservation(false);
                    System.out.println("\n  Enter the new details. Press Enter to keep the value in [brackets].");
                    printBookingRules();
                } else if (step <= 6) {
                    askDetail(step - 1, r);
                } else {
                    if (date.equals(r.getDate()) && time.equals(r.getTime()) && guests == r.getGuests()
                            && tableType == r.getTableType() && tables == r.getTables()) {
                        System.out.println("\n  Nothing was changed.");
                        return;
                    }
                    // STRATEGY: pick the fee rule at runtime
                    ChargeStrategy charge;
                    if (!date.equals(r.getDate()) || !time.equals(r.getTime())) {
                        charge = new RescheduleCharge();
                    } else {
                        charge = new NoCharge();
                    }
                    double fee = charge.computeCharge(r);
                    if (fee > 0) {
                        System.out.println("\n  Note: changing the date or time adds a " + Display.money(fee)
                                + " reschedule fee once approved.");
                    }
                    submit = Input.readYesNo("Submit this update request?");
                }
                step++;
            } catch (BackException e) {
                if (step == 1) {
                    throw e;
                }
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
                step--;
            }
        }

        if (!submit) {
<<<<<<< HEAD
            System.out.println("Update discarded. The reservation was not changed.");
            return;
        }
        reservation.requestUpdate(date, time, guests, tableType, tables);
        store.save();
        System.out.println("Update request sent. It is now Pending Update until staff approve or reject it.");
    }

    private void cancelReservation() throws ReservationException {
        System.out.println("--- Cancel a Reservation ---");
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
            String id = Input.readReservationId("Reservation ID (six digits, without RN-)");
            Reservation reservation = store.findReservation(id);
            if (reservation == null || !reservation.getCustomerId().equals(customer.getCustomerId())) {
                System.out.println("  [Invalid] You have no active reservation with ID " + id + ".");
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

=======
            System.out.println("  Update discarded. Your reservation was not changed.");
            return;
        }
        r.requestUpdate(date, time, guests, tableType, tables);
        store.save();
        System.out.println("\n  Update request sent. Status is now 'Pending Update' until staff approve it.");
    }

    // ----------------------------------------------------- cancel reservation

    private void cancelReservation() throws ReservationException {
        System.out.println("--- Cancel a Reservation ---");
        Reservation r = null;
        boolean confirmed = false;

        int step = 1;
        while (step <= 2) {
            try {
                if (step == 1) {
                    r = askMyReservation(true);
                    double fee = new CancellationCharge().computeCharge(r);
                    System.out.println("\n  A cancellation fee of " + Display.money(fee)
                            + " (20% of the table cost) applies once the manager approves.");
                } else {
                    confirmed = Input.readYesNo("Request cancellation?");
                }
                step++;
            } catch (BackException e) {
                if (step == 1) {
                    throw e;
                }
                step--;
            }
        }

        if (!confirmed) {
            System.out.println("  Your reservation was not changed.");
            return;
        }
        r.requestCancel();
        store.save();
        System.out.println("\n  Cancellation request sent to the manager for approval.");
    }

    /**
     * Asks for one of this customer's reservation IDs (6 digits only).
     * forCancel = true: must be Confirmed or Pending Update.
     * forCancel = false (update): must be Confirmed.
     */
    private Reservation askMyReservation(boolean forCancel) {
        while (true) {
            String id = Input.readReservationId("Reservation ID (6 digits, without RN-)");
            Reservation r = store.findReservation(id);
            if (r == null || !r.getCustomerId().equals(customer.getCustomerId())) {
                System.out.println("  [Invalid] You have no active reservation with ID " + id + ".");
                continue;
            }
            ReservationStatus s = r.getStatus();
            if (!forCancel && s != ReservationStatus.CONFIRMED) {
                System.out.println("  [Invalid] Only Confirmed reservations can be updated. " + id + " is " + s.getLabel() + ".");
                continue;
            }
            if (forCancel && s != ReservationStatus.CONFIRMED && s != ReservationStatus.PENDING_UPDATE) {
                System.out.println("  [Invalid] " + id + " can't be cancelled because it is " + s.getLabel() + ".");
                continue;
            }
            Display.printDetails(r);
            return r;
        }
    }

    // ---------------------------------------------------------- other views

>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
    private void viewPaymentStatus() {
        System.out.println("--- My Payment Status ---");
        List<Reservation> mine = store.getReservationsOf(customer.getCustomerId());
        new SortByDate().sort(mine);
        Display.printPaymentSummary(mine);
<<<<<<< HEAD
        System.out.println("Payments are made at the counter and recorded by staff.");
=======
        System.out.println("\n  Pay at the counter. Staff record your payment.");
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
    }

    private void viewProfile() {
        System.out.println("--- My Profile ---");
        System.out.println("  Customer ID : " + customer.getCustomerId());
<<<<<<< HEAD
        System.out.println("  Full name   : " + customer.getName());
        System.out.println("  Address     : " + customer.getAddress());
=======
        System.out.println("  First name  : " + customer.getFirstName());
        System.out.println("  Last name   : " + customer.getLastName());
        if (customer.getDuplicateNumber() > 0) {
            System.out.println("  Saved as    : " + customer.getName());
        }
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
        System.out.println("  Contact     : " + customer.getContactNumber());
        System.out.println("  Email       : " + customer.getEmail());
    }
}
