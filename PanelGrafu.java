package proj;



import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Komponent Swing odpowiedzialny za renderowanie grafu.
 */
public class PanelGrafu extends JPanel {
    private ModelGrafu model;

    // Pola do ustawień "kamery"
    private double przyblizenie = 1.0;
    private double przesuniecieX = 0.0;
    private double przesuniecieY = 0.0;
    private final int szerokoscPanelu = 724;
    private final int wysokoscPanelu = 768;

    // Pola do śledzenia zaznaczanych wierzchołków
    private Wierzcholek aktywnyWierzcholek = null;
    private Wierzcholek ostatniAktywnyWierzcholek = null;
    private int ostatniaPozX;
    private int ostatniaPozY;

    // Pole do śledzenia zaznaczonej krawędzi
    private Krawedz ostatniaAktywnaKrawedz = null;
    // Pola do komunikacji z panelem bocznym

    private Consumer<Wierzcholek> akcjaZaznaczeniaWierzcholkow = null;
    private Runnable akcjaPuszczeniaWierzcholkow = null;
    // Pola do zmiany widoku

    private boolean pokazujEtykiety = true;
    private boolean pokazujWagi = true;
    public PanelGrafu(ModelGrafu model) {
        this.model = model;
        setBackground(Color.WHITE);

        ObslugaMyszy obslugiwacz = new ObslugaMyszy();
        addMouseListener(obslugiwacz);
        addMouseMotionListener(obslugiwacz);
        addMouseWheelListener(e -> {
            if (e.getWheelRotation() < 0) {
                przyblizenie *= 1.1; // zoom o 10%
            } else {
                przyblizenie /= 1.1;
            }
            repaint();
        });
    }

    // Metody do komunikacji z panelem bocznym
    public void ZaznaczeniaWierzcholka(Consumer<Wierzcholek> akcja) {
        this.akcjaZaznaczeniaWierzcholkow = akcja;
    }
    public void PuszczenieWierzcholka(Runnable akcja) {
        this.akcjaPuszczeniaWierzcholkow = akcja;
    }
    public void nowyOstatniAktywny(Wierzcholek w) {
        this.ostatniAktywnyWierzcholek = w;
        repaint();
    }

    public void nowaOstatniaAktywnaKrawedz(Krawedz ostatniaAktywnaKrawedz) {
        this.ostatniaAktywnaKrawedz = ostatniaAktywnaKrawedz;
        repaint();
    }

    public void ustawPrzyblizenie(double wartosc) {
        this.przyblizenie = wartosc;
        repaint();
    }

    public void setPokazujEtykiety(boolean pokazuj) { this.pokazujEtykiety = pokazuj; }
    public void setPokazujWagi(boolean pokazuj) { this.pokazujWagi = pokazuj; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Włączenie antyaliasingu (wygładzanie krawędzi)
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Przesunięcie i skalowanie kamery
        g2.translate(przesuniecieX, przesuniecieY);
        g2.scale(przyblizenie, przyblizenie);

        // 1. Rysowanie krawędzi
        g2.setFont(new Font("Arial", Font.BOLD, (int) (16/przyblizenie)));
        double srodekDlugosciX;
        double srodekDlugosciY;
        for (Krawedz k : model.getKrawedzie()) {
            g2.setColor(Color.BLACK);
            Line2D linia = new Line2D.Double(
                    k.getV1().getX(), k.getV1().getY(),
                    k.getV2().getX(), k.getV2().getY()
            );
            g2.draw(linia);

            if (pokazujWagi) {
                srodekDlugosciX = (k.getV2().getX() + k.getV1().getX()) / 2;
                srodekDlugosciY = (k.getV2().getY() + k.getV1().getY()) / 2;
                g2.setColor(Color.GRAY);
                g2.drawString(String.valueOf(k.getWaga()), (int) srodekDlugosciX + 10, (int) srodekDlugosciY - 10);
            }
        }

        if (ostatniaAktywnaKrawedz != null) {
            g2.setColor(Color.BLUE);
            Line2D zaznaczonaKrawedz = new Line2D.Double(
                    ostatniaAktywnaKrawedz.getV1().getX(), ostatniaAktywnaKrawedz.getV1().getY(),
                    ostatniaAktywnaKrawedz.getV2().getX(), ostatniaAktywnaKrawedz.getV2().getY()
            );
            g2.draw(zaznaczonaKrawedz);
        }

        // 2. Rysowanie wierzchołków
        for (Wierzcholek w : model.getWierzcholki().values()) {
            g2.setColor(new Color(80, 90, 255));
            Ellipse2D punktZewn = new Ellipse2D.Double(w.getX() - 10, w.getY() - 10, 20, 20);
            g2.fill(punktZewn);
            g2.setColor(new Color(120, 140, 255));
            Ellipse2D punktWewn = new Ellipse2D.Double(w.getX() - 8, w.getY() - 8, 16, 16);
            g2.fill(punktWewn);

            if (pokazujEtykiety) {
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Arial", Font.BOLD, (int) (16/przyblizenie)));
                g2.drawString(w.getNazwa(), (int) w.getX() + 14, (int) w.getY() - 14);
            }
        }

        if (ostatniAktywnyWierzcholek != null) {
            g2.setColor(new Color(60, 70, 255));
            Ellipse2D ostatniPunkt = new Ellipse2D.Double(ostatniAktywnyWierzcholek.getX() - 14, ostatniAktywnyWierzcholek.getY() - 14, 28, 28);
            g2.fill(ostatniPunkt);
        }
    }

