/** The three kinds of tables, with their seats and price per table. */
public enum TableType {
    REGULAR("Regular", 4, 500.00),
    FAMILY("Family", 8, 1200.00),
    VIP("VIP", 6, 2500.00);

    private final String label;
    private final int seats;
    private final double price;

    TableType(String label, int seats, double price) {
        this.label = label;
        this.seats = seats;
        this.price = price;
    }

    public String getLabel() {
        return label;
    }

    public int getSeats() {
        return seats;
    }

    public double getPrice() {
        return price;
    }

    /** How many tables of this type are needed for the guests (rounded up). */
    public int tablesNeeded(int guests) {
        return (guests + seats - 1) / seats;
    }
}
