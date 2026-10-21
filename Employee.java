<<<<<<< HEAD
/** Shared account information for restaurant employees. */
public abstract class Employee extends User {
    private final String username;
    private final String password;

    public Employee(String username, String password, String name) {
        super(name);
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    /** Kept simple for this file-based school project; not for real passwords. */
    public boolean checkPassword(String enteredPassword) {
        return password.equals(enteredPassword);
    }
=======
public class Employee {

>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
}
