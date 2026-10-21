/**
<<<<<<< HEAD
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
=======
 * A registered customer. Saved in data/CUSTOMER.txt as:
 * customerId|firstName|lastName|duplicateNumber|contactNumber|email
 *
 * duplicateNumber is 0 normally. If someone with the same first and last name
 * already exists, it becomes 1, 2, ... and the name shows as "Juan Dela Cruz(1)".
 */
public class Customer extends User {

    private String customerId;
    private String firstName;
    private String lastName;
    private int duplicateNumber;
    private String contactNumber;
    private String email;

    public Customer(String customerId, String firstName, String lastName, int duplicateNumber,
                    String contactNumber, String email) {
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
        super(makeFullName(firstName, lastName, duplicateNumber));
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.duplicateNumber = duplicateNumber;
<<<<<<< HEAD
        this.address = address;
=======
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
        this.contactNumber = contactNumber;
        this.email = email;
    }

<<<<<<< HEAD
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
=======
    /** "Juan" + "Dela Cruz" + 1  ->  "Juan Dela Cruz(1)" */
    private static String makeFullName(String firstName, String lastName, int duplicateNumber) {
        String name = firstName + " " + lastName;
        if (duplicateNumber > 0) {
            name = name + "(" + duplicateNumber + ")";
        }
        return name;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getDuplicateNumber() {
        return duplicateNumber;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getEmail() {
        return email;
    }
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8

    @Override
    public String getRole() {
        return "Customer";
    }

    @Override
    public void showMenu() {
        new CustomerMenu(this).run();
    }

<<<<<<< HEAD
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
=======
    /** Turns this customer into one line of text for CUSTOMER.txt. */
    public String toFileLine() {
        return customerId + "|" + firstName + "|" + lastName + "|" + duplicateNumber + "|"
                + contactNumber + "|" + email;
    }

    /** Rebuilds a customer from one line of CUSTOMER.txt. */
    public static Customer fromFileLine(String line) {
        String[] parts = line.split("\\|");
        return new Customer(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]), parts[4], parts[5]);
>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
    }
}
