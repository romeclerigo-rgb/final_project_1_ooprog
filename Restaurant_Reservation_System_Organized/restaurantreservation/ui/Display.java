package restaurantreservation.ui;

import restaurantreservation.models.*;
import restaurantreservation.storage.*;
import java.util.List;
import java.time.format.DateTimeFormatter;

/** Keeps repeated console-output formatting out of the menus. */
public class Display {
    private static final String LINE = "---------------------------------------------------------------------------------------------------------------";
    private static final String BORDER = "============================================================";

    private Display() { }

    public static String money(double amount) {
        return String.format("PHP %,.2f", amount);
    }

    /** Prints a consistent, centered-style heading for menus and operations. */
    public static void printHeader(String title) {
        System.out.println();
        System.out.println(BORDER);
        int padding = Math.max(0, (BORDER.length() - title.length()) / 2);
        System.out.printf("%" + padding + "s%s%n", "", title);
        System.out.println(BORDER);
    }

    public static void printSection(String title) {
        System.out.println();
        System.out.println("  " + title.toUpperCase());
        System.out.println("  ----------------------------------------------------------");
    }

    public static void printSuccess(String message) {
        System.out.println("\n  [SUCCESS] " + message);
    }

    public static void printNotice(String message) {
        System.out.println("  [INFO] " + message);
    }

    public static void printError(String message) {
        System.out.println("  [ERROR] " + message);
    }

    /** Polished confirmation shown immediately after a customer submits a booking. */
    public static void printReservationConfirmation(Reservation reservation) {
        DataStore store = DataStore.getInstance();
        String date = reservation.getDate().format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));
        String time = reservation.getTime().format(DateTimeFormatter.ofPattern("h:mm a"));
        String customerName = store.getCustomerName(reservation.getCustomerId());
        String email = store.getCustomerEmail(reservation.getCustomerId());

        System.out.println();
        System.out.println(BORDER);
        System.out.println("                 RESERVATION REQUEST SAVED");
        System.out.println(BORDER);
        System.out.println("  Your reservation has been submitted successfully.");
        System.out.println("  Status: PENDING - Awaiting staff confirmation");

        printSection("Reservation Details");
        System.out.printf("  %-21s: %s%n", "Reservation Name", reservation.getReservationName());
        System.out.printf("  %-21s: %s%n", "Customer", customerName);
        System.out.printf("  %-21s: %s%n", "Email", email);
        System.out.printf("  %-21s: %s%n", "Date", date);
        System.out.printf("  %-21s: %s%n", "Time", time);
        System.out.printf("  %-21s: %d%n", "Guests", reservation.getGuests());
        System.out.printf("  %-21s: %d x %s (%d seats)%n", "Tables", reservation.getTables(),
                reservation.getTableType().getLabel(),
                reservation.getTables() * reservation.getTableType().getSeats());

        printSection("Payment Summary");
        System.out.printf("  %-21s: %s%n", "Table Cost", money(reservation.getTableCost()));
        System.out.printf("  %-21s: %s%n", "Extra Charges", money(reservation.getExtraCharges()));
        System.out.printf("  %-21s: %s%n", "Total Amount", money(reservation.getTotal()));
        System.out.printf("  %-21s: %s%n", "Amount Paid", money(reservation.getAmountPaid()));
        if (reservation.getCancellationFee() > 0) {
            System.out.printf("  %-21s: %s%n", "Cancellation Fee", money(reservation.getCancellationFee()));
            System.out.printf("  %-21s: %s%n", "Cancellation Fee Paid", money(reservation.getCancellationFeePaid()));
        }
        System.out.printf("  %-21s: %s%n", "Remaining Balance", money(reservation.getBalance()));
        System.out.printf("  %-21s: %s%n", "Payment Status", reservation.getPaymentStatus());

        System.out.println();
        System.out.println(BORDER);
        System.out.println("              IMPORTANT: REMEMBER YOUR BOOKING");
        System.out.println(BORDER);
        System.out.println("                 Your Reservation Name is:");
        System.out.println();
        System.out.println("                       >>> " + reservation.getReservationName() + " <<<");
        System.out.println();
        System.out.println("  Remember this name when checking your reservation.");
        System.out.println("  Staff must verify your reservation before confirmation.");
        System.out.println(BORDER);
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
        System.out.println("\n============================================================");
        System.out.println("  Total: " + list.size() + " reservation(s)");
    }

    public static void printDetails(Reservation reservation) {
        DataStore store = DataStore.getInstance();
        printHeader("RESERVATION DETAILS");
        System.out.printf("  %-21s: %s%n", "Reservation Name", reservation.getReservationName());
        System.out.printf("  %-21s: %s (%s)%n", "Customer",
                store.getCustomerName(reservation.getCustomerId()),
                store.getCustomerEmail(reservation.getCustomerId()));
        System.out.printf("  %-21s: %s at %s%n", "Date / Time", reservation.getDate(), reservation.getTime());
        System.out.printf("  %-21s: %d%n", "Guests", reservation.getGuests());
        System.out.printf("  %-21s: %d x %s (%d seats)%n", "Tables", reservation.getTables(),
                reservation.getTableType().getLabel(), reservation.getTables() * reservation.getTableType().getSeats());
        System.out.printf("  %-21s: %s%n", "Reservation Status", reservation.getStatus().getLabel());
        printMoney(reservation);
        if (reservation.hasRequestedUpdate()) {
            printSection("Requested Update - Waiting for Staff Approval");
            System.out.printf("  %-21s: %s at %s%n", "Requested date/time", reservation.getNewDate(), reservation.getNewTime());
            System.out.printf("  %-21s: %d%n", "Requested guests", reservation.getNewGuests());
            System.out.printf("  %-21s: %d x %s%n", "Requested tables", reservation.getNewTables(),
                    reservation.getNewTableType().getLabel());
            System.out.printf("  %-21s: %s%n", "Schedule changed",
                    reservation.requestChangesSchedule() ? "Yes (fee applies)" : "No");
        }
        System.out.println(LINE);
    }

    public static void printMoney(Reservation reservation) {
        printSection("Payment Summary");
        System.out.printf("  %-21s: %s%n", "Table Cost", money(reservation.getTableCost()));
        System.out.printf("  %-21s: %s%n", "Extra Charges", money(reservation.getExtraCharges()));
        System.out.printf("  %-21s: %s%n", "Total Amount", money(reservation.getTotal()));
        System.out.printf("  %-21s: %s%n", "Amount Paid", money(reservation.getAmountPaid()));
        if (reservation.getCancellationFee() > 0) {
            System.out.printf("  %-21s: %s%n", "Cancellation Fee", money(reservation.getCancellationFee()));
            System.out.printf("  %-21s: %s%n", "Cancellation Fee Paid", money(reservation.getCancellationFeePaid()));
        }
        System.out.printf("  %-21s: %s%n", "Remaining Balance", money(reservation.getBalance()));
        if (reservation.getRefundDue() > 0) {
            System.out.printf("  %-21s: %s%n", "Refund Due", money(reservation.getRefundDue()));
        }
        System.out.printf("  %-21s: %s%n", "Payment Status", reservation.getPaymentStatus());
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
