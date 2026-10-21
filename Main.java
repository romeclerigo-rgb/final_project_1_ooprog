/** Application entry point: registration and role-based login. */
public class Main {
    private static final DataStore STORE = DataStore.getInstance();

    public static void main(String[] args) {
        STORE.load();
        boolean running = true;
        while (running) {
            System.out.println("\n==================================================");
            System.out.println("         RESTAURANT RESERVATION SYSTEM");
            System.out.println("==================================================");
            System.out.println("  [1] Register as a new customer");
            System.out.println("  [2] Customer login");
            System.out.println("  [3] Staff / Manager login");
            System.out.println("  [0] Exit");
            System.out.println("(Type \\ at a question to return to the previous menu)");
            System.out.println("==================================================");

            try {
                int choice = Input.readMenuChoice(0, 3);
                switch (choice) {
                    case 0:
                        running = false;
                        break;
                    case 1:
                        register();
                        break;
                    case 2:
                        customerLogin();
                        break;
                    case 3:
                        employeeLogin();
                        break;
                    default:
                        
                        System.out.println("Invalid menu option.");
                }
            } catch (BackException e) {
                System.out.println("  You are already at the main menu.");
            }
        }
        System.out.println("Thank you for using the Restaurant Reservation System. Goodbye!");
    }

    /** Registration collects all customer information required by the proposal. */
    private static void register() {
        System.out.println("\n--- Customer Registration ---");
        String firstName = null;
        String lastName = null;
        String address = null;
        String contact = null;
        String email = null;
        int step = 1;

        while (step <= 5) {
            try {
                // The step number identifies one exact registration question.
                // Keep uniqueness checks beside the field they validate.
                switch (step) {
                    case 1:
                        firstName = Input.readName("First name", "Juan or Maria Clara");
                        break;
                    case 2:
                        lastName = Input.readName("Last name", "Santos or Dela Cruz");
                        break;
                    case 3:
                        address = Input.readAddress("Address");
                        break;
                    case 4:
                        contact = Input.readContactNumber("Contact number (09XXXXXXXXX)");
                        if (STORE.contactExists(contact)) {
                            System.out.println("  [Invalid] That contact number is already registered.");
                            continue;
                        }
                        break;
                    case 5:
                        email = Input.readEmail("Email address");
                        if (STORE.findCustomerByEmail(email) != null) {
                            System.out.println("  [Invalid] That email address is already registered.");
                            continue;
                        }
                        break;
                    default:
                        // The loop condition keeps the step in the 1-5 range.
                        break;
                }
                step++;
            } catch (BackException e) {
                if (step == 1) throw e;
                step--;
            }
        }

        int duplicateNumber = STORE.nextDuplicateNumber(firstName, lastName);
        Customer customer = new Customer(STORE.newCustomerId(), firstName, lastName,
                duplicateNumber, address, contact, email);
        STORE.addCustomer(customer);

        System.out.println("\nRegistration successful!");
        System.out.println("Customer ID: " + customer.getCustomerId());
        System.out.println("You can now log in using your email or contact number.");
        Input.pause();
    }

    /** The proposal allows login using either the saved email or mobile number. */
    private static void customerLogin() {
        System.out.println("\n--- Customer Login ---");
        boolean loggedIn = false;
        while (!loggedIn) {
            String identifier = Input.readEmailOrContact("Email address or contact number");
            // Try both supported identifiers. This avoids relying only on the presence of '@'.
            Customer customer = STORE.findCustomerByEmail(identifier);
            if (customer == null) {
                customer = STORE.findCustomerByContact(identifier);
            }
            if (customer == null) {
                System.out.println("  [Invalid] No customer account matches that email/contact number.");
            } else {
                openMenu(customer);
                loggedIn = true;
            }
        }
    }

    /** Employee accounts use username/password; passwords are demo credentials for this project. */
    private static void employeeLogin() {
        System.out.println("\n--- Staff / Manager Login ---");
        boolean authenticated = false;
        while (!authenticated) {
            String username = null;
            String password = null;
            int step = 1;
            while (step <= 2) {
                try {
                    // Username and password are two separate form steps.
                    switch (step) {
                        case 1:
                            username = Input.readUsername("Username");
                            break;
                        case 2:
                            password = Input.readPassword("Password");
                            break;
                        default:
                            break;
                    }
                    step++;
                } catch (BackException e) {
                    if (step == 1) throw e;
                    step--;
                }
            }

            Employee employee = STORE.findEmployee(username, password);
            if (employee == null) {
                System.out.println("  [Invalid] Incorrect username or password. Please try again.");
            } else {
                openMenu(employee);
                authenticated = true;
            }
        }
    }

    /** POLYMORPHISM: the user's actual class decides which menu showMenu() opens. */
    private static void openMenu(User user) {
        System.out.println("\nWelcome, " + user.getName() + "! Logged in as " + user.getRole() + ".");
        user.showMenu();
    }
}
