import java.util.List;

/** Prints reservations and customers as tables. */
public class Display {

    private static final String LINE =
            "--------------------------------------------------------------------------------------------------";

    public static String money(double amount) {
        return String.format("PHP %,.2f", amount);
    }

    /** Table of reservations: ID, customer, date, time, guests, tables, status, payment. */
    public static void printReservations(List<Reservation> list) {
        if (list.isEmpty()) {
            System.out.println("  No reservations to show.");
            return;
        }
        DataStore store = DataStore.getInstance();
        String format = "%-10s %-22s %-10s %-5s %6s %-13s %-16s %-14s%n";
        System.out.printf(format, "Res. ID", "Customer", "Date", "Time", "Guests", "Tables", "Status", "Payment");
        System.out.println(LINE);
        for (Reservation r : list) {
            System.out.printf(format, r.getReservationId(), store.getCustomerName(r.getCustomerId()),
                    r.getDate(), r.getTime(), r.getGuests(), r.getTables() + " x " + r.getTableType().getLabel(),
                    r.getStatus().getLabel(), r.getPaymentStatus());
        }
        System.out.println("  Total: " + list.size() + " reservation(s)");
    }

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
    }

    public static void printPaymentSummary(List<Reservation> list) {
        if (list.isEmpty()) {
            System.out.println("  No reservations to show.");
            return;
        }
        String format = "%-10s %-10s %-16s %15s %15s %15s  %-14s%n";
        System.out.printf(format, "Res. ID", "Date", "Status", "Total", "Paid", "Balance", "Payment");
        System.out.println(LINE);
        for (Reservation r : list) {
            System.out.printf(format, r.getReservationId(), r.getDate(), r.getStatus().getLabel(),
                    money(r.getTotal()), money(r.getAmountPaid()), money(r.getBalance()), r.getPaymentStatus());
        }
    }

    public static void printCustomers(List<Customer> list) {
        if (list.isEmpty()) {
            System.out.println("  No customers registered yet.");
            return;
        }
        String format = "%-12s %-18s %-22s %-12s %s%n";
        System.out.printf(format, "Customer ID", "First name", "Last name", "Contact", "Email");
        System.out.println(LINE);
        for (Customer c : list) {
            String lastName = c.getLastName();
            if (c.getDuplicateNumber() > 0) {
                lastName = lastName + "(" + c.getDuplicateNumber() + ")";
            }
            System.out.printf(format, c.getCustomerId(), c.getFirstName(), lastName, c.getContactNumber(), c.getEmail());
        }
        System.out.println("  Total: " + list.size() + " customer(s)");
    }

    public static void printStatusLog(List<String> log) {
        if (log.isEmpty()) {
            System.out.println("  No check-ins or check-outs recorded yet.");
            return;
        }
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