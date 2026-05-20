package proj;


/**
 * Reprezentuje pojedynczy węzeł grafu z jego współrzędnymi.
 */
public class Wierzcholek {
    private final String nazwa;
    private double x;
    private double y;

    public Wierzcholek(String nazwa, double x, double y) {
        this.nazwa = nazwa;
        this.x = x;
        this.y = y;
    }

    public Wierzcholek(String nazwa) {
        this.nazwa = nazwa;
        this.x = 0.0;
        this.y = 0.0;
    }

    public String getNazwa() { return nazwa; }
    public double getX() { return x; }
    public double getY() { return y; }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }

    @Override
    public String toString() {
        return String.format("%-16s X: %-16.2f Y: %-16.2f", nazwa, x, y);
    }
}
