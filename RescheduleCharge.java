/** Flat PHP 200 fee when the date or time is changed. */
public class RescheduleCharge implements ChargeStrategy {

    public static final double FEE = 200.00;

    @Override
    public double computeCharge(Reservation reservation) {
        return FEE;
    }
}
