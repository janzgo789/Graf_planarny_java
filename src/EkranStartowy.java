package proj.src;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Odpowiada za uruchomienie programu i wyświetlenie ekranu startowego.
 */
public class EkranStartowy extends JFrame {

    public EkranStartowy() {
        setTitle("Wizualizator Grafów - Start");
        setSize(1024, 768);
        this.setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panelGlowny = new JPanel();
        panelGlowny.setLayout(new BoxLayout(panelGlowny, BoxLayout.Y_AXIS));

        JButton przyciskWczytaj = new JButton("Wczytaj plik tekstowy z grafem");
        przyciskWczytaj.setFont(new Font("Arial", Font.BOLD, 28));
        przyciskWczytaj.setForeground(Color.WHITE);
        przyciskWczytaj.setBackground(new Color(70, 50, 230));
        przyciskWczytaj.setAlignmentX(Component.CENTER_ALIGNMENT);
        przyciskWczytaj.setFocusPainted(false);

        przyciskWczytaj.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File wybranyPlik = fc.getSelectedFile();
                try {
                    ModelGrafu nowyModel = new ModelGrafu();
                    MenadzerIO.wczytajWejscie(wybranyPlik, nowyModel);

                    WizualizatorGrafu glowneOkno = new WizualizatorGrafu(nowyModel, wybranyPlik);
                    glowneOkno.setVisible(true);
                    dispose();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Błąd wczytywania pliku: " + ex.getMessage());
                }
            }
        });

        JLabel info = new JLabel("Obsługiwany format: <Nazwa krawędzi> <ID 1. wierzchołka> " +
                "<ID 2. wierzchołka> <Waga krawędzi>", SwingConstants.CENTER);
        info.setFont(new Font("Arial", Font.PLAIN, 14));
        info.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelGlowny.add(Box.createVerticalGlue());
        panelGlowny.add(przyciskWczytaj);
        panelGlowny.add(Box.createRigidArea(new Dimension(0, 20)));
        panelGlowny.add(info);
        panelGlowny.add(Box.createVerticalGlue());

        add(panelGlowny);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EkranStartowy().setVisible(true));
    }
}
