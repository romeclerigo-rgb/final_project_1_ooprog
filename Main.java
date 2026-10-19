

public class Main {

    private static DataStore store = DataStore.getInstance();

    public static void main(String[] args) {
        store.load();

        while (true) {
            System.out.println();
            System.out.println("==================================================");
            System.out.println("        RESTAURANT RESERVATION SYSTEM");
            System.out.println("==================================================");
            System.out.println("  [1] Register as a new customer");
            System.out.println("  [2] Customer login");
            System.out.println("  [3] Staff / Manager login");
            System.out.println("  [0] Exit");
            System.out.println("  (Type \\ at any question to go back)");

            int choice;
            try {
                choice = Input.readMenuChoice(0, 3);
            } catch (BackException e) {
                System.out.println("  You are already at the main menu.");
                continue;
            }
            if (choice == 0) {
                System.out.println("Thank you for using the Restaurant Reservation System. Goodbye!");
                return;
            }
            try {
                if (choice == 1) {
                    register();
                } else if (choice == 2) {
                    customerLogin();
                } else {
                    employeeLogin();
                }
            } catch (BackException e) {
                System.out.println("  Back to the main menu.");
            }
        }
    }

    /** Asks 4 questions. Each wrong answer repeats that question; \ goes back one question. */
    private static void register() {
        System.out.println("\n--- Customer Registration ---");
        String firstName = null;
        String lastName = null;
        String contact = null;
        String email = null;

        int step = 1;
        while (step <= 4) {
            try {
                if (step == 1) {
                    firstName = Input.readName("First name", "Juan or Maria Clara");
                } else if (step == 2) {
                    lastName = Input.readName("Last name", "Santos or Dela Cruz");
                } else if (step == 3) {
                    contact = Input.readContactNumber("Contact number (09XXXXXXXXX)");
                    if (store.contactExists(contact)) {
                        System.out.println("  [Invalid] That contact number is already registered. Use a different one.");
                        continue;   // ask the same question again
                    }
                } else {
                    email = Input.readEmail("Email address");
                    if (store.findCustomerByEmail(email) != null) {
                        System.out.println("  [Invalid] That email address is already registered. Use a different one.");
                        continue;
                    }
                }
                step++;
            } catch (BackException e) {
                if (step == 1) {
                    throw e;   // first question: back to the main menu
                }
                step--;
            }
        }

        int duplicateNumber = store.nextDuplicateNumber(firstName, lastName);
        Customer customer = new Customer(store.newCustomerId(), firstName, lastName, duplicateNumber, contact, email);
        store.addCustomer(customer);

        System.out.println("\n  Registration successful!");
        if (duplicateNumber > 0) {
            System.out.println("  The name " + firstName + " " + lastName
                    + " is already registered, so your name was saved as " + customer.getName() + ".");
        }
        System.out.println("  Your Customer ID is: " + customer.getCustomerId());
        System.out.println("  Log in with your email address: " + customer.getEmail());
        Input.pause();
    }

    /** Customers log in with their email only. A wrong email just asks again. */
    private static void customerLogin() {
        System.out.println("\n--- Customer Login ---");
        while (true) {
            String email = Input.readEmail("Email address");
            Customer customer = store.findCustomerByEmail(email);
            if (customer == null) {
                System.out.println("  [Invalid] No account is registered with " + email
                        + ". Check the spelling, or go back and register first.");
                continue;
            }
            openMenu(customer);
            return;
        }
    }

    /** Staff and manager log in with username and password. Wrong details ask again. */
    private static void employeeLogin() {
        System.out.println("\n--- Staff / Manager Login ---");
        String username = null;
        String password = null;
        int step = 1;
        while (step <= 2) {
            try {
                if (step == 1) {
                    username = Input.readUsername("Username");
                } else {
                    password = Input.readPassword("Password");
                }
                step++;
            } catch (BackException e) {
                if (step == 1) {
                    throw e;
                }
                step--;
            }
            if (step == 3) {
                Employee employee = store.findEmployee(username, password);
                if (employee == null) {
                    System.out.println("  [Invalid] Invalid username or password. Try again.");
                    step = 1;
                } else {
                    openMenu(employee);
                }
            }
        }
    }

    /**
     * POLYMORPHISM: the same line user.showMenu() opens a CustomerMenu,
     * StaffMenu or ManagerMenu depending on what kind of User it really is.
     */
    private static void openMenu(User user) {
        System.out.println("\n  Welcome, " + user.getName() + "! Logged in as " + user.getRole() + ".");
        user.showMenu();
    }
}