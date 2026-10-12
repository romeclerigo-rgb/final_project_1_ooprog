
public class Manager extends Person {

    private String managerID;

    public Manager(String managerID, String fullName,
                   String address, String contact) {

        super(fullName, address, contact);

        this.managerID = managerID;
    }

    public String getManagerID() {
        return managerID;
    }

    @Override
    public String getRole() {
        return "Manager";
    }

    public void displayInfo() {
        System.out.println("Manager ID: " + managerID);
        System.out.println("Full Name: " + getFullName());
        System.out.println("Address: " + getAddress());
        System.out.println("Contact: " + getContact());
        System.out.println("Role: " + getRole());
    }
}
