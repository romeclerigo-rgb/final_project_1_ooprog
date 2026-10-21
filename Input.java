import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Central input validation for the whole program.
 *
 * - Regex checks the exact format before conversion.
 * - try-catch protects date and number conversions.
 * - A boolean loop repeats the same question after invalid input.
 * - Typing a single backslash (\\) returns to the previous question/menu.
 * - During updates, pressing Enter keeps the value shown in brackets.
 */
public class Input {
    private static final Scanner SCANNER = new Scanner(System.in);

    private Input() { }

    private static String ask(String label, String currentValue) {
        if (currentValue == null) {
            System.out.print(label + ": ");
        } else {
            System.out.print(label + " [" + currentValue + "]: ");
        }

        if (!SCANNER.hasNextLine()) {
            System.out.println("\nInput closed. Goodbye!");
            System.exit(0);
        }

        String text = SCANNER.nextLine();
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

    public static int readMenuChoice(int min, int max) {
        return readNumber("Choose an option", min, max, null);
    }

    /** Accepts only whole numbers within the range; spaces and leading zeros are rejected. */
    public static int readNumber(String label, int min, int max, String currentValue) {
        boolean valid = false;
        int value = 0;
        while (!valid) {
            String text = ask(label, currentValue);
            if (!text.matches("0|[1-9][0-9]{0,8}")) {
                invalid("Enter a whole number from " + min + " to " + max
                        + " (digits only, no spaces or leading zeros).");
                continue;
            }
            try {
                value = Integer.parseInt(text);
                if (value < min || value > max) {
                    invalid("Enter a number from " + min + " to " + max + ".");
                } else {
                    valid = true;
                }
            } catch (NumberFormatException e) {
                invalid("That number is too large. Please try again.");
            }
        }
        return value;
    }

    /** Reads a positive payment not exceeding the current balance. */
    public static double readAmount(String label, double balance) {
        boolean valid = false;
        double amount = 0;
        while (!valid) {
            String text = ask(label, null);
            if (!text.matches("(?:0|[1-9][0-9]{0,8})(?:\\.[0-9]{1,2})?")) {
                invalid("Enter an amount like 500 or 750.50, with up to 2 decimals.");
                continue;
            }
            try {
                amount = Double.parseDouble(text);
                if (!Double.isFinite(amount) || amount <= 0) {
                    invalid("Amount must be greater than zero.");
                } else if (amount > balance + 0.005) {
                    invalid(String.format("Amount cannot exceed the balance of PHP %,.2f.", balance));
                } else {
                    valid = true;
                }
            } catch (NumberFormatException e) {
                invalid("Invalid amount. Please try again.");
            }
        }
        return amount;
    }

    /** First/last name: letters and one space between words, maximum 30 characters. */
    public static String readName(String label, String example) {
        boolean valid = false;
        String value = "";
        while (!valid) {
            String text = ask(label, null);
            if (text.isEmpty()) {
                invalid(label + " is required.");
            } else if (text.length() > 30) {
                invalid(label + " must be 30 characters or fewer.");
            } else if (!text.matches("\\p{L}+( \\p{L}+)*")) {
                invalid("Use letters only, with single spaces (example: " + example + ").");
            } else {
                value = text;
                valid = true;
            }
        }
        return value;
    }

    /** An address allows common punctuation but not the pipe used in text-file records. */
    public static String readAddress(String label) {
        boolean valid = false;
        String value = "";
        while (!valid) {
            String text = ask(label, null);
            if (text.trim().isEmpty()) {
                invalid("Address is required.");
            } else if (text.length() > 150) {
                invalid("Address must be 150 characters or fewer.");
            } else if (text.contains("|") || text.matches(".*[\\r\\n\\t].*")) {
                invalid("Do not use | or line breaks in the address.");
            } else {
                value = text;
                valid = true;
            }
        }
        return value;
    }

    public static String readReservationName(String label) {
        boolean valid = false;
        String value = "";
        while (!valid) {
            String text = ask(label, null);
            if (text.trim().isEmpty()) {
                invalid("Reservation name is required.");
            } else if (text.length() > 60) {
                invalid("Reservation name must be 60 characters or fewer.");
            } else if (text.contains("|") || text.matches(".*[\\r\\n\\t].*")) {
                invalid("Do not use | or line breaks in the reservation name.");
            } else {
                value = text.trim();
                valid = true;
            }
        }
        return value;
    }

    public static String readContactNumber(String label) {
        boolean valid = false;
        String value = "";
        while (!valid) {
            String text = ask(label, null);
            if (text.matches("09[0-9]{9}")) {
                value = text;
                valid = true;
            } else {
                invalid("Enter 11 digits starting with 09, without spaces or dashes.");
            }
        }
        return value;
    }

    public static String readEmail(String label) {
        boolean valid = false;
        String value = "";
        while (!valid) {
            String text = ask(label, null);
            if (text.matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(?:\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}")
                    && !text.startsWith(".") && !text.contains("..") && !text.contains(".@")
                    && !text.contains("|")) {
                value = text.toLowerCase();
                valid = true;
            } else {
                invalid("Enter a valid email address, such as juan@example.com.");
            }
        }
        return value;
    }

    /** The proposal allows customer login by either their registered email or contact number. */
    public static String readEmailOrContact(String label) {
        boolean valid = false;
        String value = "";
        while (!valid) {
            String text = ask(label, null);
            boolean email = text.matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(?:\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}")
                    && !text.startsWith(".") && !text.contains("..") && !text.contains(".@");
            boolean phone = text.matches("09[0-9]{9}");
            if (email || phone) {
                value = email ? text.toLowerCase() : text;
                valid = true;
            } else {
                invalid("Enter your registered email or 11-digit contact number.");
            }
        }
        return value;
    }

    public static String readUsername(String label) {
        boolean valid = false;
        String value = "";
        while (!valid) {
            String text = ask(label, null);
            if (!text.isEmpty() && text.matches("[A-Za-z0-9_.-]{1,30}")) {
                value = text;
                valid = true;
            } else {
                invalid("Use 1-30 letters, numbers, dots, underscores, or dashes.");
            }
        }
        return value;
    }

    public static String readPassword(String label) {
        boolean valid = false;
        String value = "";
        while (!valid) {
            String text = ask(label, null);
            if (!text.isEmpty() && !text.contains("|")) {
                value = text;
                valid = true;
            } else {
                invalid("Password cannot be empty or contain |.");
            }
        }
        return value;
    }

    public static LocalDate readDate(String label, String currentValue) {
        boolean valid = false;
        LocalDate value = null;
        LocalDate today = LocalDate.now();
        LocalDate latest = today.plusYears(1);
        while (!valid) {
            String text = ask(label, currentValue);
            if (!text.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) {
                invalid("Use YYYY-MM-DD, for example 2026-12-24.");
                continue;
            }
            try {
                LocalDate date = LocalDate.parse(text);
                if (date.isBefore(today)) {
                    invalid("Date cannot be in the past.");
                } else if (date.isAfter(latest)) {
                    invalid("Bookings are limited to one year ahead (latest: " + latest + ").");
                } else if (date.equals(today) && !LocalTime.now().isBefore(Reservation.LAST_BOOKING_TIME)) {
                    invalid("Today's booking time has ended. Choose another date.");
                } else {
                    value = date;
                    valid = true;
                }
            } catch (DateTimeParseException e) {
                invalid("That date does not exist on the calendar.");
            }
        }
        return value;
    }

    public static LocalTime readTime(String label, LocalDate date, String currentValue) {
        boolean valid = false;
        LocalTime value = null;
        while (!valid) {
            String text = ask(label, currentValue);
            if (!text.matches("([01][0-9]|2[0-3]):[0-5][0-9]")) {
                invalid("Use 24-hour HH:mm format, such as 18:30.");
                continue;
            }
            try {
                LocalTime time = LocalTime.parse(text);
                if (time.isBefore(Reservation.OPENING_TIME) || time.isAfter(Reservation.LAST_BOOKING_TIME)) {
                    invalid("Booking hours are " + Reservation.OPENING_TIME + " to " + Reservation.LAST_BOOKING_TIME + ".");
                } else if (date.equals(LocalDate.now()) && !time.isAfter(LocalTime.now())) {
                    invalid("That time has already passed today.");
                } else {
                    value = time;
                    valid = true;
                }
            } catch (DateTimeParseException e) {
                invalid("Please enter a valid time.");
            }
        }
        return value;
    }

    public static String readReservationId(String label) {
        boolean valid = false;
        String value = "";
        while (!valid) {
            String text = ask(label, null);
            if (text.matches("[0-9]{6}")) {
                value = "RN-" + text;
                valid = true;
            } else {
                invalid("Enter only the 6 digits of the ID, without RN-.");
            }
        }
        return value;
    }

    public static String readReservationIdOrSkip(String label) {
        boolean valid = false;
        String value = null;
        while (!valid) {
            String text = ask(label, null);
            if (text.isEmpty()) {
                valid = true;
            } else if (text.matches("[0-9]{6}")) {
                value = "RN-" + text;
                valid = true;
            } else {
                invalid("Enter six digits or press Enter to skip.");
            }
        }
        return value;
    }

    public static boolean readYesNo(String label) {
        boolean valid = false;
        boolean answer = false;
        while (!valid) {
            String text = ask(label + " (y/n)", null);
            // Yes/no is a small, fixed set of answers; handle each one explicitly.
            switch (text) {
                case "y":
                case "Y":
                    answer = true;
                    valid = true;
                    break;
                case "n":
                case "N":
                    answer = false;
                    valid = true;
                    break;
                default:
                    invalid("Type y for yes or n for no.");
            }
        }
        return answer;
    }

    public static void pause() {
        System.out.print("\nPress Enter to continue...");
        if (SCANNER.hasNextLine()) {
            SCANNER.nextLine();
        }
    }
}
