import java.util.List;

/** Keeps repeated console-output formatting out of the menus. */
public class Display {
    private static final String LINE = "---------------------------------------------------------------------------------------------------------------";

    private Display() { }

    public static String money(double amount) {
        return String.format("PHP %,.2f", amount);
    }

    /** Displays the fields listed in the approved proposal, including reservation and payment status. */
    public static void printReservations(List<Reservation> list) {
        if (list.isEmpty()) {
            System.out.println("  No reservations to show.");
            return;
        }
        DataStore store = DataStore.getInstance();
        String format = "%-22s %-19s %-10s %-5s %6s %-14s %-16s %-14s%n";
        System.out.printf(format, "Reservation name", "Customer", "Date", "Time",
                "Guests", "Tables", "Status", "Payment");
        System.out.println(LINE);
        for (Reservation reservation : list) {
            String tableInfo = reservation.getTables() + " x " + reservation.getTableType().getLabel();
            System.out.printf(format, shortText(reservation.getReservationName(), 22),
                    shortText(store.getCustomerName(reservation.getCustomerId()), 19), reservation.getDate(),
                    reservation.getTime(), reservation.getGuests(), tableInfo,
                    reservation.getStatus().getLabel(), reservation.getPaymentStatus());
        }
        System.out.println("  Total: " + list.size() + " reservation(s)");
    }

    public static void printPickList(List<Reservation> list) {
        if (list.isEmpty()) {
            System.out.println("  No reservations to show.");
            return;
        }
        DataStore store = DataStore.getInstance();
        String format = "%-22s %-24s %-19s %-10s %s%n";
        System.out.printf(format, "Customer", "Reservation name", "Email", "Date", "Status");
        System.out.println(LINE);
        for (Reservation reservation : list) {
            System.out.printf(format,
                    shortText(store.getCustomerName(reservation.getCustomerId()), 22),
                    shortText(reservation.getReservationName(), 24),
                    shortText(store.getCustomerEmail(reservation.getCustomerId()), 19),
                    reservation.getDate(), reservation.getStatus().getLabel());
        }
    }

    public static void printDetails(Reservation reservation) {
        DataStore store = DataStore.getInstance();
        System.out.println("  Reservation name: " + reservation.getReservationName());
        System.out.println("  Customer       : " + store.getCustomerName(reservation.getCustomerId())
                + " (" + store.getCustomerEmail(reservation.getCustomerId()) + ")");
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
    }

    public static void printPaymentSummary(List<Reservation> list) {
        if (list.isEmpty()) {
            System.out.println("  No reservations to show.");
            return;
        }
        String format = "%-22s %-10s %-16s %15s %15s %15s %-14s%n";
        System.out.printf(format, "Reservation name", "Date", "Status", "Total", "Paid", "Balance", "Payment");
        System.out.println(LINE);
        for (Reservation reservation : list) {
            System.out.printf(format, shortText(reservation.getReservationName(), 22), reservation.getDate(),
                    reservation.getStatus().getLabel(), money(reservation.getTotal()),
                    money(reservation.getAmountPaid()), money(reservation.getBalance()), reservation.getPaymentStatus());
        }
    }

    public static void printCustomers(List<Customer> list) {
        if (list.isEmpty()) {
            System.out.println("  No customers registered yet.");
            return;
        }
        String format = "%-24s %-32s %s%n";
        System.out.printf(format, "Full name", "Address", "Email");
        System.out.println(LINE);
        for (Customer customer : list) {
            System.out.printf(format, shortText(customer.getName(), 24),
                    shortText(customer.getAddress(), 32), customer.getEmail());
        }
        System.out.println("  Total: " + list.size() + " customer(s)");
    }

    public static void printStatusLog(List<String> log) {
        if (log.isEmpty()) {
            System.out.println("  No check-ins or check-outs recorded yet.");
            return;
        }
        String format = "%-18s %-24s %s%n";
        System.out.printf(format, "Timestamp", "Customer", "Status");
        System.out.println(LINE);
        for (String line : log) {
            String[] parts = line.split("\\|", -1);
            if (parts.length == 5) {
                System.out.printf(format, parts[0], shortText(parts[3], 24), parts[4]);
            }
        }
    }

    private static String shortText(String value, int maximum) {
        if (value == null) return "-";
        if (value.length() <= maximum) return value;
        return value.substring(0, maximum - 3) + "...";
    }
}
