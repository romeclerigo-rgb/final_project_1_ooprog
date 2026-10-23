package restaurantreservation.models;

import restaurantreservation.menus.*;

/**
 * A registered customer.
 * Current format: accountKey|firstName|lastName|duplicateNumber|address|email|password.
 * New accounts use email as the internal key; older records with IDs remain readable.
 */
public class Customer extends User {
    private final String customerId, firstName, lastName, address, email, password;
    private final int duplicateNumber;

    public Customer(String customerId, String firstName, String lastName, int duplicateNumber,
                    String address, String email, String password) {
        super(makeFullName(firstName, lastName, duplicateNumber));
        this.customerId = customerId; this.firstName = firstName; this.lastName = lastName;
        this.duplicateNumber = duplicateNumber; this.address = address;
        this.email = email; this.password = password;
    }
    private static String makeFullName(String firstName, String lastName, int duplicateNumber) {
        String name = firstName + " " + lastName;
        return duplicateNumber > 0 ? name + " (" + duplicateNumber + ")" : name;
    }
    public String getCustomerId() { return customerId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public int getDuplicateNumber() { return duplicateNumber; }
    public String getAddress() { return address; }
    public String getEmail() { return email; }
    public boolean checkPassword(String enteredPassword) {
        return password != null && !password.isEmpty() && password.equals(enteredPassword);
    }
    @Override public String getRole() { return "Customer"; }
    @Override public void showMenu() { new CustomerMenu(this).run(); }
    public String toFileLine() {
        return customerId + "|" + firstName + "|" + lastName + "|" + duplicateNumber
                + "|" + address + "|" + email + "|" + password;
    }
    public static Customer fromFileLine(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length == 6) {
            return new Customer(p[0], p[1], p[2], Integer.parseInt(p[3]), "Not provided", p[5], "");
        }
        if (p.length == 7 && p[5].matches("09[0-9]{9}")) {
            // Legacy format had contact number and email but no password.
            return new Customer(p[0], p[1], p[2], Integer.parseInt(p[3]), p[4], p[6], "");
        }
        if (p.length == 7) {
            return new Customer(p[0], p[1], p[2], Integer.parseInt(p[3]), p[4], p[5], p[6]);
        }
        throw new IllegalArgumentException("Invalid customer record.");
    }
}