    public void wysrodkujObraz() {
        Map<String, Wierzcholek> mapaWierzcholkow = model.getWierzcholki();
        double maxX = -Double.MAX_VALUE;
        double minX = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        for (Wierzcholek w : mapaWierzcholkow.values()) {
            if (w.getX() > maxX) { maxX = w.getX(); }
            if (w.getX() < minX) { minX = w.getX(); }

            if (w.getY() > maxY) { maxY = w.getY(); }
            if (w.getY() < minY) { minY = w.getY(); }
        }

        double szerokoscGrafu = maxX - minX;
        double wysokoscGrafu = maxY - minY;

        if (szerokoscGrafu == 0) { szerokoscGrafu = 0.01; }
        if (wysokoscGrafu == 0) { wysokoscGrafu = 0.01; }

        double skalaX = (szerokoscPanelu / szerokoscGrafu)*0.9;
        double skalaY = (wysokoscPanelu / wysokoscGrafu)*0.8;
        przyblizenie = Math.min(skalaX, skalaY);

        double srodekGrafuX = (maxX + minX) / 2;
        double srodekGrafuY = (maxY + minY) / 2;
        przesuniecieX = (szerokoscPanelu / 2) - (srodekGrafuX * przyblizenie) - 10;
        przesuniecieY = (wysokoscPanelu / 2) - (srodekGrafuY * przyblizenie) - 40;

        repaint();
    }

    private class ObslugaMyszy extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            ostatniaPozX = e.getX();
            ostatniaPozY = e.getY();
            for (Wierzcholek w : model.getWierzcholki().values()) {
                double myszX = (e.getX() - przesuniecieX) / przyblizenie;
                double myszY = (e.getY() - przesuniecieY) / przyblizenie;
                double odleglosc = Math.sqrt((w.getX() - myszX)*(w.getX() - myszX)
                        + (w.getY() - myszY)*(w.getY() - myszY));
                // promień wierzchołka wynosi 10, ale będzie łatwiej kliknąć
                if (odleglosc <= 13/przyblizenie && SwingUtilities.isLeftMouseButton(e)) {
                    aktywnyWierzcholek = w;
                    ostatniAktywnyWierzcholek = aktywnyWierzcholek;

                    // Przesłanie wierzchołka do panelu bocznego
                    if (akcjaZaznaczeniaWierzcholkow != null) {
                        akcjaZaznaczeniaWierzcholkow.accept(w);
                    }
                    break;
                }
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (aktywnyWierzcholek != null && ostatniAktywnyWierzcholek != null) {
                double pozX = (e.getX() - przesuniecieX) / przyblizenie;
                double pozY = (e.getY() - przesuniecieY) / przyblizenie;
                aktywnyWierzcholek.setX(pozX);
                aktywnyWierzcholek.setY(pozY);
            } else if (!SwingUtilities.isLeftMouseButton(e)){
                double dx = e.getX() - ostatniaPozX;
                double dy = e.getY() - ostatniaPozY;
                przesuniecieX += dx;
                przesuniecieY += dy;

                ostatniaPozX = e.getX();
                ostatniaPozY = e.getY();
            }
            repaint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            // tylko w momencie gdy zaznaczyliśmy jakiś wierzchołek lewym przyciskiem myszy
            if (SwingUtilities.isLeftMouseButton(e)) {
                if ( aktywnyWierzcholek != null && akcjaPuszczeniaWierzcholkow != null) {
                    akcjaPuszczeniaWierzcholkow.run();
                }
                wysrodkujObraz();
            }
            aktywnyWierzcholek = null;
        }
    }
}
