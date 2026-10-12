public class Staff extends Person {

    private String staffID;

    public Staff(String staffID, String fullName,
                 String address, String contact) {

        super(fullName, address, contact);

        this.staffID = staffID;
    }

    public String getStaffID() {
        return staffID;
    }

    @Override
    public String getRole() {
        return "Staff";
    }

    public void displayInfo() {
        System.out.println("Staff ID: " + staffID);
        System.out.println("Full Name: " + getFullName());
        System.out.println("Address: " + getAddress());
        System.out.println("Contact: " + getContact());
        System.out.println("Role: " + getRole());
    }
}
