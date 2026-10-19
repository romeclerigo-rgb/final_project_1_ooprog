DataStore.java
100%
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * SINGLETON PATTERN
 *
 * The one and only place that keeps the data and reads/writes the .txt files.
 *   1. The constructor is private, so nobody else can write "new DataStore()".
 *   2. The single object is stored in the static field "instance".
 *   3. getInstance() creates it the first time and returns the same object after that.
 *
 * Files (in the "data" folder):
 *   CUSTOMER.txt  - registered customers
 *   RESERVE.txt   - active reservations
 *   STATUS.txt    - log of every check-in and check-out
 *   CANCELLED.txt - cancelled reservations (kept, not deleted)
 */
public class DataStore {

    private static DataStore instance;

    private static final String FOLDER = "data";
    private static final String CUSTOMER_FILE = "CUSTOMER.txt";
    private static final String RESERVE_FILE = "RESERVE.txt";
    private static final String STATUS_FILE = "STATUS.txt";
    private static final String CANCELLED_FILE = "CANCELLED.txt";

    private List<Customer> customers = new ArrayList<Customer>();
    private List<Reservation> reservations = new ArrayList<Reservation>();
    private List<Reservation> cancelled = new ArrayList<Reservation>();
    private List<String> statusLog = new ArrayList<String>();
    private List<Employee> employees = new ArrayList<Employee>();
    private Random random = new Random();

    private DataStore() {
        // The two employee accounts of the restaurant
        employees.add(new Staff("staff", "staff123", "Default Staff"));
        employees.add(new Manager("manager", "manager123", "Default Manager"));
    }

    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    // ============================================================ files

    /** Reads all .txt files into the lists. Damaged lines are skipped. */
    public void load() {
        for (String line : readLines(CUSTOMER_FILE)) {
            try {
                customers.add(Customer.fromFileLine(line));
            } catch (RuntimeException e) {
                System.out.println("[Warning] Skipped a damaged line in " + CUSTOMER_FILE);
            }
        }
        for (String line : readLines(RESERVE_FILE)) {
            try {
                reservations.add(Reservation.fromFileLine(line));
            } catch (RuntimeException e) {
                System.out.println("[Warning] Skipped a damaged line in " + RESERVE_FILE);
            }
        }
        for (String line : readLines(CANCELLED_FILE)) {
            try {
                cancelled.add(Reservation.fromFileLine(line));
            } catch (RuntimeException e) {
                System.out.println("[Warning] Skipped a damaged line in " + CANCELLED_FILE);
            }
        }
        statusLog.addAll(readLines(STATUS_FILE));
    }

    /** Writes all lists back to the .txt files. Called after every change. */
    public void save() {
        List<String> lines = new ArrayList<String>();
        for (Customer c : customers) {
            lines.add(c.toFileLine());
        }
        writeLines(CUSTOMER_FILE, lines);

        lines = new ArrayList<String>();
        for (Reservation r : reservations) {
            lines.add(r.toFileLine());
        }
        writeLines(RESERVE_FILE, lines);

        lines = new ArrayList<String>();
        for (Reservation r : cancelled) {
            lines.add(r.toFileLine());
        }
        writeLines(CANCELLED_FILE, lines);

        writeLines(STATUS_FILE, statusLog);
    }

    private List<String> readLines(String fileName) {
        List<String> lines = new ArrayList<String>();
        File file = new File(FOLDER, fileName);
        if (!file.exists()) {
            return lines;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("[Error] Could not read " + fileName + ": " + e.getMessage());
        }
        return lines;
    }

    private void writeLines(String fileName, List<String> lines) {
        new File(FOLDER).mkdirs();
        try (PrintWriter writer = new PrintWriter(new FileWriter(new File(FOLDER, fileName)))) {
            for (String line : lines) {
                writer.println(line);
            }
        } catch (IOException e) {
            System.out.println("[Error] Could not save " + fileName + ": " + e.getMessage());
        }
    }

    // ======================================================== customers

