package proj;


/**
 * Reprezentuje pojedynczy węzeł grafu z jego współrzędnymi.
 */
public class Wierzcholek {
    private String nazwa;
    private double x;
    private double y;

    public Wierzcholek(String nazwa, double x, double y) {
        this.nazwa = nazwa;
        this.x = x;
        this.y = y;
    }

    public String getNazwa() { return nazwa; }
    public double getX() { return x; }
    public double getY() { return y; }
}
