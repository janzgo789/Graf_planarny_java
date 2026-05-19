package proj;


/**
 * Reprezentuje krawędź łączącą dwa wierzchołki.
 */
public class Krawedz {
    private String nazwa;
    private Wierzcholek v1;
    private Wierzcholek v2;
    private double waga;

    public Krawedz(String nazwa, Wierzcholek v1, Wierzcholek v2, double waga) {
        this.nazwa = nazwa;
        this.v1 = v1;
        this.v2 = v2;
        this.waga = waga;
    }

    // Gettery do rysowania
    public Wierzcholek getV1() { return v1; }
    public Wierzcholek getV2() { return v2; }
    public double getWaga() { return waga; }
    public void setWaga(double waga) { this.waga = waga; }

    @Override
    public String toString() {
        return String.format("%-16s %-16s %-16s %-16f", nazwa, v1.getNazwa(), v2.getNazwa(), waga);
    }
}
