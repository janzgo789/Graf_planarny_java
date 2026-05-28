package proj.src;


import java.io.File;
import javax.swing.*;
import java.awt.*;
import java.util.Locale;

/**
 * Wyświetla ekran główny aplikacji i obrazuje podany graf
 */
public class WizualizatorGrafu extends JFrame {
    // Pole zapamiętujące oryginalne wejście programu
    private File aktualnyPlik = null;

    // Pola ekranu wizualizującego graf
    private ModelGrafu model;
    private PanelGrafu panelWizualizacji;
    private final int szerokoscOkna = 1024;
    private final int wysokoscOkna = 768;

    // Pole zakładki "Algorytmy" panelu bocznego
    private JComboBox<String> wyborAlgorytmu;

    // Pola zakładki "Wierzchołki" panelu bocznego
    private DefaultListModel<Wierzcholek> modelListyWierzcholkow;
    private JList<Wierzcholek> listaWierzcholkow;
    private JTextField poleX;
    private JTextField poleY;

    // Pola zakładki "Krawędzie" panelu bocznego
    private DefaultListModel<Krawedz> modelListyKrawedzi;
    private JList<Krawedz> listaKrawedzi;
    private JTextField poleWagi;

    public WizualizatorGrafu(ModelGrafu wczytanyModel, File wczytanyPlik) {
        this.model = wczytanyModel;
        this.aktualnyPlik = wczytanyPlik;

        setTitle("Wizualizator Grafów Planarnych");
        setSize(szerokoscOkna, wysokoscOkna);
        this.setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        panelWizualizacji = new PanelGrafu(model);

        // Menu górne
        JMenuBar menuBar = new JMenuBar();
        JMenu menuPlik = new JMenu("Plik");
        JMenu menuWidok = new JMenu("Widok");

        // Przyciski zakładki "Plik"
        JMenuItem mItemOtworz = new JMenuItem("Otwórz plik tekstowy");
        mItemOtworz.addActionListener(e -> obsluzWczytywanie());

        JMenuItem mItemZapiszTxt = new JMenuItem("Zapisz jako plik tekstowy...");
        mItemZapiszTxt.addActionListener(e -> obsluzZapisywanie(true));

        JMenuItem mItemZapiszBin = new JMenuItem("Zapisz jako plik binarny...");
        mItemZapiszBin.addActionListener(e -> obsluzZapisywanie(false));

        // Przyciski zakładki "Widok"
        JCheckBoxMenuItem mItemPokazEtykiety = new JCheckBoxMenuItem("Ukryj etykiety wierzchołków");
        mItemPokazEtykiety.addActionListener(e -> {
            panelWizualizacji.setPokazujEtykiety(!mItemPokazEtykiety.isSelected());
            repaint();
        });

        JCheckBoxMenuItem mItemPokazWagi = new JCheckBoxMenuItem("Ukryj wagi krawędzi");
        mItemPokazWagi.addActionListener(e -> {
            panelWizualizacji.setPokazujWagi(!mItemPokazWagi.isSelected());
            repaint();
        });

        // Dodanie przycisków
        menuPlik.add(mItemOtworz);
        menuBar.add(menuPlik);
        menuPlik.add(mItemZapiszTxt);
        menuPlik.add(mItemZapiszBin);

        menuWidok.add(mItemPokazEtykiety);
        menuWidok.add(mItemPokazWagi);
        menuBar.add(menuWidok);

        setJMenuBar(menuBar);

        // Panel narzędzi "Reset widoku"
        JPanel panelNarzedzi = new JPanel();
        JButton resetWidoku = new JButton("Reset widoku");
        resetWidoku.addActionListener(e -> {
            panelWizualizacji.wysrodkujObraz();
        });
        panelNarzedzi.add(resetWidoku);

        add(panelWizualizacji, BorderLayout.CENTER);
        add(panelNarzedzi, BorderLayout.SOUTH);
        add(budujPanelBoczny(), BorderLayout.WEST);

        // Zaznacznie wierzchołka na liście bocznej po kliknięciu na wierzchołek
        panelWizualizacji.ZaznaczeniaWierzcholka(kliknietyWierzcholek -> {
            listaWierzcholkow.setSelectedValue(kliknietyWierzcholek, true);
        });

        // Zaktualizowanie współrzędnych wierzchołka na liście i w polach do edycji po upuszczeniu go
        panelWizualizacji.PuszczenieWierzcholka(() -> {
            listaWierzcholkow.repaint();

            Wierzcholek w = listaWierzcholkow.getSelectedValue();
            if (w != null) {
                poleX.setText(String.format(java.util.Locale.US, "%.2f", w.getX()));
                poleY.setText(String.format(java.util.Locale.US, "%.2f", w.getY()));
            }
        });

        String domyslnyAlgorytm = (String) wyborAlgorytmu.getSelectedItem();
        przeliczGraf(domyslnyAlgorytm);
    }

    private JPanel budujPanelBoczny() {
        // Przypięcie zakładek do prawej krawędzi panelu
        JTabbedPane zakladki = new JTabbedPane(JTabbedPane.NORTH);

        // Do resetowania podświetlenia zaznaczonej krawędzi przy zmianie zakładki
        zakladki.addChangeListener(e -> panelWizualizacji.nowaOstatniaAktywnaKrawedz(null));

        // Podpięcie zakładek
        zakladki.addTab("Wierzchołki", budujPanelWierzcholkow());
        zakladki.addTab("Krawędzie", budujPanelKrawedzi());
        zakladki.addTab("Algorytmy", budujPanelAlgorytmow());

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(zakladki, BorderLayout.CENTER);

        panel.setPreferredSize(new Dimension(300, 0));

        return panel;
    }

