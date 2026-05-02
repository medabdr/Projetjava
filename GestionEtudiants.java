import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class GestionEtudiants extends JFrame {
    private JTextField txtMatricule, txtNom, txtPrenom, txtAge, txtFiliere, txtMoyenne, txtRecherche;
    private JButton btnAjouter, btnModifier, btnSupprimer, btnActualiser;
    private JTable table;
    private DefaultTableModel model;
    private Connection conn;

    

    // Couleurs globales
    private final Color COULEUR_PRINCIPALE = new Color(44, 62, 80);  // Bleu
    private final Color COULEUR_TEXTE      = Color.WHITE;
    private final Color COULEUR_BUTTON     = new Color(44, 62, 80);    // Noir/Gris foncé (base boutons)

    // Couleurs hover par bouton
    private final Color COULEUR_HOVER_A  = new Color(39, 174, 96);    // Vert  (Ajouter)
    private final Color COULEUR_HOVER_M  = new Color(230, 126, 34);   // Orange (Modifier)
    private final Color COULEUR_HOVER_S  = new Color(192, 57, 43);    // Rouge  (Supprimer)
    private final Color COULEUR_HOVER_Ac = new Color(41, 128, 185);   // Violet (Actualiser)
    private final Color COULEUR_HOVER_Ch = new Color(0, 0, 0);   // Bleu   (Chercher)

    public GestionEtudiants() {
        setTitle("Gestion des Étudiants");
        setSize(800, 580);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(236, 240, 241));
        // Application logo 
        ImageIcon logo = new ImageIcon("logo.png");
        setIconImage(logo.getImage());

        // Connexion BD
        conn = ConnexionBD.getConnection();

        // ===================== TITRE CENTRÉ COLORÉ =====================
        JPanel panelTitre = new JPanel(new BorderLayout());
        panelTitre.setBackground(COULEUR_PRINCIPALE);
        panelTitre.setPreferredSize(new Dimension(800, 55));

        JLabel labelTitre = new JLabel("Gestion des Étudiants", SwingConstants.CENTER);
        labelTitre.setForeground(Color.WHITE);
        labelTitre.setFont(new Font("Arial", Font.BOLD, 22));
        panelTitre.add(labelTitre, BorderLayout.CENTER);

        // ===================== FORMULAIRE =====================
        JPanel panelForm = new JPanel(new GridLayout(6, 2, 5, 5));
        panelForm.setBorder(BorderFactory.createTitledBorder("Informations de l'étudiant"));
        panelForm.setBackground(new Color(236, 240, 241));

        panelForm.add(new JLabel(" Matricule:")); txtMatricule = new JTextField(); panelForm.add(txtMatricule);
        panelForm.add(new JLabel(" Nom:"));       txtNom       = new JTextField(); panelForm.add(txtNom);
        panelForm.add(new JLabel(" Prénom:"));    txtPrenom    = new JTextField(); panelForm.add(txtPrenom);
        panelForm.add(new JLabel(" Âge:"));       txtAge       = new JTextField(); panelForm.add(txtAge);
        panelForm.add(new JLabel(" Filière:"));   txtFiliere   = new JTextField(); panelForm.add(txtFiliere);
        panelForm.add(new JLabel(" Moyenne:"));   txtMoyenne   = new JTextField(); panelForm.add(txtMoyenne);

        // ===================== BOUTONS STYLISÉS =====================
        JPanel panelBoutons = new JPanel(new GridLayout(2, 2, 10, 10));
        panelBoutons.setBackground(new Color(236, 240, 241));
        panelBoutons.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        btnAjouter    = creerBouton("Ajouter",    COULEUR_HOVER_A);
        btnModifier   = creerBouton("Modifier",   COULEUR_HOVER_M);
        btnSupprimer  = creerBouton("Supprimer",  COULEUR_HOVER_S);
        btnActualiser = creerBouton("Actualiser", COULEUR_HOVER_Ac);

        panelBoutons.add(btnAjouter);
        panelBoutons.add(btnModifier);
        panelBoutons.add(btnSupprimer);
        panelBoutons.add(btnActualiser);

        // ===================== RECHERCHE =====================
        JPanel panelRecherche = new JPanel(new FlowLayout());
        panelRecherche.setBackground(new Color(236, 240, 241));
        panelRecherche.setBorder(BorderFactory.createTitledBorder("Recherche"));
        panelRecherche.add(new JLabel("Rechercher par Nom:"));
        txtRecherche = new JTextField(20);
        panelRecherche.add(txtRecherche);
        JButton btnRechercher = creerBouton("Chercher", COULEUR_HOVER_Ch);
        panelRecherche.add(btnRechercher);

        // ===================== HEADER PANEL =====================
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(236, 240, 241));
        headerPanel.add(panelTitre, BorderLayout.NORTH);
        headerPanel.add(panelRecherche, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // ===================== LEFT PANEL =====================
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(236, 240, 241));
        leftPanel.setPreferredSize(new Dimension(320, 0));

        JPanel innerLeft = new JPanel(new BorderLayout());
        innerLeft.setBackground(new Color(236, 240, 241));
        innerLeft.add(panelForm, BorderLayout.NORTH);
        innerLeft.add(panelBoutons, BorderLayout.CENTER);

        leftPanel.add(innerLeft, BorderLayout.NORTH);
        add(leftPanel, BorderLayout.WEST);

        // ===================== TABLEAU (version originale) =====================
        model = new DefaultTableModel(
            new String[]{"Matricule", "Nom", "Prénom", "Âge", "Filière", "Moyenne"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                remplirChamps();
            }
        });
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ===================== ÉVÉNEMENTS =====================
        btnAjouter.addActionListener(e -> ajouterEtudiant());
        btnModifier.addActionListener(e -> modifierEtudiant());
        btnSupprimer.addActionListener(e -> supprimerEtudiant());
        btnActualiser.addActionListener(e -> { txtRecherche.setText(""); chargerDonnees(""); });
        btnRechercher.addActionListener(e -> chargerDonnees(txtRecherche.getText()));

        chargerDonnees("");
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ===================== BOUTON AVEC HOVER PERSONNALISÉ =====================
    private JButton creerBouton(String texte, Color couleurHover) {
    JButton btn = new JButton(texte) {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); // 30 = rayon des coins
            super.paintComponent(g);
            g2.dispose();
        }
    };

    btn.setBackground(COULEUR_BUTTON);
    btn.setForeground(COULEUR_TEXTE);
    btn.setFont(new Font("Arial", Font.BOLD, 12));
    btn.setFocusPainted(false);
    btn.setBorderPainted(false);
    btn.setContentAreaFilled(false); // Important pour voir les coins arrondis
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    btn.setPreferredSize(new Dimension(100, 28));

    // Effet hover
    btn.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            btn.setBackground(couleurHover);
        }
        @Override
        public void mouseExited(MouseEvent e) {
            btn.setBackground(COULEUR_BUTTON);
        }
    });

    return btn;
}

    // ===================== CHARGER DONNÉES (CORRIGÉ) =====================
    private void chargerDonnees(String recherche) {
        model.setRowCount(0); // Vider le tableau
        if (conn == null) {
            JOptionPane.showMessageDialog(this, "Connexion à la base de données échouée.");
            return;
        }
        try {
            String query = recherche.trim().isEmpty()
                ? "SELECT * FROM etudiant"
                : "SELECT * FROM etudiant WHERE nom LIKE ?";

            PreparedStatement ps = conn.prepareStatement(query);
            if (!recherche.trim().isEmpty()) {
                ps.setString(1, "%" + recherche.trim() + "%");
            }

            java.util.List<Etudiant> listeEtudiants = new java.util.ArrayList<>();
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Etudiant etudiant = new Etudiant(
                    rs.getString("matricule"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getInt("age"),
                    rs.getString("filiere"),
                    rs.getDouble("moyenne")
                );
                listeEtudiants.add(etudiant);
            }

            for (Etudiant etu : listeEtudiants) {
                model.addRow(new Object[]{
                    etu.getMatricule(),
                    etu.getNom(),
                    etu.getPrenom(),
                    etu.getAge(),
                    etu.getFiliere(),
                    etu.getMoyenne()
                });
            }

            // Message si aucun résultat
            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Aucun étudiant trouvé.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur chargement : " + e.getMessage());
        }
    }

    private void ajouterEtudiant() {
        if (conn == null) return;
        if (!validerChamps()) return;
        try {
            Etudiant etudiant = new Etudiant(
                txtMatricule.getText(),
                txtNom.getText(),
                txtPrenom.getText(),
                Integer.parseInt(txtAge.getText()),
                txtFiliere.getText(),
                Double.parseDouble(txtMoyenne.getText())
            );

            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO etudiant (matricule, nom, prenom, age, filiere, moyenne) VALUES (?, ?, ?, ?, ?, ?)");
            ps.setString(1, etudiant.getMatricule());
            ps.setString(2, etudiant.getNom());
            ps.setString(3, etudiant.getPrenom());
            ps.setInt(4, etudiant.getAge());
            ps.setString(5, etudiant.getFiliere());
            ps.setDouble(6, etudiant.getMoyenne());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Étudiant ajouté avec succès!");
            chargerDonnees("");
            viderChamps();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur d'ajout : " + e.getMessage());
        }
    }

    private void modifierEtudiant() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un étudiant à modifier.");
            return;
        }
        if (conn == null) return;
        if (!validerChamps()) return;
        try {
            Etudiant etudiant = new Etudiant(
                model.getValueAt(row, 0).toString(),
                txtNom.getText(),
                txtPrenom.getText(),
                Integer.parseInt(txtAge.getText()),
                txtFiliere.getText(),
                Double.parseDouble(txtMoyenne.getText())
            );

            PreparedStatement ps = conn.prepareStatement(
                "UPDATE etudiant SET nom=?, prenom=?, age=?, filiere=?, moyenne=? WHERE matricule=?");
            ps.setString(1, etudiant.getNom());
            ps.setString(2, etudiant.getPrenom());
            ps.setInt(3, etudiant.getAge());
            ps.setString(4, etudiant.getFiliere());
            ps.setDouble(5, etudiant.getMoyenne());
            ps.setString(6, etudiant.getMatricule());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Étudiant modifié avec succès!");
            chargerDonnees(txtRecherche.getText());
            viderChamps();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur de modification : " + e.getMessage());
        }
    }

    private void supprimerEtudiant() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un étudiant à supprimer.");
            return;
        }
        if (conn == null) return;
        int confirmation = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer cet étudiant?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                Etudiant etudiant = new Etudiant();
                etudiant.setMatricule(model.getValueAt(row, 0).toString());

                PreparedStatement ps = conn.prepareStatement("DELETE FROM etudiant WHERE matricule=?");
                ps.setString(1, etudiant.getMatricule());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Étudiant supprimé!");
                chargerDonnees(txtRecherche.getText());
                viderChamps();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erreur de suppression : " + e.getMessage());
            }
        }
    }

    private void remplirChamps() {
        int row = table.getSelectedRow();
        if (row != -1) {
            txtMatricule.setText(model.getValueAt(row, 0).toString());
            txtNom.setText(model.getValueAt(row, 1).toString());
            txtPrenom.setText(model.getValueAt(row, 2).toString());
            txtAge.setText(model.getValueAt(row, 3).toString());
            txtFiliere.setText(model.getValueAt(row, 4).toString());
            txtMoyenne.setText(model.getValueAt(row, 5).toString());
        }
    }

    private void viderChamps() {
        txtMatricule.setText(""); txtNom.setText(""); txtPrenom.setText("");
        txtAge.setText(""); txtFiliere.setText(""); txtMoyenne.setText("");
        table.clearSelection();
    }

    private boolean validerChamps() {
        if (txtMatricule.getText().isEmpty() || txtNom.getText().isEmpty() ||
            txtPrenom.getText().isEmpty()    || txtAge.getText().isEmpty() ||
            txtFiliere.getText().isEmpty()   || txtMoyenne.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.");
            return false;
        }
        try {
            Integer.parseInt(txtAge.getText());
            Double.parseDouble(txtMoyenne.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "L'âge doit être un entier et la moyenne un nombre (ex: 15.5).");
            return false;
        }
        return true;
    }
}