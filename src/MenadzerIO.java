package proj.src;


import java.io.*;
import java.util.*;

/**
 * Klasa narzędziowa do importu danych z formatu tekstowego i binarnego.
 */
public class MenadzerIO {

    //Metoda czyta plik wejściowy i uzupełnia pozycje w strukturach wierzchołków i krawędzi.
    public static void wczytajWejscie(File plik, ModelGrafu model) throws IOException {
        model.wyczysc();
        try (BufferedReader br = new BufferedReader(new FileReader(plik))) {
            String linia;
            while ((linia = br.readLine()) != null) {
                String[] czesci = linia.trim().split("\\s+");
                if (czesci.length == 4) {
                    String nazwa = czesci[0];
                    String id_v1 = czesci[1];
                    String id_v2 = czesci[2];
                    double waga = Double.parseDouble(czesci[3]);

                    Map<String, Wierzcholek> mapaWierzcholkow = model.getWierzcholki();

                    Wierzcholek v1 = mapaWierzcholkow.get(id_v1);
                    if (v1 == null) {
                        v1 = new Wierzcholek(id_v1);
                        model.dodajWierzcholek(v1);
                    }

                    Wierzcholek v2 = mapaWierzcholkow.get(id_v2);
                    if (v2 == null) {
                        v2 = new Wierzcholek(id_v2);
                        model.dodajWierzcholek(v2);
                    }

                    model.dodajKrawedz(new Krawedz(nazwa, v1, v2, waga));
                }
            }
        }
    }

    /**
     * Metoda czyta plik wynikowy z C i aktualizuje pozycje istniejących wierzchołków.
     */
    public static void wczytajWspolrzedne(File plik, ModelGrafu model) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(plik))) {
            String linia;
            while ((linia = br.readLine()) != null) {
                String[] czesci = linia.trim().split("\\s+");
                if (czesci.length == 3) {
                    String nazwa = czesci[0];
                    double x = Double.parseDouble(czesci[1]);
                    double y = Double.parseDouble(czesci[2]);

                    Map<String, Wierzcholek> mapaWierzcholkow = model.getWierzcholki();

                    Wierzcholek w = mapaWierzcholkow.get(nazwa);
                    if(w != null) {
                        w.setX(x);
                        w.setY(y);
                    }
                }
            }
        }
    }

    public static void zapiszDoPlikuTxt(File plik, ModelGrafu model) throws Exception {
        // try-with-resources, żeby plik sam się bezpiecznie zamknął
        try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter(plik))) {
            // Liczba wierzchołków
            pw.printf("%x\n", model.getWierzcholki().size());

            // Format: <id_wierzcholka> <współrzędna X> <współrzędna Y>
            for (Wierzcholek w : model.getWierzcholki().values()) {
                pw.printf("%s %f %f\n", w.getNazwa(), w.getX(), w.getY());
            }
        }
    }

    public static void zapiszDoPlikuBin(File plik, ModelGrafu model) {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(plik))) {
            // Liczba wierzchołków
            dos.writeInt(model.getWierzcholki().size());

            // Format: <id_wierzcholka> <współrzędna X> <współrzędna Y>
            for (Map.Entry<String, Wierzcholek> entry : model.getWierzcholki().entrySet()) {
                dos.writeUTF(entry.getKey());            // ID wierzchołka
                dos.writeDouble(entry.getValue().getX()); // współrzędna X
                dos.writeDouble(entry.getValue().getY()); // współrzędna Y
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
