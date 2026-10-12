
import java.util.Scanner;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

public class Validation {

    public int getMenuChoice(Scanner sc, int min, int max) {

        boolean valid = false;
        int choice = 0;

        while (!valid) {

            System.out.print("Enter your choice: ");
            String input = sc.nextLine();

            if (input.matches("[0-9]+")) {

                try {

                    choice = Integer.parseInt(input);

                    if (choice >= min && choice <= max) {
                        valid = true;
                    } else {
                        System.out.println(
                            "Invalid input! Please enter a number from "
                            + min + " to " + max + " only."
                        );
                    }

                } catch (NumberFormatException e) {

                    System.out.println(
                        "Invalid input! The number is too large."
                    );
                }

            } else {

                System.out.println(
                    "Invalid input! Please enter numbers only."
                );
            }
        }

        return choice;
    }

    public String getFullName(Scanner sc) {

        boolean valid = false;
        String name = "";

        while (!valid) {

            System.out.print("Enter full name: ");
            String input = sc.nextLine();

            if (input.trim().isEmpty()) {

                System.out.println("Name cannot be empty!");

            } else if (input.length() > 100) {

                System.out.println(
                    "Invalid name! Maximum length is 100 characters."
                );

            } else if (!input.matches(
                "[A-Za-z]+(?:[ .'-][A-Za-z]+)*"
            )) {

                System.out.println(
                    "Invalid name! Please enter a valid full name."
                );

            } else {

                name = input.trim();
                valid = true;
            }
        }

        return name;
    }

    public String getAddress(Scanner sc) {

        boolean valid = false;
        String address = "";

        while (!valid) {

            System.out.print("Enter address: ");
            String input = sc.nextLine();

            if (input.trim().isEmpty()) {

                System.out.println("Address cannot be empty!");

            } else if (input.length() > 200) {

                System.out.println(
                    "Invalid address! Maximum length is 200 characters."
                );

            } else {

                address = input.trim();
                valid = true;
            }
        }

        return address;
    }

    public String getEmailOrContact(Scanner sc) {

        boolean valid = false;
        String contact = "";

        while (!valid) {

            System.out.print("Enter email or contact number: ");
            String input = sc.nextLine();

            boolean email = input.matches(
                "[A-Za-z0-9]+(?:[._%+-][A-Za-z0-9]+)*"
                + "@[A-Za-z0-9-]+(?:\\.[A-Za-z0-9-]+)+"
            );

            boolean phone = input.matches(
                "(09\\d{9}|\\+639\\d{9})"
            );

            if (email || phone) {

                contact = input;
                valid = true;

            } else {

                System.out.println(
                    "Invalid email or contact number!"
                );
            }
        }

        return contact;
    }

    public String getReservationName(Scanner sc) {

        boolean valid = false;
        String name = "";

        while (!valid) {

            System.out.print("Enter reservation name: ");
            String input = sc.nextLine();

            if (input.trim().isEmpty()) {

                System.out.println(
                    "Reservation name cannot be empty!"
                );

            } else if (input.length() > 100) {

                System.out.println(
                    "Reservation name cannot exceed 100 characters."
                );

            } else {

                name = input.trim();
                valid = true;
            }
        }

        return name;
    }

    public LocalDate getReservationDate(Scanner sc) {

        boolean valid = false;
        LocalDate date = null;

        DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("uuuu-MM-dd")
                .withResolverStyle(ResolverStyle.STRICT);

        while (!valid) {

            System.out.print("Enter reservation date (YYYY-MM-DD): ");
            String input = sc.nextLine();

            if (!input.matches("\\d{4}-\\d{2}-\\d{2}")) {

                System.out.println(
                    "Invalid date format! Use YYYY-MM-DD."
                );

            } else {

                try {

                    date = LocalDate.parse(input, formatter);

                    if (date.isBefore(LocalDate.now())) {

                        System.out.println(
                            "Reservation date cannot be in the past!"
                        );

                    } else {

                        valid = true;
                    }

                } catch (DateTimeParseException e) {

                    System.out.println(
                        "Invalid date! Please enter a real calendar date."
                    );
                }
            }
        }

        return date;
    }

    public LocalTime getReservationTime(Scanner sc) {

        boolean valid = false;
        LocalTime time = null;

        DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("HH:mm")
                .withResolverStyle(ResolverStyle.STRICT);

        while (!valid) {

            System.out.print("Enter reservation time (HH:mm): ");
            String input = sc.nextLine();

            if (!input.matches("\\d{2}:\\d{2}")) {

                System.out.println(
                    "Invalid time format! Use HH:mm, such as 18:30."
                );

            } else {

                try {

                    time = LocalTime.parse(input, formatter);
                    valid = true;

                } catch (DateTimeParseException e) {

                    System.out.println(
                        "Invalid time! Enter a valid time from 00:00 to 23:59."
                    );
                }
            }
        }

        return time;
    }

    public int getGuestCount(Scanner sc) {

        boolean valid = false;
        int guests = 0;

        while (!valid) {

            System.out.print("Enter number of guests: ");
            String input = sc.nextLine();

            if (!input.matches("[1-9]\\d{0,2}")) {

                System.out.println(
                    "Invalid input! Enter a whole number from 1 to 999."
                );

            } else {

                try {

                    guests = Integer.parseInt(input);
                    valid = true;

                } catch (NumberFormatException e) {

                    System.out.println(
                        "Invalid guest count! Please try again."
                    );
                }
            }
        }

        return guests;
    }

    public int getTableCount(Scanner sc) {

        boolean valid = false;
        int tables = 0;

        while (!valid) {

            System.out.print("Enter number of tables: ");
            String input = sc.nextLine();

            if (!input.matches("[1-9]\\d{0,2}")) {

                System.out.println(
                    "Invalid input! Enter a whole number from 1 to 999."
                );

            } else {

                try {

                    tables = Integer.parseInt(input);
                    valid = true;

                } catch (NumberFormatException e) {

                    System.out.println(
                        "Invalid table count! Please try again."
                    );
                }
            }
        }

        return tables;
    }

    public String getTableType(Scanner sc) {

        boolean valid = false;
        String tableType = "";

        while (!valid) {

            System.out.println("\nAvailable Table Types:");
            System.out.println("1. Standard");
            System.out.println("2. VIP");
            System.out.println("3. Private");

            System.out.print("Enter table type: ");
            String input = sc.nextLine();

            switch (input) {

                case "1":
                    tableType = "Standard";
                    valid = true;
                    break;

                case "2":
                    tableType = "VIP";
                    valid = true;
                    break;

                case "3":
                    tableType = "Private";
                    valid = true;
                    break;

                default:
                    System.out.println(
                        "Invalid table type! Please enter 1, 2, or 3."
                    );
            }
        }

        return tableType;
    }
}
