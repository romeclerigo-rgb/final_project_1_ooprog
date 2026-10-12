public class Customer extends Person {

    private String customerID;
    private String emailOrContact;

    public Customer(String customerID, String fullName,
                    String address, String emailOrContact) {

        super(fullName, address, emailOrContact);

        this.customerID = customerID;
        this.emailOrContact = emailOrContact;
    }

    public String getCustomerID() {
        return customerID;
    }

    public String getEmailOrContact() {
        return emailOrContact;
    }

    public void setEmailOrContact(String emailOrContact) {
        this.emailOrContact = emailOrContact;
        setContact(emailOrContact);
    }

    @Override
    public String getRole() {
        return "Customer";
    }

    public void displayInfo() {
        System.out.println("Customer ID: " + customerID);
        System.out.println("Full Name: " + getFullName());
        System.out.println("Address: " + getAddress());
        System.out.println("Email or Contact: " + emailOrContact);
        System.out.println("Role: " + getRole());
    }
}
