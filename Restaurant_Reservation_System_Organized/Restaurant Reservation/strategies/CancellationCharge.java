package restaurantreservation.strategies;

import restaurantreservation.app.*;
import restaurantreservation.models.*;
import restaurantreservation.menus.*;
import restaurantreservation.strategies.*;
import restaurantreservation.storage.*;
import restaurantreservation.ui.*;
import restaurantreservation.exceptions.*;

/** 20% of the table cost when a cancellation is approved. */
public class CancellationCharge implements ChargeStrategy {

    public static final double RATE = 0.20;

    @Override
    public double computeCharge(Reservation reservation) {
        return reservation.getTableCost() * RATE;
    }
}
