<<<<<<< HEAD
=======
DataStore.java
100%
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
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
<<<<<<< HEAD
 * SINGLETON + FILE HANDLING
 *
 * Only one DataStore instance is used by the application. It owns the lists
 * in memory and saves them to CUSTOMER.txt, RESERVE.txt, STATUS.txt, and
 * CANCELLED.txt inside the data folder.
 *
 * The collections returned by getters are copies, so a menu cannot accidentally
 * remove records from the stored lists just by filtering or sorting them.
 */
public class DataStore {
    private static DataStore instance;
=======
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

>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
    private static final String FOLDER = "data";
    private static final String CUSTOMER_FILE = "CUSTOMER.txt";
    private static final String RESERVE_FILE = "RESERVE.txt";
    private static final String STATUS_FILE = "STATUS.txt";
    private static final String CANCELLED_FILE = "CANCELLED.txt";

<<<<<<< HEAD
    private final List<Customer> customers = new ArrayList<Customer>();
    private final List<Reservation> reservations = new ArrayList<Reservation>();
    private final List<Reservation> cancelled = new ArrayList<Reservation>();
    private final List<String> statusLog = new ArrayList<String>();
    private final List<Employee> employees = new ArrayList<Employee>();
    private final Random random = new Random();
    private boolean loaded;

    private DataStore() {
        // Demo accounts for the school project. Change these before a real deployment.
        employees.add(new Staff("staff", "staff123", "Restaurant Staff"));
        employees.add(new Manager("manager", "manager123", "Restaurant Manager"));
=======
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
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
    }

    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

<<<<<<< HEAD
    /** Load once per application run. Clearing first prevents repeated calls from duplicating records. */
    public void load() {
        if (loaded) {
            return;
        }
        customers.clear();
        reservations.clear();
        cancelled.clear();
        statusLog.clear();
        ensureFilesExist();

        loadCustomers();
        loadReservations();
        loadCancelledReservations();
        statusLog.addAll(readLines(STATUS_FILE));
        loaded = true;
    }

    private void loadCustomers() {
=======
    // ============================================================ files

    /** Reads all .txt files into the lists. Damaged lines are skipped. */
    public void load() {
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
        for (String line : readLines(CUSTOMER_FILE)) {
            try {
                customers.add(Customer.fromFileLine(line));
            } catch (RuntimeException e) {
<<<<<<< HEAD
                System.out.println("[Warning] Skipped one damaged record in " + CUSTOMER_FILE + ".");
            }
        }
    }

    private void loadReservations() {
=======
                System.out.println("[Warning] Skipped a damaged line in " + CUSTOMER_FILE);
            }
        }
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
        for (String line : readLines(RESERVE_FILE)) {
            try {
                reservations.add(Reservation.fromFileLine(line));
            } catch (RuntimeException e) {
<<<<<<< HEAD
                System.out.println("[Warning] Skipped one damaged record in " + RESERVE_FILE + ".");
            }
        }
    }

    private void loadCancelledReservations() {
=======
                System.out.println("[Warning] Skipped a damaged line in " + RESERVE_FILE);
            }
        }
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
        for (String line : readLines(CANCELLED_FILE)) {
            try {
                cancelled.add(Reservation.fromFileLine(line));
            } catch (RuntimeException e) {
<<<<<<< HEAD
                System.out.println("[Warning] Skipped one damaged record in " + CANCELLED_FILE + ".");
            }
        }
    }

    /** Creates the four proposal-required files if they do not exist yet. */
    private void ensureFilesExist() {
        File folder = new File(FOLDER);
        if (!folder.exists() && !folder.mkdirs()) {
            System.out.println("[Warning] Could not create the data folder.");
        }
        createIfMissing(CUSTOMER_FILE);
        createIfMissing(RESERVE_FILE);
        createIfMissing(STATUS_FILE);
        createIfMissing(CANCELLED_FILE);
    }

    private void createIfMissing(String fileName) {
        try {
            File file = new File(FOLDER, fileName);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            if (file.createNewFile()) {
                System.out.println("[Info] Created data/" + fileName);
            }
        } catch (IOException e) {
            System.out.println("[Error] Could not initialize " + fileName + ": " + e.getMessage());
        }
    }

    /** Rewrites all four files from the in-memory records after a successful change. */
    public void save() {
        ensureFilesExist();
        List<String> lines = new ArrayList<String>();
        for (Customer customer : customers) {
            lines.add(customer.toFileLine());
=======
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
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
        }
        writeLines(CUSTOMER_FILE, lines);

        lines = new ArrayList<String>();
<<<<<<< HEAD
        for (Reservation reservation : reservations) {
            lines.add(reservation.toFileLine());
=======
        for (Reservation r : reservations) {
            lines.add(r.toFileLine());
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
        }
        writeLines(RESERVE_FILE, lines);

        lines = new ArrayList<String>();
<<<<<<< HEAD
        for (Reservation reservation : cancelled) {
            lines.add(reservation.toFileLine());
        }
        writeLines(CANCELLED_FILE, lines);
=======
        for (Reservation r : cancelled) {
            lines.add(r.toFileLine());
        }
        writeLines(CANCELLED_FILE, lines);

>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
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
<<<<<<< HEAD
        File file = new File(FOLDER, fileName);
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.println(line);
            }
            if (writer.checkError()) {
                System.out.println("[Error] A write error occurred for " + fileName + ".");
            }
=======
        new File(FOLDER).mkdirs();
        try (PrintWriter writer = new PrintWriter(new FileWriter(new File(FOLDER, fileName)))) {
            for (String line : lines) {
                writer.println(line);
            }
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
        } catch (IOException e) {
            System.out.println("[Error] Could not save " + fileName + ": " + e.getMessage());
        }
    }

