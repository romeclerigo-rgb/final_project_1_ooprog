package restaurantreservation.strategies;

import restaurantreservation.models.*;
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
