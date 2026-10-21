<<<<<<< HEAD
/** Manager has the higher-level record and cancellation-approval functions. */
public class Manager extends Employee {
    public Manager(String username, String password, String name) {
        super(username, password, name);
    }

    @Override
    public String getRole() {
        return "Manager";
    }

    @Override
    public void showMenu() {
        new ManagerMenu().run();
    }
=======
public class Manager {

>>>>>>> 974082ec24bb78705a0d9bc166ae84d18ade79a8
}
