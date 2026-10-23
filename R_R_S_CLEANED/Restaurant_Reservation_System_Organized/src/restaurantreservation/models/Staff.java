package restaurantreservation.models;

import restaurantreservation.app.*;
import restaurantreservation.models.*;
import restaurantreservation.menus.*;
import restaurantreservation.strategies.*;
import restaurantreservation.storage.*;
import restaurantreservation.ui.*;
import restaurantreservation.exceptions.*;

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
}