<<<<<<< HEAD
    // --------------------------------------------------------------- customers
=======
    // ======================================================== customers
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8

    public void addCustomer(Customer customer) {
        customers.add(customer);
        save();
    }

    public List<Customer> getCustomers() {
<<<<<<< HEAD
        return new ArrayList<Customer>(customers);
    }

    public Customer findCustomer(String customerId) {
        for (Customer customer : customers) {
            if (customer.getCustomerId().equals(customerId)) {
                return customer;
=======
        return customers;
    }

    public Customer findCustomer(String customerId) {
        for (Customer c : customers) {
            if (c.getCustomerId().equals(customerId)) {
                return c;
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
            }
        }
        return null;
    }

    public Customer findCustomerByEmail(String email) {
<<<<<<< HEAD
        for (Customer customer : customers) {
            if (customer.getEmail().equalsIgnoreCase(email)) {
                return customer;
            }
        }
        return null;
    }

    public Customer findCustomerByContact(String contact) {
        for (Customer customer : customers) {
            if (customer.getContactNumber().equals(contact)) {
                return customer;
=======
        for (Customer c : customers) {
            if (c.getEmail().equalsIgnoreCase(email)) {
                return c;
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
            }
        }
        return null;
    }

    public boolean contactExists(String contactNumber) {
<<<<<<< HEAD
        return findCustomerByContact(contactNumber) != null;
    }

    /** Returns the next display suffix for a repeated first/last-name pair. */
    public int nextDuplicateNumber(String firstName, String lastName) {
        int nextNumber = 0;

        // A single pass is enough: no repeated while/for searching is needed.
        for (Customer customer : customers) {
            boolean sameFirstName = customer.getFirstName().equalsIgnoreCase(firstName);
            boolean sameLastName = customer.getLastName().equalsIgnoreCase(lastName);
            if (sameFirstName && sameLastName) {
                nextNumber = Math.max(nextNumber, customer.getDuplicateNumber() + 1);
            }
        }
        return nextNumber;
    }

    public String newCustomerId() {
        String id = "";
        boolean available = false;
        while (!available) {
            id = LocalDateTime.now().getYear() + "-" + sixDigits();
            available = findCustomer(id) == null;
        }
        return id;
    }

    // ------------------------------------------------------------- reservations
=======
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
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8

    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
        save();
    }

<<<<<<< HEAD
    public List<Reservation> getReservations() {
        return new ArrayList<Reservation>(reservations);
    }

    public List<Reservation> getCancelled() {
        return new ArrayList<Reservation>(cancelled);
    }

    public List<Reservation> getAllReservationsIncludingCancelled() {
        List<Reservation> all = getReservations();
        all.addAll(cancelled);
        return all;
    }

    public Reservation findReservation(String reservationId) {
        for (Reservation reservation : reservations) {
            if (reservation.getReservationId().equals(reservationId)) {
                return reservation;
            }
        }
        return null;
    }

    public Reservation findAnyReservation(String reservationId) {
        Reservation active = findReservation(reservationId);
        if (active != null) {
            return active;
        }
        for (Reservation reservation : cancelled) {
            if (reservation.getReservationId().equals(reservationId)) {
                return reservation;
=======
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
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
            }
        }
        return null;
    }

<<<<<<< HEAD
    public List<Reservation> getReservationsOf(String customerId) {
        List<Reservation> result = new ArrayList<Reservation>();
        for (Reservation reservation : reservations) {
            if (reservation.getCustomerId().equals(customerId)) {
                result.add(reservation);
            }
        }
        for (Reservation reservation : cancelled) {
            if (reservation.getCustomerId().equals(customerId)) {
                result.add(reservation);
            }
        }
        return result;
    }

    /** Searches by reservation name, case-insensitively; partial matches are allowed. */
    public List<Reservation> searchReservationsByName(String query, boolean includeCancelled) {
        List<Reservation> result = new ArrayList<Reservation>();
        String term = query.toLowerCase();
        for (Reservation reservation : reservations) {
            if (reservation.getReservationName().toLowerCase().contains(term)) {
                result.add(reservation);
            }
        }
        if (includeCancelled) {
            for (Reservation reservation : cancelled) {
                if (reservation.getReservationName().toLowerCase().contains(term)) {
                    result.add(reservation);
                }
            }
        }
        return result;
    }

    public List<Reservation> getReservationsByStatus(ReservationStatus status) {
        List<Reservation> result = new ArrayList<Reservation>();
        for (Reservation reservation : reservations) {
            if (reservation.getStatus() == status) {
                result.add(reservation);
            }
        }
        return result;
    }

    public List<Reservation> getReservationsByPaymentStatus(String paymentStatus) {
        List<Reservation> result = new ArrayList<Reservation>();
        for (Reservation reservation : reservations) {
            if (reservation.getPaymentStatus().equals(paymentStatus)) {
                result.add(reservation);
            }
        }
        return result;
    }

    /** An approved cancellation is archived, not permanently deleted. */
    public void moveToCancelled(Reservation reservation) {
        if (reservations.remove(reservation)) {
            cancelled.add(reservation);
            save();
        }
    }

    public String newReservationId() {
        String id = "";
        boolean available = false;
        while (!available) {
            id = "RN-" + sixDigits();
            available = findAnyReservation(id) == null;
        }
=======
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
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
        return id;
    }

    public String getCustomerName(String customerId) {
<<<<<<< HEAD
        Customer customer = findCustomer(customerId);
        return customer == null ? "(unknown)" : customer.getName();
    }

    public String getCustomerEmail(String customerId) {
        Customer customer = findCustomer(customerId);
        return customer == null ? "-" : customer.getEmail();
    }

    // --------------------------------------------------------------- status log

    /** STATUS.txt stores a history line for each check-in/check-out transition. */
    public void logStatus(Reservation reservation) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        statusLog.add(time + "|" + reservation.getReservationId() + "|" + reservation.getCustomerId()
                + "|" + getCustomerName(reservation.getCustomerId()) + "|" + reservation.getStatus().getLabel());
=======
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
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
        save();
    }

    public List<String> getStatusLog() {
<<<<<<< HEAD
        return new ArrayList<String>(statusLog);
    }

    // --------------------------------------------------------------- employees

    public Employee findEmployee(String username, String password) {
        for (Employee employee : employees) {
            if (employee.getUsername().equals(username) && employee.checkPassword(password)) {
                return employee;
=======
        return statusLog;
    }

    // ======================================================== employees

    /** Returns the employee with this username and password, or null if they don't match. */
    public Employee findEmployee(String username, String password) {
        for (Employee e : employees) {
            if (e.getUsername().equals(username) && e.checkPassword(password)) {
                return e;
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
            }
        }
        return null;
    }

    private String sixDigits() {
        return String.format("%06d", random.nextInt(1000000));
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
