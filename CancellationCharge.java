/** 20% of the table cost when a cancellation is approved. */
public class CancellationCharge implements ChargeStrategy {

    public static final double RATE = 0.20;

    @Override
    public double computeCharge(Reservation reservation) {
        return reservation.getTableCost() * RATE;
    }
}
