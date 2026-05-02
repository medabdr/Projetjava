CREATE DATABASE IF NOT EXISTS universite;
USE universite;

CREATE TABLE IF NOT EXISTS etudiant (
    matricule VARCHAR(20) PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    age INT,
    filiere VARCHAR(50),
    moyenne DOUBLE
);

-- Données de test (optionnelles)
INSERT INTO etudiant (matricule, nom, prenom, age, filiere, moyenne) VALUES 
('I21023', 'Mohamed', 'Ahmed', 20, 'IG', 15.5),
('I21028', 'Mariem', 'Jafar', 22, 'BA', 16.2),
('I21035', 'Mohamed', 'Salah', 21, 'DI', 14.0);
