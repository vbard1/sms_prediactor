package src.model_builder;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ProgressWindow extends JFrame {
    private List<JProgressBar> progressBars;

    public ProgressWindow(String title, String[] progressBarTitles) {
        super(title);
        progressBars = new ArrayList<>();

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(progressBarTitles.length, 2, 10, 10)); // Ajout de marges de 10 pixels entre les composants

        // Création des barres de progression avec leurs titres
        for (String progressBarTitle : progressBarTitles) {
            JLabel label = new JLabel(progressBarTitle);
            JProgressBar progressBar = new JProgressBar(0, 100);
            progressBar.setValue(0);
            progressBar.setStringPainted(true);

            panel.add(label);
            panel.add(progressBar);

            progressBars.add(progressBar);
        }

        // Ajout de marges à la fenêtre
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Marges de 10 pixels de chaque côté

        setContentPane(panel);
        pack();
        setLocationRelativeTo(null); // Centrer la fenêtre sur l'écran
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    // Méthode pour définir la valeur d'une barre de progression spécifique
    public void setProgress(int progressBarIndex, int value) {
        if (progressBarIndex >= 0 && progressBarIndex < progressBars.size()) {
            progressBars.get(progressBarIndex).setValue(value);
        }
    }

    // Méthode pour incrémenter la valeur d'une barre de progression spécifique
    public void incrementProgress(int progressBarIndex, int incrementValue) {
        if (progressBarIndex >= 0 && progressBarIndex < progressBars.size()) {
            JProgressBar progressBar = progressBars.get(progressBarIndex);
            int value = progressBar.getValue() + incrementValue;
            progressBar.setValue(value);
        }
    }
}