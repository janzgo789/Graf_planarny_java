package proj;



import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.util.function.Consumer;

/**
 * Komponent Swing odpowiedzialny za renderowanie grafu.
 */
public class PanelGrafu extends JPanel {
    private ModelGrafu model;
    private double przyblizenie = 1.0;
    private Wierzcholek aktywnyWierzcholek = null;
    private Wierzcholek ostatniAktywny = null;
    private double przesuniecieX = 0.0;
    private double przesuniecieY = 0.0;
    private int ostatniaPozX;
    private int ostatniaPozY;

    private Consumer<Wierzcholek> akcjaZaznaczenia = null;
    private Runnable akcjaPuszczenia = null;

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
        this.akcjaZaznaczenia = akcja;
    }

    public void PuszczenieWierzcholka(Runnable akcja) {
        this.akcjaPuszczenia = akcja;
    }

    public void nowyOstatniAktywny(Wierzcholek w) {
        this.ostatniAktywny = w;
        repaint();
    }

    public void ustawPrzyblizenie(double wartosc) {
        this.przyblizenie = wartosc;
        repaint();
    }

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
        g2.setColor(Color.LIGHT_GRAY);
        for (Krawedz k : model.getKrawedzie()) {
            Line2D linia = new Line2D.Double(
                    k.getV1().getX(), k.getV1().getY(),
                    k.getV2().getX(), k.getV2().getY()
            );
            g2.draw(linia);
        }

        // 2. Rysowanie wierzchołków
        for (Wierzcholek w : model.getWierzcholki().values()) {
            g2.setColor(new Color(80, 90, 255));
            Ellipse2D punktZewn = new Ellipse2D.Double(w.getX() - 10, w.getY() - 10, 20, 20);
            g2.fill(punktZewn);
            g2.setColor(new Color(120, 140, 255));
            Ellipse2D punktWewn = new Ellipse2D.Double(w.getX() - 8, w.getY() - 8, 16, 16);
            g2.fill(punktWewn);

            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.BOLD, 18));
            g2.drawString(w.getNazwa(), (int)w.getX() + 14, (int)w.getY() - 14);
        }

        if (ostatniAktywny != null) {
            g2.setColor(new Color(60, 70, 255));
            Ellipse2D ostatniPunkt = new Ellipse2D.Double(ostatniAktywny.getX() - 14, ostatniAktywny.getY() - 14, 28, 28);
            g2.fill(ostatniPunkt);
        }
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
                if (odleglosc <= 13 && SwingUtilities.isLeftMouseButton(e)) {
                    aktywnyWierzcholek = w;
                    ostatniAktywny = aktywnyWierzcholek;

                    // Przesłanie wierzchołka do panelu bocznego
                    if (akcjaZaznaczenia != null) {
                        akcjaZaznaczenia.accept(w);
                    }
                    break;
                }
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (aktywnyWierzcholek != null && ostatniAktywny != null) {
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
            if (SwingUtilities.isLeftMouseButton(e) && aktywnyWierzcholek != null && akcjaPuszczenia != null) {
                akcjaPuszczenia.run();
            }

            aktywnyWierzcholek = null;
            repaint();
        }
    }
}
