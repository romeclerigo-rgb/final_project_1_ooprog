package restaurantreservation.strategies;

import restaurantreservation.app.*;
import restaurantreservation.models.*;
import restaurantreservation.menus.*;
import restaurantreservation.strategies.*;
import restaurantreservation.storage.*;
import restaurantreservation.ui.*;
import restaurantreservation.exceptions.*;

import java.util.List;

/**
 * STRATEGY PATTERN (sorting)
 *
 * Staff and the manager choose how to sort; the menu just calls sort()
 * on whichever strategy was chosen.
 */
public interface SortStrategy {

    void sort(List<Reservation> reservations);

    String getName();
}
