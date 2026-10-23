package restaurantreservation.strategies;

import restaurantreservation.models.*;

/** No fee (the update keeps the same date and time). */
public class NoCharge implements ChargeStrategy {

    @Override
    public double computeCharge(Reservation reservation) {
        return 0;
    }
}
