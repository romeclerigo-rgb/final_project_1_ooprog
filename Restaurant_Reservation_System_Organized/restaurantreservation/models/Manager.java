package restaurantreservation.models;

import restaurantreservation.menus.*;

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
}
