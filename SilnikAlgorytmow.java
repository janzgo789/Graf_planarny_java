package proj;

import java.io.IOException;
import java.io.File;
import java.io.InterruptedIOException;

public class SilnikAlgorytmow {
    /**
     * Metoda wywołuje zewnętrzny program w C.
     *
     * @param plik     Oryginalny plik z krawędziami (np. graf.txt)
     * @param algortym Nazwa algorytmu do przekazania w parametrze (np. "Fruchterman-Reingold")
     * @return Plik wynikowy wygenerowany przez program w C
     */
    public static File uruchomAlgorytm(File plik, String algortym) throws IOException, InterruptedException {
        // Sprawdzenie na jakim systemie aktualnie działa Java
        String systemOperacyjny = System.getProperty("os.name").toLowerCase();

        // Wybór nazwy pliku w zależności od systemu
        String nazwaSilnika;
        if (systemOperacyjny.contains("win")) {
            nazwaSilnika = "silnik.exe"; // Windows
        } else {
            nazwaSilnika = "./silnik";   // Linux
        }

        String plikWynikowy = "temp.txt";

        // Przygotowanie procesu
        ProcessBuilder konstruktorProcesu = new ProcessBuilder(
                nazwaSilnika,
                "-i", plik.getAbsolutePath(),
                "-o", plikWynikowy,
                "-a", algortym
        );

        // logger silnika (pozostawione do debugowania)
        konstruktorProcesu.inheritIO();

        Process silnik = null;
        // Uruchomienie silnika
        try {
            silnik = konstruktorProcesu.start();
            int kodPowrotu = silnik.waitFor();
            if (kodPowrotu != 0) {
                System.err.println("Silnik obliczeniowy zwrócił błąd. Kod wyjścia: " + kodPowrotu);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(silnik != null) {
                silnik.destroy();
            }
        }

        return new File(plikWynikowy);
    }
}
