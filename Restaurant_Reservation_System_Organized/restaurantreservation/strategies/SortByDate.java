package restaurantreservation.strategies;

import restaurantreservation.models.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** Earliest date first; same day sorted by time. */
public class SortByDate implements SortStrategy {

    @Override
    public void sort(List<Reservation> reservations) {
        Collections.sort(reservations, new Comparator<Reservation>() {
            @Override
            public int compare(Reservation a, Reservation b) {
                int byDate = a.getDate().compareTo(b.getDate());
                if (byDate != 0) {
                    return byDate;
                }
                return a.getTime().compareTo(b.getTime());
            }
        });
    }

    @Override
    public String getName() {
        return "Date (earliest first)";
    }
}
