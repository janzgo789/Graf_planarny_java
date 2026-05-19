package proj;



import java.util.*;

/**
 * Klasa reprezentująca strukturę danych grafu.
 * Przechowuje mapę wierzchołków (dla szybkiego wyszukiwania po nazwie)
 * oraz listę krawędzi.
 */
public class ModelGrafu {
    // Mapa: Klucz to nazwa wierzchołka, wartość to obiekt Wierzchołek
    private Map<String, Wierzcholek> wierzcholki;
    // Lista wszystkich krawędzi w grafie
    private List<Krawedz> krawedzie;

    public ModelGrafu() {
        this.wierzcholki = new HashMap<>();
        this.krawedzie = new ArrayList<>();
    }

    // Czyszczenie danych przed wczytaniem nowego pliku
    public void wyczysc() {
        wierzcholki.clear();
        krawedzie.clear();
    }

    // Dodawanie danych o wierzchołkach do modelu
    public void dodajWierzcholek(Wierzcholek w) {
        wierzcholki.put(w.getNazwa(), w);
    }

    // Dodawanie danych o krawędziach do modelu
    public void dodajKrawedz(Krawedz k) {
        krawedzie.add(k);
    }

    // Gettery wykorzystywane przez PanelGrafu do rysowania
    public Map<String, Wierzcholek> getWierzcholki() {
        return wierzcholki;
    }
    public List<Krawedz> getKrawedzie() {
        return krawedzie;
    }

    // Korygowanie współrzędnych wierzchołków o wagę krawędzi za pomocą algorytmu siłowego
    public void korygujWspolrzedneWagami() {
        int liczbaWierzcholkow = this.wierzcholki.size();
        int iteracje = Math.max(50, liczbaWierzcholkow * 10);
        double poczatkowaTemperatura = 0.05;
        for (int i = 0; i < iteracje; i++) {
            double aktualnaTemperatura = poczatkowaTemperatura *(1.0 - (double) i/iteracje);
            for (Krawedz k : this.getKrawedzie()) {
                Wierzcholek w1 = k.getV1();
                Wierzcholek w2 = k.getV2();

                double dx = w2.getX() - w1.getX();
                double dy = w2.getY() - w1.getY();
                double odleglosc = Math.sqrt(dx*dx + dy*dy);

                if (odleglosc < 1.0) continue;
                double waga = k.getWaga();
                double silaKorekty = aktualnaTemperatura*(waga - 1.0);

                double kierunekSilyX = dx/odleglosc;
                double kierunekSilyY = dy/odleglosc;

                double przesuniecieX = kierunekSilyX * silaKorekty * odleglosc;
                double przesuniecieY = kierunekSilyY * silaKorekty * odleglosc;

                w1.setX(w1.getX() + przesuniecieX);
                w1.setY(w1.getY() + przesuniecieY);
                w2.setX(w2.getX() - przesuniecieX);
                w2.setY(w2.getY() - przesuniecieY);
            }
        }
    }
}
