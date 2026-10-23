package restaurantreservation.storage;

import restaurantreservation.app.*;
import restaurantreservation.models.*;
import restaurantreservation.menus.*;
import restaurantreservation.strategies.*;
import restaurantreservation.storage.*;
import restaurantreservation.ui.*;
import restaurantreservation.exceptions.*;

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
    private static final String FOLDER = "data";
    private static final String CUSTOMER_FILE = "CUSTOMER.txt";
    private static final String RESERVE_FILE = "RESERVE.txt";
    private static final String STATUS_FILE = "STATUS.txt";
    private static final String CANCELLED_FILE = "CANCELLED.txt";

    private final List<Customer> customers = new ArrayList<Customer>();
    private final List<Reservation> reservations = new ArrayList<Reservation>();
    private final List<Reservation> cancelled = new ArrayList<Reservation>();
    private final List<String> statusLog = new ArrayList<String>();
    private final List<Employee> employees = new ArrayList<Employee>();
    private final Random random = new Random();
    private boolean loaded;

    private DataStore() {
        // Demo accounts
        employees.add(new Staff("staffUser@gmail.com", "admin123", "Restaurant Staff"));
        employees.add(new Manager("adminUser@gmail.com", "admin123", "Restaurant Manager"));
    }

    /** Lazy, thread-safe Singleton initialization. */
    private static class InstanceHolder {
        private static final DataStore INSTANCE = new DataStore();
    }

    public static DataStore getInstance() {
        return InstanceHolder.INSTANCE;
    }

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
        for (String line : readLines(CUSTOMER_FILE)) {
            try {
                customers.add(Customer.fromFileLine(line));
            } catch (RuntimeException e) {
                System.out.println("[Warning] Skipped one damaged record in " + CUSTOMER_FILE + ".");
            }
        }
    }

    private void loadReservations() {
        for (String line : readLines(RESERVE_FILE)) {
            try {
                reservations.add(Reservation.fromFileLine(line));
            } catch (RuntimeException e) {
                System.out.println("[Warning] Skipped one damaged record in " + RESERVE_FILE + ".");
            }
        }
    }

    private void loadCancelledReservations() {
        for (String line : readLines(CANCELLED_FILE)) {
            try {
                cancelled.add(Reservation.fromFileLine(line));
            } catch (RuntimeException e) {
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
        }
        writeLines(CUSTOMER_FILE, lines);

        lines = new ArrayList<String>();
        for (Reservation reservation : reservations) {
            lines.add(reservation.toFileLine());
        }
        writeLines(RESERVE_FILE, lines);

        lines = new ArrayList<String>();
        for (Reservation reservation : cancelled) {
            lines.add(reservation.toFileLine());
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
        File file = new File(FOLDER, fileName);
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.println(line);
            }
            if (writer.checkError()) {
                System.out.println("[Error] A write error occurred for " + fileName + ".");
            }
        } catch (IOException e) {
            System.out.println("[Error] Could not save " + fileName + ": " + e.getMessage());
        }
    }

    // --------------------------------------------------------------- customers

    public void addCustomer(Customer customer) {
        customers.add(customer);
        save();
    }

    public List<Customer> getCustomers() {
        return new ArrayList<Customer>(customers);
    }

    public Customer findCustomer(String customerId) {
        for (Customer customer : customers) {
            if (customer.getCustomerId().equals(customerId)) {
                return customer;
            }
        }
        return null;
    }

    public Customer findCustomerByEmail(String email) {
        for (Customer customer : customers) {
            if (customer.getEmail().equalsIgnoreCase(email)) {
                return customer;
            }
        }
        return null;
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


    // ------------------------------------------------------------- reservations

    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
        save();
    }

    public List<Reservation> getReservations() {
        return new ArrayList<Reservation>(reservations);
    }

    public List<Reservation> getCancelled() {
        return new ArrayList<Reservation>(cancelled);
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
            }
        }
        return null;
    }

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
        return id;
    }

    public String getCustomerName(String customerId) {
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
        save();
    }

    public List<String> getStatusLog() {
        return new ArrayList<String>(statusLog);
    }

    // --------------------------------------------------------------- employees

    public Employee findEmployee(String email, String password) {
        for (Employee employee : employees) {
            if (employee.getEmail().equalsIgnoreCase(email) && employee.checkPassword(password)) {
                return employee;
            }
        }
        return null;
    }

    private String sixDigits() {
        return String.format("%06d", random.nextInt(1000000));
    }
}
