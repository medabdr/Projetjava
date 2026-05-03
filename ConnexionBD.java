import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JOptionPane;

public class ConnexionBD {
    
    // URL, utilisateur et mot de passe de la base de données
    private static final String URL = "jdbc:mysql://localhost:3306/universite?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Par défaut vide sur WAMP/XAMPP

    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Chargement explicite du driver MySQL (utile pour certaines configurations)
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Établissement de la connexion
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, 
                "Le driver MySQL (mysql-connector-java.jar) est introuvable.\nAssurez-vous qu'il est bien ajouté dans le dossier 'lib' de votre projet.", 
                "Erreur Driver Introuvable", 
                JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Impossible de se connecter à la base de données MySQL.\n\n" +
                "1. Vérifiez que votre serveur (XAMPP/WAMP) est allumé.\n" +
                "2. Vérifiez que vous avez bien importé le fichier SQL (gestion_etudiants.sql) dans phpMyAdmin.\n\n" +
                "Détail de l'erreur : " + e.getMessage(), 
                "Erreur de Connexion", 
                JOptionPane.ERROR_MESSAGE);
        }
        return conn;
    }
}