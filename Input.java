import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Every question the program asks goes through this class.
 *
 * Rules for every question:
 *   - Typing \ goes back (it throws BackException).
 *   - A wrong answer prints the reason and asks the SAME question again.
 *   - Input is checked exactly as typed, so "01" or " 1" are not accepted.
 *   - If a current value is given (when updating), pressing Enter keeps it.
 */
public class Input {

    private static final Scanner scanner = new Scanner(System.in);

    /** Reads one line. \ means go back. Enter keeps the current value (if there is one). */
    private static String ask(String label, String currentValue) {
        if (currentValue != null) {
            System.out.print(label + " [" + currentValue + "]: ");
        } else {
            System.out.print(label + ": ");
        }
        if (!scanner.hasNextLine()) {
            System.out.println("\nInput closed. Goodbye!");
            System.exit(0);
        }
        String text = scanner.nextLine();
        if (text.equals("\\")) {
            throw new BackException();
        }
        if (text.isEmpty() && currentValue != null) {
            return currentValue;
        }
        return text;
    }

    private static void invalid(String message) {
        System.out.println("  [Invalid] " + message);
    }

    // ------------------------------------------------------------ numbers

    /** A whole number from min to max. "01", "1 " and letters are rejected. */
    public static int readNumber(String label, int min, int max, String currentValue) {
        while (true) {
            String text = ask(label, currentValue);
            if (!text.matches("0|[1-9][0-9]{0,8}")) {
                invalid("Enter a number from " + min + " to " + max + " (digits only, no spaces or leading zeros).");
                continue;
            }
            int number = Integer.parseInt(text);
            if (number < min || number > max) {
                invalid("Enter a number from " + min + " to " + max + ".");
                continue;
            }
            return number;
        }
    }

    public static int readMenuChoice(int min, int max) {
        return readNumber("Choose an option", min, max, null);
    }

    /** Money: digits with up to 2 decimals, more than 0, not more than the balance. */
    public static double readAmount(String label, double balance) {
        while (true) {
            String text = ask(label, null);
            if (!text.matches("(0|[1-9][0-9]{0,8})(\\.[0-9]{1,2})?")) {
                invalid("Enter an amount like 500 or 750.50 (digits only, up to 2 decimals).");
                continue;
            }
            double amount = Double.parseDouble(text);
            if (amount <= 0) {
                invalid("Amount must be greater than zero.");
                continue;
            }
            if (amount > balance + 0.005) {
                invalid(String.format("Amount is more than the remaining balance of PHP %,.2f.", balance));
                continue;
            }
            return amount;
        }
    }

    // ----------------------------------------------------- customer info

    /** First or last name: letters only, one space allowed between words, max 30 characters. */
    public static String readName(String label, String example) {
        while (true) {
            String text = ask(label, null);
            if (text.isEmpty()) {
                invalid(label + " is required.");
            } else if (text.length() > 30) {
                invalid(label + " must be 30 characters or fewer.");
            } else if (!text.matches("\\p{L}+( \\p{L}+)*")) {   // \p{L} = any letter, including ñ
                invalid("Use letters only, with one space between words (e.g. " + example + ").");
            } else {
                return text;
            }
        }
    }

    /** 11 digits starting with 09, e.g. 09171234567. */
    public static String readContactNumber(String label) {
        while (true) {
            String text = ask(label, null);
            if (text.matches("09[0-9]{9}")) {
                return text;
            }
            invalid("Enter 11 digits starting with 09, with no spaces or dashes (e.g. 09171234567).");
        }
    }

