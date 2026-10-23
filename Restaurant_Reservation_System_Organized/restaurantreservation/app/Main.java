package restaurantreservation.app;

import restaurantreservation.models.*;
import restaurantreservation.storage.*;
import restaurantreservation.ui.*;
import restaurantreservation.exceptions.*;

/** Application entry point: customer registration and email/password login. */
public class Main {
    private static final DataStore STORE = DataStore.getInstance();
    private static final String BORDER = "==================================================";

    public static void main(String[] args) {
        STORE.load();
        boolean running = true;
        while (running) {
            printTitle("RESTAURANT RESERVATION SYSTEM");
            System.out.println("  [1] Sign In");
            System.out.println("  [2] Log In");
            System.out.println("  [0] Exit");
            System.out.println(BORDER);
            try {
                switch (Input.readMenuChoice(0, 2)) {
                    case 0: running = false; break;
                    case 1: register(); break;
                    case 2: login(); break;
                    default: System.out.println("Invalid menu option.");
                }
            } catch (BackException e) {
                System.out.println("You are already at the main menu.");
            }
        }
        Display.printHeader("THANK YOU FOR VISITING");
        System.out.println("  Thank you for using the Restaurant Reservation System.");
        System.out.println("  Goodbye!");
    }

    private static void printTitle(String title) {
        System.out.println("\n" + BORDER);
        System.out.println(center(title));
        System.out.println(BORDER);
    }

    private static String center(String title) {
        int padding = Math.max(0, (BORDER.length() - title.length()) / 2);
        // Java 8 has no String.repeat(), so the spaces are added with a loop.
        StringBuilder spaces = new StringBuilder();
        for (int i = 0; i < padding; i++) {
            spaces.append(' ');
        }
        return spaces + title;
    }

    private static void register() {
        printTitle("SIGN IN");
        String firstName = Input.readName("First name", "Juan or Maria Clara");
        String lastName = Input.readName("Last name", "Santos or Dela Cruz");
        String address = Input.readAddress("Address");
        String email = Input.readEmail("Email address");
        
        while (STORE.findCustomerByEmail(email) != null) {
            Display.printError("That email address is already registered.");
            email = Input.readEmail("Email address");
        }

        String password = Input.readNewPassword("Create password");
        int duplicateNumber = STORE.nextDuplicateNumber(firstName, lastName);
        // Use the email as th e internal account key instead of generating a customer ID.
        Customer customer = new Customer(email, firstName, lastName,
                duplicateNumber, address, email, password);
        STORE.addCustomer(customer);
        
        Display.printHeader("ACCOUNT CREATED SUCCESSFULLY");
        System.out.println("  Your account is ready to use.");
        System.out.println("  You can now log in using your email and password.");
        Input.pause();
    }

    private static void login() {
        printTitle("LOG IN");
        String email = Input.readEmail("Email address");
        String password = Input.readPassword("Password");

        // Check staff/manager email first, then check customer accounts.
        Employee employee = STORE.findEmployee(email, password);
        if (employee != null) {
            openMenu(employee);
            return;
        }
        Customer customer = STORE.findCustomerByEmail(email);
        if (customer != null && customer.checkPassword(password)) {
            openMenu(customer);
            return;
        }
        Display.printError("Incorrect email or password.");
        Input.pause();
    }

    private static void openMenu(User user) {
        Display.printHeader("WELCOME, " + user.getName().toUpperCase());
        System.out.println("  Logged in as: " + user.getRole());
        user.showMenu();
    }
}