    public void addCustomer(Customer customer) {
        customers.add(customer);
        save();
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public Customer findCustomer(String customerId) {
        for (Customer c : customers) {
            if (c.getCustomerId().equals(customerId)) {
                return c;
            }
        }
        return null;
    }

    public Customer findCustomerByEmail(String email) {
        for (Customer c : customers) {
            if (c.getEmail().equalsIgnoreCase(email)) {
                return c;
            }
        }
        return null;
    }

    public boolean contactExists(String contactNumber) {
        for (Customer c : customers) {
            if (c.getContactNumber().equals(contactNumber)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 0 if nobody has this first + last name yet. Otherwise the next free number,
     * so the names become Juan Dela Cruz, Juan Dela Cruz(1), Juan Dela Cruz(2) ...
     */
    public int nextDuplicateNumber(String firstName, String lastName) {
        int number = 0;
        boolean taken = true;
        while (taken) {
            taken = false;
            for (Customer c : customers) {
                if (c.getFirstName().equalsIgnoreCase(firstName)
                        && c.getLastName().equalsIgnoreCase(lastName)
                        && c.getDuplicateNumber() == number) {
                    taken = true;
                    number++;
                    break;
                }
            }
        }
        return number;
    }

    /** Customer ID format: YEAR-6 random digits, e.g. 2026-123456. */
    public String newCustomerId() {
        String id;
        do {
            id = LocalDateTime.now().getYear() + "-" + sixDigits();
        } while (findCustomer(id) != null);
        return id;
    }

    // ===================================================== reservations

    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
        save();
    }

    /** All active reservations (not cancelled ones). */
    public List<Reservation> getReservations() {
        return reservations;
    }

    public List<Reservation> getCancelled() {
        return cancelled;
    }

    public Reservation findReservation(String reservationId) {
        for (Reservation r : reservations) {
            if (r.getReservationId().equals(reservationId)) {
                return r;
            }
        }
        return null;
    }

    /** A customer's reservations, including cancelled ones. */
    public List<Reservation> getReservationsOf(String customerId) {
        List<Reservation> list = new ArrayList<Reservation>();
        for (Reservation r : reservations) {
            if (r.getCustomerId().equals(customerId)) {
                list.add(r);
            }
        }
        for (Reservation r : cancelled) {
            if (r.getCustomerId().equals(customerId)) {
                list.add(r);
            }
        }
        return list;
    }

    /** Moves an approved cancellation from RESERVE.txt to CANCELLED.txt. */
    public void moveToCancelled(Reservation reservation) {
        reservations.remove(reservation);
        cancelled.add(reservation);
        save();
    }

    /** Reservation ID format: RN-6 random digits, e.g. RN-123456. */
    public String newReservationId() {
        String id;
        boolean used;
        do {
            id = "RN-" + sixDigits();
            used = findReservation(id) != null;
            for (Reservation r : cancelled) {
                if (r.getReservationId().equals(id)) {
                    used = true;
                }
            }
        } while (used);
        return id;
    }

    public String getCustomerName(String customerId) {
        Customer c = findCustomer(customerId);
        return c == null ? "(unknown)" : c.getName();
    }

    public String getCustomerEmail(String customerId) {
        Customer c = findCustomer(customerId);
        return c == null ? "-" : c.getEmail();
    }

    // ======================================================= status log

    /** Adds a line to STATUS.txt: date time|reservation ID|customer ID|customer name|status */
    public void logStatus(Reservation r) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        statusLog.add(time + "|" + r.getReservationId() + "|" + r.getCustomerId() + "|"
                + getCustomerName(r.getCustomerId()) + "|" + r.getStatus().getLabel());
        save();
    }

    public List<String> getStatusLog() {
        return statusLog;
    }

    // ======================================================== employees

    /** Returns the employee with this username and password, or null if they don't match. */
    public Employee findEmployee(String username, String password) {
        for (Employee e : employees) {
            if (e.getUsername().equals(username) && e.checkPassword(password)) {
                return e;
            }
        }
        return null;
    }

    private String sixDigits() {
        return String.format("%06d", random.nextInt(1000000));
    }
}