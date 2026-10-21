<<<<<<< HEAD
/** Staff handles everyday reservation and payment operations. */
public class Staff extends Employee {
    public Staff(String username, String password, String name) {
        super(username, password, name);
    }

    @Override
    public String getRole() {
        return "Staff";
    }

    @Override
    public void showMenu() {
        new StaffMenu().run();
    }
=======
public class Staff {

>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
}
