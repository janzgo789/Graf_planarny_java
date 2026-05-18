package proj;



import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;

/**
 * Komponent Swing odpowiedzialny za renderowanie grafu.
 */
public class PanelGrafu extends JPanel {
    private ModelGrafu model;
    private double przyblizenie = 1.0;
    private Wierzcholek aktywnyWierzcholek = null;
    private double przesuniecieX = 0.0;
    private double przesuniecieY = 0.0;
    private int ostatniaPozX;
    private int ostatniaPozY;

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
            g2.setColor(Color.BLUE);
            Ellipse2D punkt = new Ellipse2D.Double(w.getX() - 5, w.getY() - 5, 10, 10);
            g2.fill(punkt);

            g2.setColor(Color.BLACK);
            g2.drawString(w.getNazwa(), (int)w.getX() + 7, (int)w.getY() - 7);
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
                // promień wierzchołka wynosi 5, ale będzie łatwiej kliknąć
                if (odleglosc <= 10 && SwingUtilities.isLeftMouseButton(e)) {
                    aktywnyWierzcholek = w;
                    break;
                }
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (aktywnyWierzcholek != null) {
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
            aktywnyWierzcholek = null;
        }
    }
}
