import java.time.LocalDate;
import java.time.LocalTime;

/**
<<<<<<< HEAD
 * Represents one restaurant booking and controls its valid status/payment changes.
 * Encapsulation: every field is private; operations validate rules before changing state.
 * The reservation name is the name staff can ask for to confirm a booking.
 */
public class Reservation {
    public static final LocalTime OPENING_TIME = LocalTime.of(10, 0);
    public static final LocalTime LAST_BOOKING_TIME = LocalTime.of(21, 0);
    public static final int MAX_TABLES = 10;
    public static final int MAX_GUESTS = 80;

    private final String reservationId;
    private final String customerId;
    private final String reservationName;
=======
 * One restaurant reservation. Saved in data/RESERVE.txt (or CANCELLED.txt once cancelled).
 *
 * ENCAPSULATION: all fields are private. The status and the money can only change
 * through methods like checkIn() or pay(), and those methods check the rules first.
 */
public class Reservation {

    // Restaurant rules
    public static final LocalTime OPENING_TIME = LocalTime.of(10, 0);
    public static final LocalTime LAST_BOOKING_TIME = LocalTime.of(21, 0);
    public static final int MAX_TABLES = 10;
    public static final int MAX_GUESTS = 80;   // 10 Family tables x 8 seats

    private String reservationId;
    private String customerId;
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
    private LocalDate date;
    private LocalTime time;
    private int guests;
    private TableType tableType;
    private int tables;
    private ReservationStatus status;
<<<<<<< HEAD
    // Used so rejecting a cancellation can restore its previous active status.
    private ReservationStatus statusBeforeCancel;
    private double amountPaid;
    private double extraCharges;

    // Pending values stay separate until staff approve the requested update.
=======
    private double amountPaid;
    private double extraCharges;

    // The change the customer asked for (empty until they request an update)
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
    private LocalDate newDate;
    private LocalTime newTime;
    private int newGuests;
    private TableType newTableType;
    private int newTables;

<<<<<<< HEAD
    public Reservation(String reservationId, String customerId, String reservationName,
                       LocalDate date, LocalTime time, int guests, TableType tableType, int tables) {
        this.reservationId = reservationId;
        this.customerId = customerId;
        this.reservationName = reservationName;
=======
    /** A brand-new reservation: Confirmed and unpaid. */
    public Reservation(String reservationId, String customerId, LocalDate date, LocalTime time,
                       int guests, TableType tableType, int tables) {
        this.reservationId = reservationId;
        this.customerId = customerId;
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
        this.date = date;
        this.time = time;
        this.guests = guests;
        this.tableType = tableType;
        this.tables = tables;
<<<<<<< HEAD
        this.status = ReservationStatus.PENDING;
        this.amountPaid = 0.0;
        this.extraCharges = 0.0;
    }

    /** Backward compatibility for old code that did not yet store a reservation name. */
    public Reservation(String reservationId, String customerId, LocalDate date, LocalTime time,
                       int guests, TableType tableType, int tables) {
        this(reservationId, customerId, "Reservation " + reservationId, date, time, guests, tableType, tables);
    }

    public String getReservationId() { return reservationId; }
    public String getCustomerId() { return customerId; }
    public String getReservationName() { return reservationName; }
=======
        this.status = ReservationStatus.CONFIRMED;
        this.amountPaid = 0;
        this.extraCharges = 0;
    }

    // ----------------------------------------------------------- getters

    public String getReservationId() { return reservationId; }
    public String getCustomerId() { return customerId; }
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
    public LocalDate getDate() { return date; }
    public LocalTime getTime() { return time; }
    public int getGuests() { return guests; }
    public TableType getTableType() { return tableType; }
    public int getTables() { return tables; }
    public ReservationStatus getStatus() { return status; }
    public double getAmountPaid() { return amountPaid; }
    public double getExtraCharges() { return extraCharges; }
    public LocalDate getNewDate() { return newDate; }
    public LocalTime getNewTime() { return newTime; }
    public int getNewGuests() { return newGuests; }
    public TableType getNewTableType() { return newTableType; }
    public int getNewTables() { return newTables; }

    public boolean hasRequestedUpdate() {
        return newDate != null;
    }

<<<<<<< HEAD
    public boolean requestChangesSchedule() {
        return hasRequestedUpdate() && (!newDate.equals(date) || !newTime.equals(time));
    }

=======
    /** True if the requested change moves the date or time (this has a fee). */
    public boolean requestChangesSchedule() {
        return !newDate.equals(date) || !newTime.equals(time);
    }

    // -------------------------------------------------------------- money

