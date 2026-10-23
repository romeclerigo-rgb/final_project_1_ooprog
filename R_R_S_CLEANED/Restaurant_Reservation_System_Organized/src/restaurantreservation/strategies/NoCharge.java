package restaurantreservation.strategies;

import restaurantreservation.app.*;
import restaurantreservation.models.*;
import restaurantreservation.menus.*;
import restaurantreservation.strategies.*;
import restaurantreservation.storage.*;
import restaurantreservation.ui.*;
import restaurantreservation.exceptions.*;

/** No fee (the update keeps the same date and time). */
public class NoCharge implements ChargeStrategy {

    @Override
    public double computeCharge(Reservation reservation) {
        return 0;
    }
}
