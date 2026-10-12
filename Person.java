public abstract class Person {

    private String fullName;
    private String address;
    private String contact;

    public Person(String fullName, String address, String contact) {
        this.fullName = fullName;
        this.address = address;
        this.contact = contact;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAddress() {
        return address;
    }

    public String getContact() {
        return contact;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public abstract String getRole();
}