    /** Table price x number of tables. */
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
    public double getTableCost() {
        return tableType.getPrice() * tables;
    }

<<<<<<< HEAD
    /** A cancelled reservation is charged its cancellation fee instead of the full table cost. */
    public double getTotal() {
        return status == ReservationStatus.CANCELLED ? extraCharges : getTableCost() + extraCharges;
=======
    /** What the customer has to pay in total. A cancelled booking only owes its fee. */
    public double getTotal() {
        if (status == ReservationStatus.CANCELLED) {
            return extraCharges;
        }
        return getTableCost() + extraCharges;
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
    }

    public double getBalance() {
        double balance = getTotal() - amountPaid;
<<<<<<< HEAD
        return balance < 0.005 ? 0.0 : balance;
=======
        return balance < 0.005 ? 0 : balance;
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
    }

    public double getRefundDue() {
        double refund = amountPaid - getTotal();
<<<<<<< HEAD
        return refund < 0.005 ? 0.0 : refund;
    }

    public String getPaymentStatus() {
        if (amountPaid < 0.005) return "Unpaid";
        if (getBalance() == 0.0) return "Fully Paid";
        return "Partially Paid";
    }

    /** Numeric order used by sorting: fully paid first, partial second, unpaid last. */
    public int getPaymentRank() {
        // Payment status is one of three fixed labels, so use switch-case.
        switch (getPaymentStatus()) {
            case "Fully Paid":
                return 1;
            case "Partially Paid":
                return 2;
            case "Unpaid":
            default:
                return 3;
        }
    }

    public void requestUpdate(LocalDate date, LocalTime time, int guests,
                              TableType tableType, int tables) throws ReservationException {
        if (status != ReservationStatus.CONFIRMED) {
            throw new ReservationException("Only confirmed reservations can be updated. Current status: " + status.getLabel() + ".");
        }
        validateBookingDetails(date, time, guests, tableType, tables);
        newDate = date;
        newTime = time;
        newGuests = guests;
        newTableType = tableType;
        newTables = tables;
        status = ReservationStatus.PENDING_UPDATE;
    }

    public void approveUpdate(double fee) throws ReservationException {
        if (status != ReservationStatus.PENDING_UPDATE || !hasRequestedUpdate()) {
            throw new ReservationException("There is no update request waiting for approval.");
=======
        return refund < 0.005 ? 0 : refund;
    }

    /** "Unpaid", "Partially Paid" or "Fully Paid". */
    public String getPaymentStatus() {
        if (amountPaid < 0.005) {
            return "Unpaid";
        }
        if (getBalance() == 0) {
            return "Fully Paid";
        }
        return "Partially Paid";
    }

    /** Used for sorting: Fully Paid = 1, Partially Paid = 2, Unpaid = 3. */
    public int getPaymentRank() {
        String payment = getPaymentStatus();
        if (payment.equals("Fully Paid")) {
            return 1;
        }
        if (payment.equals("Partially Paid")) {
            return 2;
        }
        return 3;
    }

    // ----------------------------------------------------- status changes

    public void requestUpdate(LocalDate date, LocalTime time, int guests, TableType tableType, int tables)
            throws ReservationException {
        if (status != ReservationStatus.CONFIRMED) {
            throw new ReservationException("Only Confirmed reservations can be updated. This one is " + status.getLabel() + ".");
        }
        this.newDate = date;
        this.newTime = time;
        this.newGuests = guests;
        this.newTableType = tableType;
        this.newTables = tables;
        this.status = ReservationStatus.PENDING_UPDATE;
    }

    public void approveUpdate(double fee) throws ReservationException {
        if (status != ReservationStatus.PENDING_UPDATE) {
            throw new ReservationException("This reservation has no update waiting for approval.");
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
        }
        date = newDate;
        time = newTime;
        guests = newGuests;
        tableType = newTableType;
        tables = newTables;
<<<<<<< HEAD
        extraCharges += fee;
=======
        extraCharges = extraCharges + fee;
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
        clearRequestedUpdate();
        status = ReservationStatus.CONFIRMED;
    }

    public void rejectUpdate() throws ReservationException {
        if (status != ReservationStatus.PENDING_UPDATE) {
<<<<<<< HEAD
            throw new ReservationException("There is no update request waiting for approval.");
=======
            throw new ReservationException("This reservation has no update waiting for approval.");
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
        }
        clearRequestedUpdate();
        status = ReservationStatus.CONFIRMED;
    }

<<<<<<< HEAD
    /** Staff confirm a new booking only after checking its reservation name. */
    public void confirm() throws ReservationException {
        if (status != ReservationStatus.PENDING) {
            throw new ReservationException("Only Pending reservations can be confirmed.");
        }
        status = ReservationStatus.CONFIRMED;
    }

    public void requestCancel() throws ReservationException {
        if (status != ReservationStatus.PENDING && status != ReservationStatus.CONFIRMED
                && status != ReservationStatus.PENDING_UPDATE) {
            throw new ReservationException("This reservation cannot be cancelled while its status is " + status.getLabel() + ".");
        }
        // A pending-update request is cleared, so rejection restores Confirmed instead.
        statusBeforeCancel = status == ReservationStatus.PENDING_UPDATE ? ReservationStatus.CONFIRMED : status;
=======
    public void requestCancel() throws ReservationException {
        if (status != ReservationStatus.CONFIRMED && status != ReservationStatus.PENDING_UPDATE) {
            throw new ReservationException("This reservation can't be cancelled because it is " + status.getLabel() + ".");
        }
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
        clearRequestedUpdate();
        status = ReservationStatus.CANCEL_REQUESTED;
    }

    public void approveCancel(double fee) throws ReservationException {
        if (status != ReservationStatus.CANCEL_REQUESTED) {
<<<<<<< HEAD
            throw new ReservationException("There is no cancellation request waiting for approval.");
        }
        extraCharges += fee;
        status = ReservationStatus.CANCELLED;
        statusBeforeCancel = null;
=======
            throw new ReservationException("This reservation has no cancellation request.");
        }
        extraCharges = extraCharges + fee;
        status = ReservationStatus.CANCELLED;
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
    }

    public void rejectCancel() throws ReservationException {
        if (status != ReservationStatus.CANCEL_REQUESTED) {
<<<<<<< HEAD
            throw new ReservationException("There is no cancellation request waiting for approval.");
        }
        status = statusBeforeCancel == null ? ReservationStatus.CONFIRMED : statusBeforeCancel;
        statusBeforeCancel = null;
=======
            throw new ReservationException("This reservation has no cancellation request.");
        }
        status = ReservationStatus.CONFIRMED;
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
    }

    public void checkIn() throws ReservationException {
        if (status != ReservationStatus.CONFIRMED) {
<<<<<<< HEAD
            throw new ReservationException("Only confirmed reservations can be checked in. Current status: " + status.getLabel() + ".");
=======
            throw new ReservationException("Only Confirmed reservations can be checked in. This one is " + status.getLabel() + ".");
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
        }
        status = ReservationStatus.CHECKED_IN;
    }

    public void checkOut() throws ReservationException {
        if (status != ReservationStatus.CHECKED_IN) {
<<<<<<< HEAD
            throw new ReservationException("Only checked-in customers can be checked out.");
        }
        if (getBalance() > 0.005) {
            throw new ReservationException(String.format("The customer still owes PHP %,.2f. Record the payment first.", getBalance()));
=======
            throw new ReservationException("Only Checked-In guests can be checked out.");
        }
        if (getBalance() > 0) {
            throw new ReservationException(String.format(
                    "The customer still owes PHP %,.2f. Record the payment first.", getBalance()));
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
        }
        status = ReservationStatus.CHECKED_OUT;
    }

    public void pay(double amount) throws ReservationException {
        if (status == ReservationStatus.CANCELLED) {
<<<<<<< HEAD
            throw new ReservationException("A cancelled reservation cannot receive payments.");
        }
        if (!Double.isFinite(amount) || amount <= 0 || amount > getBalance() + 0.005) {
            throw new ReservationException(String.format("Enter a payment greater than zero and no more than PHP %,.2f.", getBalance()));
        }
        amountPaid += amount;
    }

    private void validateBookingDetails(LocalDate date, LocalTime time, int guests,
                                        TableType tableType, int tables) throws ReservationException {
        if (date == null || time == null || tableType == null) {
            throw new ReservationException("Date, time, and table type are required.");
        }
        if (guests < 1 || guests > MAX_GUESTS || tables < 1 || tables > MAX_TABLES) {
            throw new ReservationException("Guest count or table count is outside the allowed range.");
        }
        if (tables < tableType.tablesNeeded(guests)) {
            throw new ReservationException("The selected number of tables cannot seat all guests.");
        }
=======
            throw new ReservationException("A cancelled reservation can't receive payments.");
        }
        if (amount <= 0 || amount > getBalance() + 0.005) {
            throw new ReservationException(String.format(
                    "The amount must be more than 0 and not more than the balance of PHP %,.2f.", getBalance()));
        }
        amountPaid = amountPaid + amount;
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
    }

