/**
 * A registered customer.
 * File format: customerId|firstName|lastName|duplicateNumber|address|contact|email
 *
 * The duplicate number keeps two customers with the same name distinguishable.
 */
public class Customer extends User {
    private final String customerId;
    private final String firstName;
    private final String lastName;
    private final int duplicateNumber;
    private final String address;
    private final String contactNumber;
    private final String email;

    public Customer(String customerId, String firstName, String lastName,
                    int duplicateNumber, String address, String contactNumber, String email) {
        super(makeFullName(firstName, lastName, duplicateNumber));
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.duplicateNumber = duplicateNumber;
        this.address = address;
        this.contactNumber = contactNumber;
        this.email = email;
    }

    /** Backward-compatible constructor for older saved records/code. */
    public Customer(String customerId, String firstName, String lastName,
                    int duplicateNumber, String contactNumber, String email) {
        this(customerId, firstName, lastName, duplicateNumber, "Not provided", contactNumber, email);
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
    public String getContactNumber() { return contactNumber; }
    public String getEmail() { return email; }

    @Override
    public String getRole() {
        return "Customer";
    }

    @Override
    public void showMenu() {
        new CustomerMenu(this).run();
    }

    /** Converts customer details into one line for CUSTOMER.txt. */
    public String toFileLine() {
        return customerId + "|" + firstName + "|" + lastName + "|" + duplicateNumber
                + "|" + address + "|" + contactNumber + "|" + email;
    }

    /** Supports both the new 7-field format and the old 6-field format. */
    public static Customer fromFileLine(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length == 6) {
            return new Customer(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]),
                    "Not provided", parts[4], parts[5]);
        }
        if (parts.length != 7) {
            throw new IllegalArgumentException("Invalid customer record.");
        }
        return new Customer(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]),
                parts[4], parts[5], parts[6]);
    }
}
