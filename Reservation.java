import java.time.LocalDate;
import java.time.LocalTime;

/**
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
    private LocalDate date;
    private LocalTime time;
    private int guests;
    private TableType tableType;
    private int tables;
    private ReservationStatus status;
    private double amountPaid;
    private double extraCharges;

    // The change the customer asked for (empty until they request an update)
    private LocalDate newDate;
    private LocalTime newTime;
    private int newGuests;
    private TableType newTableType;
    private int newTables;

    /** A brand-new reservation: Confirmed and unpaid. */
    public Reservation(String reservationId, String customerId, LocalDate date, LocalTime time,
                       int guests, TableType tableType, int tables) {
        this.reservationId = reservationId;
        this.customerId = customerId;
        this.date = date;
        this.time = time;
        this.guests = guests;
        this.tableType = tableType;
        this.tables = tables;
        this.status = ReservationStatus.CONFIRMED;
        this.amountPaid = 0;
        this.extraCharges = 0;
    }

    // ----------------------------------------------------------- getters

    public String getReservationId() { return reservationId; }
    public String getCustomerId() { return customerId; }
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

    /** True if the requested change moves the date or time (this has a fee). */
    public boolean requestChangesSchedule() {
        return !newDate.equals(date) || !newTime.equals(time);
    }

    // -------------------------------------------------------------- money

    /** Table price x number of tables. */
    public double getTableCost() {
        return tableType.getPrice() * tables;
    }

    /** What the customer has to pay in total. A cancelled booking only owes its fee. */
    public double getTotal() {
        if (status == ReservationStatus.CANCELLED) {
            return extraCharges;
        }
        return getTableCost() + extraCharges;
    }

    public double getBalance() {
        double balance = getTotal() - amountPaid;
        return balance < 0.005 ? 0 : balance;
    }

    public double getRefundDue() {
        double refund = amountPaid - getTotal();
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
        }
        date = newDate;
        time = newTime;
        guests = newGuests;
        tableType = newTableType;
        tables = newTables;
        extraCharges = extraCharges + fee;
        clearRequestedUpdate();
        status = ReservationStatus.CONFIRMED;
    }

    public void rejectUpdate() throws ReservationException {
        if (status != ReservationStatus.PENDING_UPDATE) {
            throw new ReservationException("This reservation has no update waiting for approval.");
        }
        clearRequestedUpdate();
        status = ReservationStatus.CONFIRMED;
    }

    public void requestCancel() throws ReservationException {
        if (status != ReservationStatus.CONFIRMED && status != ReservationStatus.PENDING_UPDATE) {
            throw new ReservationException("This reservation can't be cancelled because it is " + status.getLabel() + ".");
        }
        clearRequestedUpdate();
        status = ReservationStatus.CANCEL_REQUESTED;
    }

    public void approveCancel(double fee) throws ReservationException {
        if (status != ReservationStatus.CANCEL_REQUESTED) {
            throw new ReservationException("This reservation has no cancellation request.");
        }
        extraCharges = extraCharges + fee;
        status = ReservationStatus.CANCELLED;
    }

    public void rejectCancel() throws ReservationException {
        if (status != ReservationStatus.CANCEL_REQUESTED) {
            throw new ReservationException("This reservation has no cancellation request.");
        }
        status = ReservationStatus.CONFIRMED;
    }

    public void checkIn() throws ReservationException {
        if (status != ReservationStatus.CONFIRMED) {
            throw new ReservationException("Only Confirmed reservations can be checked in. This one is " + status.getLabel() + ".");
        }
        status = ReservationStatus.CHECKED_IN;
    }

    public void checkOut() throws ReservationException {
        if (status != ReservationStatus.CHECKED_IN) {
            throw new ReservationException("Only Checked-In guests can be checked out.");
        }
        if (getBalance() > 0) {
            throw new ReservationException(String.format(
                    "The customer still owes PHP %,.2f. Record the payment first.", getBalance()));
        }
        status = ReservationStatus.CHECKED_OUT;
    }

    public void pay(double amount) throws ReservationException {
        if (status == ReservationStatus.CANCELLED) {
            throw new ReservationException("A cancelled reservation can't receive payments.");
        }
        if (amount <= 0 || amount > getBalance() + 0.005) {
            throw new ReservationException(String.format(
                    "The amount must be more than 0 and not more than the balance of PHP %,.2f.", getBalance()));
        }
        amountPaid = amountPaid + amount;
    }

    private void clearRequestedUpdate() {
        newDate = null;
        newTime = null;
        newGuests = 0;
        newTableType = null;
        newTables = 0;
    }

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
}
