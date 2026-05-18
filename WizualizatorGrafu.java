package proj;


import java.io.File;
import javax.swing.*;
import java.awt.*;

public class WizualizatorGrafu extends JFrame {
    private ModelGrafu model;
    private PanelGrafu panelWizualizacji;
    private File aktualnyPlik = null;
    private DefaultListModel<String> modelListyWierzcholkow;
    private JList<String> listaWierzcholkow;
    private JTextField poleX;
    private JTextField poleY;
    private JComboBox<String> wyborAlgorytmu;

    public WizualizatorGrafu(ModelGrafu wczytanyModel, File wczytanyPlik) {
        this.model = wczytanyModel;
        this.aktualnyPlik = wczytanyPlik;

        setTitle("Wizualizator Grafów Planarnych");
        setSize(1024, 768);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        panelWizualizacji = new PanelGrafu(model);

        // Menu górne
        JMenuBar menuBar = new JMenuBar();
        JMenu menuPlik = new JMenu("Plik");

        JMenuItem mItemOtworz = new JMenuItem("Otwórz plik tekstowy");
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
        add(budujPanelBoczny(), BorderLayout.WEST);

        String domyslnyAlgorytm = (String) wyborAlgorytmu.getSelectedItem();
        przeliczGraf(domyslnyAlgorytm);
    }

    private JPanel budujPanelBoczny() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setPreferredSize(new Dimension(250, 0));

        // Sekcja wyboru algorytmu
        panel.add(new JLabel("Wybierz algorytm"));
        wyborAlgorytmu = new JComboBox<>(new String[]{"Fruchterman-Reingold", "Kamada-Kawai"});
        wyborAlgorytmu.setMaximumSize(new Dimension(230, 30));
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(wyborAlgorytmu);

        JButton przyciskUruchom = new JButton("Uruchom algorytm");
        przyciskUruchom.setAlignmentX(Component.CENTER_ALIGNMENT);
        przyciskUruchom.addActionListener(e -> przeliczGraf((String) wyborAlgorytmu.getSelectedItem()));
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(przyciskUruchom);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Sekcja listy wierzcholkow
        panel.add(new JLabel("Wierzchołki:"));
        modelListyWierzcholkow = new DefaultListModel<>();
        listaWierzcholkow = new JList<>(modelListyWierzcholkow);
        listaWierzcholkow.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        listaWierzcholkow.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && listaWierzcholkow.getSelectedValue() != null) {
                String nazwa = listaWierzcholkow.getSelectedValue();
                Wierzcholek w = model.getWierzcholki().get(nazwa);
                if (w != null) {
                    poleX.setText(String.format(java.util.Locale.US, "%.2f", w.getX()));
                    poleY.setText(String.format(java.util.Locale.US, "%.2f", w.getY()));
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(listaWierzcholkow);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(scrollPane);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Sekcja edycji współrzędnych
        panel.add(new JLabel("Edytuj:"));
        JPanel panelEdycji = new JPanel(new GridLayout(2, 2, 5, 5));
        panelEdycji.setMaximumSize(new Dimension(230, 60));;

        poleX = new JTextField();
        poleY = new JTextField();
        panelEdycji.add(new JLabel("X:"));
        panelEdycji.add(poleX);
        panelEdycji.add(new JLabel("Y:"));
        panelEdycji.add(poleY);

        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(panelEdycji);

        JButton przyciskZapisz = new JButton("Zapisz współrzędne");
        przyciskZapisz.setAlignmentX(Component.CENTER_ALIGNMENT);
        przyciskZapisz.addActionListener(e -> {
            String nazwa = listaWierzcholkow.getSelectedValue();
            if (nazwa != null) {
                try {
                    double noweX = Double.parseDouble(poleX.getText());
                    double noweY = Double.parseDouble(poleY.getText());
                    Wierzcholek w = model.getWierzcholki().get(nazwa);
                    w.setX(noweX);
                    w.setY(noweY);
                    panelWizualizacji.repaint();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Wpisz poprawną liczbę! (kropka zamiast przecinka");
                }
            }
        });
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(przyciskZapisz);

        return panel;
    }

    private void zaladujWierzcholkiDoListy() {
        modelListyWierzcholkow.clear();
        for (String nazwa : model.getWierzcholki().keySet()) {
            modelListyWierzcholkow.addElement(nazwa);
        }
    }

    private void obsluzWczytywanie() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                // Zapamiętywanie wybranego pliku
                aktualnyPlik = fc.getSelectedFile();

                // Wczytywanie
                MenadzerIO.wczytajWejscie(fc.getSelectedFile(), model);
                przeliczGraf("fruchterman");
                panelWizualizacji.repaint();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Błąd: " + ex.getMessage());
            }
        }
    }

    public void przeliczGraf(String wybranyAlgorytm) {
        if (aktualnyPlik == null) return; // Zabezpieczenie
        String algorytm;
        if (wybranyAlgorytm.equals("Fruchterman-Reingold")) {
            algorytm = "fruchterman";
        } else {
            algorytm = "kamada";
        }
        try {
            System.out.println("Przeliczanie grafu algorytmem: " + wybranyAlgorytm + "...");

            File plikWynikowy = SilnikAlgorytmow.uruchomAlgorytm(aktualnyPlik, algorytm);
            MenadzerIO.wczytajWspolrzedne(plikWynikowy, model);

            panelWizualizacji.repaint();
            zaladujWierzcholkiDoListy();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Błąd podczas pracy silnika obliczeniowego: " + ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }
}