    private void clearRequestedUpdate() {
        newDate = null;
        newTime = null;
        newGuests = 0;
        newTableType = null;
        newTables = 0;
    }

<<<<<<< HEAD
    /** Saves the current and pending values using a pipe-separated text record. */
    public String toFileLine() {
        String requested = hasRequestedUpdate()
                ? newDate + "|" + newTime + "|" + newGuests + "|" + newTableType.name() + "|" + newTables
                : "-|-|-|-|-";
        return reservationId + "|" + customerId + "|" + reservationName + "|" + date + "|" + time + "|"
                + guests + "|" + tableType.name() + "|" + tables + "|" + status.name() + "|"
                + amountPaid + "|" + extraCharges + "|" + requested + "|"
                + (statusBeforeCancel == null ? "-" : statusBeforeCancel.name());
    }

    /** Reads current and older reservation formats to preserve existing saved data. */
    public static Reservation fromFileLine(String line) {
        String[] p = line.split("\\|", -1);
        Reservation reservation;

        // File format is determined by field count. Switch makes version handling explicit.
        switch (p.length) {
            case 15:
                // Original format: no reservation name or cancellation-origin field.
                reservation = new Reservation(p[0], p[1], LocalDate.parse(p[2]), LocalTime.parse(p[3]),
                        Integer.parseInt(p[4]), TableType.valueOf(p[5]), Integer.parseInt(p[6]));
                reservation.status = ReservationStatus.valueOf(p[7]);
                reservation.amountPaid = Double.parseDouble(p[8]);
                reservation.extraCharges = Double.parseDouble(p[9]);
                if (!p[10].equals("-")) {
                    reservation.newDate = LocalDate.parse(p[10]);
                    reservation.newTime = LocalTime.parse(p[11]);
                    reservation.newGuests = Integer.parseInt(p[12]);
                    reservation.newTableType = TableType.valueOf(p[13]);
                    reservation.newTables = Integer.parseInt(p[14]);
                }
                break;
            case 16:
            case 17:
                // Current format adds reservation name; 17 fields also store the previous status.
                reservation = new Reservation(p[0], p[1], p[2], LocalDate.parse(p[3]), LocalTime.parse(p[4]),
                        Integer.parseInt(p[5]), TableType.valueOf(p[6]), Integer.parseInt(p[7]));
                reservation.status = ReservationStatus.valueOf(p[8]);
                reservation.amountPaid = Double.parseDouble(p[9]);
                reservation.extraCharges = Double.parseDouble(p[10]);
                if (!p[11].equals("-")) {
                    reservation.newDate = LocalDate.parse(p[11]);
                    reservation.newTime = LocalTime.parse(p[12]);
                    reservation.newGuests = Integer.parseInt(p[13]);
                    reservation.newTableType = TableType.valueOf(p[14]);
                    reservation.newTables = Integer.parseInt(p[15]);
                }
                if (p.length == 17 && !p[16].equals("-")) {
                    reservation.statusBeforeCancel = ReservationStatus.valueOf(p[16]);
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid reservation record: unexpected field count " + p.length + ".");
        }
        return reservation;
    }

=======
    // --------------------------------------------------------- file format

    /**
     * One line for RESERVE.txt / CANCELLED.txt (15 values separated by |).
     * The last 5 values are the requested update, or "-" when there is none.
     */
    public String toFileLine() {
        String requested;
        if (hasRequestedUpdate()) {
            requested = newDate + "|" + newTime + "|" + newGuests + "|" + newTableType.name() + "|" + newTables;
        } else {
            requested = "-|-|-|-|-";
        }
        return reservationId + "|" + customerId + "|" + date + "|" + time + "|" + guests + "|"
                + tableType.name() + "|" + tables + "|" + status.name() + "|"
                + amountPaid + "|" + extraCharges + "|" + requested;
    }

    /** Rebuilds a reservation from one line of RESERVE.txt / CANCELLED.txt. */
    public static Reservation fromFileLine(String line) {
        String[] p = line.split("\\|");
        Reservation r = new Reservation(p[0], p[1], LocalDate.parse(p[2]), LocalTime.parse(p[3]),
                Integer.parseInt(p[4]), TableType.valueOf(p[5]), Integer.parseInt(p[6]));
        r.status = ReservationStatus.valueOf(p[7]);
        r.amountPaid = Double.parseDouble(p[8]);
        r.extraCharges = Double.parseDouble(p[9]);
        if (!p[10].equals("-")) {
            r.newDate = LocalDate.parse(p[10]);
            r.newTime = LocalTime.parse(p[11]);
            r.newGuests = Integer.parseInt(p[12]);
            r.newTableType = TableType.valueOf(p[13]);
            r.newTables = Integer.parseInt(p[14]);
        }
        return r;
    }
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
}
