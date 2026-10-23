package restaurantreservation.exceptions;

import restaurantreservation.app.*;
import restaurantreservation.models.*;
import restaurantreservation.menus.*;
import restaurantreservation.strategies.*;
import restaurantreservation.storage.*;
import restaurantreservation.ui.*;
import restaurantreservation.exceptions.*;

/**
 * Thrown when the user types \ at any question.
 * The screen that catches it goes back ONE question
 * (or back to the menu if it was the first question).
 */
public class BackException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BackException() {
        super("The user typed \\ to go back.");
    }
}