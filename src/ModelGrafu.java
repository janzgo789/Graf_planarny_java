package proj.src;



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
}
