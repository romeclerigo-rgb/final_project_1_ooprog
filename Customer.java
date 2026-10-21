/**
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
        super(makeFullName(firstName, lastName, duplicateNumber));
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.duplicateNumber = duplicateNumber;
        this.contactNumber = contactNumber;
        this.email = email;
    }

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

    @Override
    public String getRole() {
        return "Customer";
    }

    @Override
    public void showMenu() {
        new CustomerMenu(this).run();
    }

    /** Turns this customer into one line of text for CUSTOMER.txt. */
    public String toFileLine() {
        return customerId + "|" + firstName + "|" + lastName + "|" + duplicateNumber + "|"
                + contactNumber + "|" + email;
    }

    /** Rebuilds a customer from one line of CUSTOMER.txt. */
    public static Customer fromFileLine(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 6) {
            throw new IllegalArgumentException("expected 6 values but found " + parts.length);
        }
        return new Customer(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]), parts[4], parts[5]);
    }
}
