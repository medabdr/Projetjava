import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // Appliquer un style natif (moderne) si disponible
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Impossible d'appliquer le thème du système : " + e.getMessage());
        }
        
        // Lancer l'interface graphique de l'application
        SwingUtilities.invokeLater(() -> {
            new GestionEtudiants();
        });
    }
}
