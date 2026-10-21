import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** Fully Paid first, then Partially Paid, then Unpaid. Ties are sorted by date. */
public class SortByPaymentStatus implements SortStrategy {

    @Override
    public void sort(List<Reservation> reservations) {
        Collections.sort(reservations, new Comparator<Reservation>() {
            @Override
            public int compare(Reservation a, Reservation b) {
                int byPayment = a.getPaymentRank() - b.getPaymentRank();
                if (byPayment != 0) {
                    return byPayment;
                }
                return a.getDate().compareTo(b.getDate());
            }
        });
    }

    @Override
    public String getName() {
        return "Payment status (Fully Paid, Partially Paid, Unpaid)";
    }
}