    /** A normal email format, e.g. juan@example.com. Returned in lowercase. */
    public static String readEmail(String label) {
        while (true) {
            String text = ask(label, null);
            if (text.isEmpty()) {
                invalid("Email address is required.");
            } else if (!text.matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}")
                    || text.startsWith(".") || text.contains("..") || text.contains(".@")) {
                invalid("That is not a valid email address. Use a format like juan@example.com.");
            } else {
                return text.toLowerCase();
            }
        }
    }

    public static String readUsername(String label) {
        while (true) {
            String text = ask(label, null);
            if (!text.isEmpty() && !text.contains(" ")) {
                return text;
            }
            invalid("Enter your username (no spaces).");
        }
    }

    public static String readPassword(String label) {
        while (true) {
            String text = ask(label, null);
            if (!text.isEmpty()) {
                return text;
            }
            invalid("Enter your password.");
        }
    }

    // ------------------------------------------------------- reservations

    /** A real date from today up to 1 year ahead, in YYYY-MM-DD format. */
    public static LocalDate readDate(String label, String currentValue) {
        LocalDate today = LocalDate.now();
        LocalDate latest = today.plusYears(1);
        while (true) {
            String text = ask(label, currentValue);
            if (!text.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) {
                invalid("Use the format YYYY-MM-DD (e.g. 2026-12-24).");
                continue;
            }
            LocalDate date;
            try {
                date = LocalDate.parse(text);   // EXCEPTION HANDLING: 2026-02-30 throws an error
            } catch (DateTimeParseException e) {
                invalid("That date does not exist on the calendar.");
                continue;
            }
            if (date.isBefore(today)) {
                invalid("That date has already passed. Pick today or a later date.");
            } else if (date.isAfter(latest)) {
                invalid("You can only book up to 1 year ahead (latest date: " + latest + ").");
            } else if (date.equals(today) && !LocalTime.now().isBefore(Reservation.LAST_BOOKING_TIME)) {
                invalid("Bookings for today are closed (last booking is " + Reservation.LAST_BOOKING_TIME + "). Pick another date.");
            } else {
                return date;
            }
        }
    }

    /** 24-hour HH:mm between 10:00 and 21:00. If the date is today, it must be later than now. */
    public static LocalTime readTime(String label, LocalDate date, String currentValue) {
        while (true) {
            String text = ask(label, currentValue);
            if (!text.matches("([01][0-9]|2[0-3]):[0-5][0-9]")) {
                invalid("Use 24-hour format HH:mm (e.g. 10:00, 13:30, 18:45).");
                continue;
            }
            LocalTime time = LocalTime.parse(text);
            if (time.isBefore(Reservation.OPENING_TIME) || time.isAfter(Reservation.LAST_BOOKING_TIME)) {
                invalid("We accept bookings from " + Reservation.OPENING_TIME + " to " + Reservation.LAST_BOOKING_TIME + " only.");
            } else if (date.equals(LocalDate.now()) && !time.isAfter(LocalTime.now())) {
                invalid("That time has already passed today. Pick a later time.");
            } else {
                return time;
            }
        }
    }

    /** The customer types only the 6 digits; "RN-" is added here. */
    public static String readReservationId(String label) {
        while (true) {
            String text = ask(label, null);
            if (text.matches("[0-9]{6}")) {
                return "RN-" + text;
            }
            invalid("Enter only the 6 digits of the reservation ID (e.g. 123456), without RN-.");
        }
    }

    /** Same as readReservationId, but pressing Enter returns null (meaning "skip"). */
    public static String readReservationIdOrSkip(String label) {
        while (true) {
            String text = ask(label, null);
            if (text.isEmpty()) {
                return null;
            }
            if (text.matches("[0-9]{6}")) {
                return "RN-" + text;
            }
            invalid("Enter only the 6 digits of the reservation ID (e.g. 123456), without RN-.");
        }
    }

    // --------------------------------------------------------------- other

    public static boolean readYesNo(String label) {
        while (true) {
            String text = ask(label + " (y/n)", null);
            if (text.equals("y") || text.equals("Y")) {
                return true;
            }
            if (text.equals("n") || text.equals("N")) {
                return false;
            }
            invalid("Type y for yes or n for no.");
        }
    }

    public static void pause() {
        System.out.print("\nPress Enter to continue...");
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
    }
}