    private JPanel budujPanelAlgorytmow() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Wybierz algorytm:"));
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

        return panel;
    }

    private JPanel budujPanelWierzcholkow(){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Lista wierzchołków:"));
        modelListyWierzcholkow = new DefaultListModel<>();
        listaWierzcholkow = new JList<>(modelListyWierzcholkow);
        listaWierzcholkow.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        listaWierzcholkow.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && listaWierzcholkow.getSelectedValue() != null) {
                Wierzcholek w = listaWierzcholkow.getSelectedValue();
                poleX.setText(String.format(Locale.US, "%.2f", w.getX()));
                poleY.setText(String.format(Locale.US, "%.2f", w.getY()));

                // Zaznaczony wierzchołek wyświetli się też na planszy
                panelWizualizacji.nowyOstatniAktywny(w);
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
            Wierzcholek w = listaWierzcholkow.getSelectedValue();
            if (w != null) {
                try {
                    double noweX = Double.parseDouble(poleX.getText());
                    double noweY = Double.parseDouble(poleY.getText());
                    w.setX(noweX);
                    w.setY(noweY);
                    panelWizualizacji.wysrodkujObraz();
                    listaWierzcholkow.repaint();
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

    private JPanel budujPanelKrawedzi() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Lista krawędzi:"));

        modelListyKrawedzi = new DefaultListModel<>();
        listaKrawedzi = new JList<>(modelListyKrawedzi);
        listaKrawedzi.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        listaKrawedzi.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && listaKrawedzi.getSelectedValue() != null) {
                Krawedz k = listaKrawedzi.getSelectedValue();
                panelWizualizacji.nowaOstatniaAktywnaKrawedz(k);
                poleWagi.setText(String.format(Locale.US, "%.2f", k.getWaga()));
            }
        });

        JScrollPane scrollKrawedzi = new JScrollPane(listaKrawedzi);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(scrollKrawedzi);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Pole do edycji wagi
        panel.add(new JLabel("Edytuj:"));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        JPanel panelWagi = new JPanel(new GridLayout(1, 2, 5, 5));
        panelWagi.setMaximumSize(new Dimension(230, 30));
        poleWagi = new JTextField();
        panelWagi.add(new JLabel("Waga:"));
        panelWagi.add(poleWagi);
        panel.add(panelWagi);

        // Przycisk zapisywania wagi
        JButton przyciskZapiszWage = new JButton("Zapisz wagę");
        przyciskZapiszWage.setAlignmentX(Component.CENTER_ALIGNMENT);
        przyciskZapiszWage.addActionListener(e -> {
            Krawedz wybrana = listaKrawedzi.getSelectedValue();
            if (wybrana != null) {
                try {
                    double nowaWaga = Double.parseDouble(poleWagi.getText());

                    if (nowaWaga <= 0) {
                        JOptionPane.showMessageDialog(this,
                                "Waga krawędzi nie może być ujemna! Podaj wartość większą lub równą 0.",
                                "Błąd wartości",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    wybrana.setWaga(nowaWaga);
                    panelWizualizacji.korygujWspolrzedneWagami();
                    panelWizualizacji.wysrodkujObraz();
                    listaKrawedzi.repaint(); // Odświeża napis w liście (odpala toString)
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Wpisz poprawną liczbę! (liczba dodatnia, kropka zamiast przecinka)");
                }
            }
        });
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(przyciskZapiszWage);

        return panel;
    }

    private void zaladujWierzcholkiDoListy() {
        modelListyWierzcholkow.clear();
        java.util.List<Wierzcholek> posortowaneWierzcholki = new java.util.ArrayList<>(model.getWierzcholki().values());
        posortowaneWierzcholki.sort((w1, w2) -> {
            try {
                int id1 = Integer.parseInt(w1.getNazwa());
                int id2 = Integer.parseInt(w2.getNazwa());
                return Integer.compare(id1, id2);
            } catch (NumberFormatException ex) {
               return w1.getNazwa().compareTo(w2.getNazwa());
            }
        });

        for (Wierzcholek w : posortowaneWierzcholki) {
            modelListyWierzcholkow.addElement(w);
        }
    }

    private void zaladujKrawedzieDoListy() {
        modelListyKrawedzi.clear();
        for (Krawedz k : model.getKrawedzie()) {
            modelListyKrawedzi.addElement(k);
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
                panelWizualizacji.korygujWspolrzedneWagami();
                przeliczGraf("fruchterman");
                panelWizualizacji.wysrodkujObraz();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Błąd: " + ex.getMessage());
            }
        }
    }

    private void obsluzZapisywanie(boolean formatTxt) {
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File plikDocelowy = fc.getSelectedFile();
                if (formatTxt) {
                    MenadzerIO.zapiszDoPlikuTxt(plikDocelowy, model);
                } else {
                    MenadzerIO.zapiszDoPlikuBin(plikDocelowy, model);
                }
                JOptionPane.showMessageDialog(this, "Graf zapisany pomyślnie!", "Sukces", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Błąd podczas zapisywania: " + ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void przeliczGraf(String wybranyAlgorytm) {
        if (aktualnyPlik == null) return;
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

            // Zmiana współrzędnych ze względu na wagi
            panelWizualizacji.korygujWspolrzedneWagami();

            panelWizualizacji.wysrodkujObraz();
            zaladujWierzcholkiDoListy();
            zaladujKrawedzieDoListy();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Błąd podczas pracy silnika obliczeniowego: " + ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }
}
