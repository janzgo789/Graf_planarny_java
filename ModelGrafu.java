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

    /**
     * Czyści dane grafu przed wczytaniem nowego pliku.
     */
    public void wyczysc() {
        wierzcholki.clear();
        krawedzie.clear();
    }

    /**
     * Dodaje wierzchołek do modelu.
     */
    public void dodajWierzcholek(Wierzcholek w) {
        wierzcholki.put(w.getNazwa(), w);
    }

    /**
     * Dodaje krawędź do modelu.
     */
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
}
