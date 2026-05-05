package proj;



import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * Komponent Swing odpowiedzialny za renderowanie grafu.
 */
public class PanelGrafu extends JPanel {
    private ModelGrafu model;
    private double przyblizenie = 1.0;

    public PanelGrafu(ModelGrafu model) {
        this.model = model;
        setBackground(Color.WHITE);
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

        // Skalowanie widoku
        AffineTransform at = AffineTransform.getScaleInstance(przyblizenie, przyblizenie);
        g2.transform(at);

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
}
