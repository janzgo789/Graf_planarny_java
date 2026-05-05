package proj;


import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Klasa narzędziowa do importu danych z formatu tekstowego i binarnego.
 */
public class MenadzerIO {

    public static void wczytajTekstowy(File plik, ModelGrafu model) throws IOException {
        model.wyczysc();
        try (BufferedReader br = new BufferedReader(new FileReader(plik))) {
            String linia;
            while ((linia = br.readLine()) != null) {
                String[] czesci = linia.trim().split("\\s+");
                if (czesci.length >= 3) {
                    String nazwa = czesci[0];
                    double x = Double.parseDouble(czesci[1]);
                    double y = Double.parseDouble(czesci[2]);
                    model.dodajWierzcholek(new Wierzcholek(nazwa, x, y));
                }
            }
        }
    }

    public static void wczytajBinarny(File plik, ModelGrafu model) throws IOException {
        model.wyczysc();
        try (DataInputStream dis = new DataInputStream(new FileInputStream(plik))) {
            while (dis.available() > 0) {
                int dlugoscNazwy = dis.readInt();
                byte[] bajtyNazwy = new byte[dlugoscNazwy];
                dis.readFully(bajtyNazwy);
                String nazwa = new String(bajtyNazwy, StandardCharsets.UTF_8);
                double x = dis.readDouble();
                double y = dis.readDouble();
                model.dodajWierzcholek(new Wierzcholek(nazwa, x, y));
            }
        }
    }
}
