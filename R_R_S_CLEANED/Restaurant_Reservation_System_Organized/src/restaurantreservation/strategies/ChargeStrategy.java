package restaurantreservation.strategies;

import restaurantreservation.app.*;
import restaurantreservation.models.*;
import restaurantreservation.menus.*;
import restaurantreservation.strategies.*;
import restaurantreservation.storage.*;
import restaurantreservation.ui.*;
import restaurantreservation.exceptions.*;

/**
 * STRATEGY PATTERN (charges)
 *
 * Each class that implements this interface is one way to compute an extra fee.
 * The menus pick the right one at runtime:
 *   - NoCharge           -> update that keeps the same date and time
 *   - RescheduleCharge   -> update that moves the date or time
 *   - CancellationCharge -> approved cancellation
 */
public interface ChargeStrategy {

    double computeCharge(Reservation reservation);
}
