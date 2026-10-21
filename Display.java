import java.util.List;

<<<<<<< HEAD
/** Keeps repeated console-output formatting out of the menus. */
public class Display {
    private static final String LINE = "---------------------------------------------------------------------------------------------------------------";

    private Display() { }
=======
/** Prints reservations and customers as tables. */
public class Display {

    private static final String LINE =
            "--------------------------------------------------------------------------------------------------";
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8

    public static String money(double amount) {
        return String.format("PHP %,.2f", amount);
    }

<<<<<<< HEAD
    /** Displays the fields listed in the approved proposal, including reservation and payment status. */
=======
    /** Table of reservations: ID, customer, date, time, guests, tables, status, payment. */
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
    public static void printReservations(List<Reservation> list) {
        if (list.isEmpty()) {
            System.out.println("  No reservations to show.");
            return;
        }
        DataStore store = DataStore.getInstance();
<<<<<<< HEAD
        String format = "%-10s %-20s %-19s %-10s %-5s %6s %-14s %-16s %-14s%n";
        System.out.printf(format, "Res. ID", "Reservation name", "Customer", "Date", "Time",
                "Guests", "Tables", "Status", "Payment");
        System.out.println(LINE);
        for (Reservation reservation : list) {
            String tableInfo = reservation.getTables() + " x " + reservation.getTableType().getLabel();
            System.out.printf(format, reservation.getReservationId(), shortText(reservation.getReservationName(), 20),
                    shortText(store.getCustomerName(reservation.getCustomerId()), 19), reservation.getDate(),
                    reservation.getTime(), reservation.getGuests(), tableInfo,
                    reservation.getStatus().getLabel(), reservation.getPaymentStatus());
=======
        String format = "%-10s %-22s %-10s %-5s %6s %-13s %-16s %-14s%n";
        System.out.printf(format, "Res. ID", "Customer", "Date", "Time", "Guests", "Tables", "Status", "Payment");
        System.out.println(LINE);
        for (Reservation r : list) {
            System.out.printf(format, r.getReservationId(), store.getCustomerName(r.getCustomerId()),
                    r.getDate(), r.getTime(), r.getGuests(), r.getTables() + " x " + r.getTableType().getLabel(),
                    r.getStatus().getLabel(), r.getPaymentStatus());
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
        }
        System.out.println("  Total: " + list.size() + " reservation(s)");
    }

<<<<<<< HEAD
    public static void printPickList(List<Reservation> list) {
        if (list.isEmpty()) {
            System.out.println("  No reservations to show.");
            return;
        }
        DataStore store = DataStore.getInstance();
        String format = "%-10s %-22s %-24s %-19s %-10s %s%n";
        System.out.printf(format, "Res. ID", "Customer", "Reservation name", "Email", "Date", "Status");
        System.out.println(LINE);
        for (Reservation reservation : list) {
            System.out.printf(format, reservation.getReservationId(),
                    shortText(store.getCustomerName(reservation.getCustomerId()), 22),
                    shortText(reservation.getReservationName(), 24),
                    shortText(store.getCustomerEmail(reservation.getCustomerId()), 19),
                    reservation.getDate(), reservation.getStatus().getLabel());
        }
    }

    public static void printDetails(Reservation reservation) {
        DataStore store = DataStore.getInstance();
        System.out.println("  Reservation ID : " + reservation.getReservationId());
        System.out.println("  Reservation name: " + reservation.getReservationName());
        System.out.println("  Customer       : " + store.getCustomerName(reservation.getCustomerId())
                + " (" + reservation.getCustomerId() + ")");
        System.out.println("  Date / Time    : " + reservation.getDate() + " at " + reservation.getTime());
        System.out.println("  Guests         : " + reservation.getGuests());
        System.out.println("  Tables         : " + reservation.getTables() + " x " + reservation.getTableType().getLabel()
                + " (" + (reservation.getTables() * reservation.getTableType().getSeats()) + " seats total)");
        System.out.println("  Status         : " + reservation.getStatus().getLabel());
        printMoney(reservation);
        if (reservation.hasRequestedUpdate()) {
            System.out.println("  -- Requested update (waiting for staff approval) --");
            System.out.println("  Requested date/time: " + reservation.getNewDate() + " at " + reservation.getNewTime());
            System.out.println("  Requested guests   : " + reservation.getNewGuests());
            System.out.println("  Requested tables   : " + reservation.getNewTables() + " x "
                    + reservation.getNewTableType().getLabel());
            System.out.println("  Schedule changed   : " + (reservation.requestChangesSchedule() ? "Yes (fee applies)" : "No"));
        }
    }

    public static void printMoney(Reservation reservation) {
        System.out.println("  Table cost      : " + money(reservation.getTableCost()));
        System.out.println("  Extra charges   : " + money(reservation.getExtraCharges()));
        System.out.println("  Total amount    : " + money(reservation.getTotal()));
        System.out.println("  Amount paid     : " + money(reservation.getAmountPaid()));
        System.out.println("  Remaining balance: " + money(reservation.getBalance()));
        if (reservation.getRefundDue() > 0) {
            System.out.println("  Refund due      : " + money(reservation.getRefundDue()));
        }
        System.out.println("  Payment status  : " + reservation.getPaymentStatus());
=======
    /**
     * Numbered list for staff and the manager:
     * [1] customer name, email, reservation ID, date, status
     */
    public static void printPickList(List<Reservation> list) {
        DataStore store = DataStore.getInstance();
        String format = "  %-5s %-24s %-28s %-10s %-10s %s%n";
        System.out.printf(format, "No.", "Customer name", "Email", "Res. ID", "Date", "Status");
        System.out.println("  " + LINE);
        for (int i = 0; i < list.size(); i++) {
            Reservation r = list.get(i);
            System.out.printf(format, "[" + (i + 1) + "]", store.getCustomerName(r.getCustomerId()),
                    store.getCustomerEmail(r.getCustomerId()), r.getReservationId(), r.getDate(),
                    r.getStatus().getLabel());
        }
        System.out.println();
    }

    public static void printDetails(Reservation r) {
        DataStore store = DataStore.getInstance();
        System.out.println("  Reservation ID : " + r.getReservationId());
        System.out.println("  Customer       : " + store.getCustomerName(r.getCustomerId()) + " (" + r.getCustomerId() + ")");
        System.out.println("  Date / Time    : " + r.getDate() + " at " + r.getTime());
        System.out.println("  Guests         : " + r.getGuests());
        System.out.println("  Tables         : " + r.getTables() + " x " + r.getTableType().getLabel()
                + " (seats " + (r.getTables() * r.getTableType().getSeats()) + ")");
        System.out.println("  Status         : " + r.getStatus().getLabel());
        printMoney(r);
        if (r.hasRequestedUpdate()) {
            System.out.println("  -- Requested update (waiting for staff approval) --");
            System.out.println("     Date / Time : " + r.getNewDate() + " at " + r.getNewTime());
            System.out.println("     Guests      : " + r.getNewGuests());
            System.out.println("     Tables      : " + r.getNewTables() + " x " + r.getNewTableType().getLabel());
            System.out.println("     Reschedule  : " + (r.requestChangesSchedule() ? "Yes (fee applies)" : "No"));
        }
    }

    public static void printMoney(Reservation r) {
        String waived = r.getStatus() == ReservationStatus.CANCELLED ? " (waived, cancelled)" : "";
        System.out.println("  Table cost     : " + money(r.getTableCost()) + waived);
        System.out.println("  Extra charges  : " + money(r.getExtraCharges()));
        System.out.println("  Total amount   : " + money(r.getTotal()));
        System.out.println("  Amount paid    : " + money(r.getAmountPaid()));
        System.out.println("  Balance        : " + money(r.getBalance()));
        if (r.getRefundDue() > 0) {
            System.out.println("  Refund due     : " + money(r.getRefundDue()));
        }
        System.out.println("  Payment status : " + r.getPaymentStatus());
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
    }

    public static void printPaymentSummary(List<Reservation> list) {
        if (list.isEmpty()) {
            System.out.println("  No reservations to show.");
            return;
        }
<<<<<<< HEAD
        String format = "%-10s %-10s %-16s %15s %15s %15s %-14s%n";
        System.out.printf(format, "Res. ID", "Date", "Status", "Total", "Paid", "Balance", "Payment");
        System.out.println(LINE);
        for (Reservation reservation : list) {
            System.out.printf(format, reservation.getReservationId(), reservation.getDate(),
                    reservation.getStatus().getLabel(), money(reservation.getTotal()),
                    money(reservation.getAmountPaid()), money(reservation.getBalance()), reservation.getPaymentStatus());
=======
        String format = "%-10s %-10s %-16s %15s %15s %15s  %-14s%n";
        System.out.printf(format, "Res. ID", "Date", "Status", "Total", "Paid", "Balance", "Payment");
        System.out.println(LINE);
        for (Reservation r : list) {
            System.out.printf(format, r.getReservationId(), r.getDate(), r.getStatus().getLabel(),
                    money(r.getTotal()), money(r.getAmountPaid()), money(r.getBalance()), r.getPaymentStatus());
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
        }
    }

    public static void printCustomers(List<Customer> list) {
        if (list.isEmpty()) {
            System.out.println("  No customers registered yet.");
            return;
        }
<<<<<<< HEAD
        String format = "%-15s %-24s %-32s %-16s %s%n";
        System.out.printf(format, "Customer ID", "Full name", "Address", "Contact", "Email");
        System.out.println(LINE);
        for (Customer customer : list) {
            System.out.printf(format, customer.getCustomerId(), shortText(customer.getName(), 24),
                    shortText(customer.getAddress(), 32), customer.getContactNumber(), customer.getEmail());
=======
        String format = "%-12s %-18s %-22s %-12s %s%n";
        System.out.printf(format, "Customer ID", "First name", "Last name", "Contact", "Email");
        System.out.println(LINE);
        for (Customer c : list) {
            String lastName = c.getLastName();
            if (c.getDuplicateNumber() > 0) {
                lastName = lastName + "(" + c.getDuplicateNumber() + ")";
            }
            System.out.printf(format, c.getCustomerId(), c.getFirstName(), lastName, c.getContactNumber(), c.getEmail());
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
        }
        System.out.println("  Total: " + list.size() + " customer(s)");
    }

    public static void printStatusLog(List<String> log) {
        if (log.isEmpty()) {
            System.out.println("  No check-ins or check-outs recorded yet.");
            return;
        }
<<<<<<< HEAD
        String format = "%-18s %-10s %-15s %-24s %s%n";
        System.out.printf(format, "Timestamp", "Res. ID", "Customer ID", "Customer", "Status");
        System.out.println(LINE);
        for (String line : log) {
            String[] parts = line.split("\\|", -1);
            if (parts.length == 5) {
                System.out.printf(format, parts[0], parts[1], parts[2], shortText(parts[3], 24), parts[4]);
            }
        }
    }

    private static String shortText(String value, int maximum) {
        if (value == null) return "-";
        if (value.length() <= maximum) return value;
        return value.substring(0, maximum - 3) + "...";
    }
}
=======
        String format = "%-17s %-10s %-12s %-24s %s%n";
        System.out.printf(format, "Time", "Res. ID", "Customer ID", "Customer", "Status");
        System.out.println(LINE);
        for (String line : log) {
            String[] p = line.split("\\|");
            if (p.length == 5) {
                System.out.printf(format, p[0], p[1], p[2], p[3], p[4]);
            }
        }
    }
}
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
