package restaurantreservation.models;

import restaurantreservation.app.*;
import restaurantreservation.models.*;
import restaurantreservation.menus.*;
import restaurantreservation.strategies.*;
import restaurantreservation.storage.*;
import restaurantreservation.ui.*;
import restaurantreservation.exceptions.*;

/** Shared account information for restaurant employees. */
public abstract class Employee extends User {
    private final String email;
    private final String password;

    public Employee(String email, String password, String name) {
        super(name);
        this.email = email;
        this.password = password;
    }
    public String getEmail() { return email; }
    /** Compatibility alias for older menu code; this value is an email now. */
    public String getUsername() { return email; }
    public boolean checkPassword(String enteredPassword) {
        return password.equals(enteredPassword);
    }
}
