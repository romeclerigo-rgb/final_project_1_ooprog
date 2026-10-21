
/**
 * Abstract parent for every person who can use the system.
 *
 * ABSTRACTION: every user has a name and a role, but each type of user
 * decides which menu it should open.
 * POLYMORPHISM: Main can call showMenu() without checking the exact class.
 */
public abstract class User {
    private final String name;

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract String getRole();

    public abstract void showMenu();
}    

