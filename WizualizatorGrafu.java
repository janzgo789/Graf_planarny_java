package proj;



import javax.swing.*;
import java.awt.*;

public class WizualizatorGrafu extends JFrame {
    private ModelGrafu model = new ModelGrafu();
    private PanelGrafu panelWizualizacji;

    public WizualizatorGrafu() {
        setTitle("Wizualizator Grafów Planarnych");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        panelWizualizacji = new PanelGrafu(model);

        // Menu górne
        JMenuBar menuBar = new JMenuBar();
        JMenu menuPlik = new JMenu("Plik");
        JMenuItem mItemOtworz = new JMenuItem("Otwórz tekstowy...");

        mItemOtworz.addActionListener(e -> obsluzWczytywanie());

        menuPlik.add(mItemOtworz);
        menuBar.add(menuPlik);
        setJMenuBar(menuBar);

        // Panel narzędzi (Zoom)
        JPanel panelNarzedzi = new JPanel();
        JSlider suwakZoom = new JSlider(10, 500, 100);
        suwakZoom.addChangeListener(e -> {
            panelWizualizacji.ustawPrzyblizenie(suwakZoom.getValue() / 100.0);
        });

        panelNarzedzi.add(new JLabel("Powiększenie:"));
        panelNarzedzi.add(suwakZoom);

        add(panelWizualizacji, BorderLayout.CENTER);
        add(panelNarzedzi, BorderLayout.SOUTH);
    }

    private void obsluzWczytywanie() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                MenadzerIO.wczytajTekstowy(fc.getSelectedFile(), model);
                panelWizualizacji.repaint();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Błąd: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WizualizatorGrafu().setVisible(true));
    }
}
