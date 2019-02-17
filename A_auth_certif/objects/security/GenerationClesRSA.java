package A_auth_certif.objects.security;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

/**
 * Classe permettant de générer une paire de clés privée/publique et de les
 * sauvegarder dans des fichiers.
 * Les noms des fichiers de sortie doivent être spécifiés en ligne de commande.
 * Par exemple : java GenerationClesRSA prive.bin publique.bin
 * La clé privée est sauvée dans 'prive.bin' et la cle publique est sauvée
 * dans 'publique.bin'.
 * @author Cyril Rabat
 * @version 23/10/2017
 */
public class GenerationClesRSA {

    /**
     * Méthode principale.
     * @param args [0] nom du fichier dans lequel sauvegarder la clé privée
     *             [1] nom du fichier dans lequel sauvegarder la clé publique
     */
    public static void main(String[] args) {
        // Vérification des arguments
        if(args.length != 2) {
            System.err.println("Utilisation :");
            System.err.println("  java GenerationClesRSA privee publique");
            System.err.println("    où :");
            System.err.println("      - privee   : nom du fichier qui contiendra la clé privée");
            System.err.println("      - publique : nom du fichier qui contiendra la clé publique");
            System.exit(-1);
        }

        // Création d'un générateur RSA
        KeyPairGenerator generateurCles = null;
        try {
            generateurCles = KeyPairGenerator.getInstance("RSA");
            generateurCles.initialize(2048);
        } catch(NoSuchAlgorithmException e) {
            System.err.println("Erreur lors de l'initialisation du générateur de clés : " + e);
            System.exit(-1);
        }

        // Génération de la paire de clés
        KeyPair paireCles = generateurCles.generateKeyPair();

        // Sauvegarde de la clé privée
        GestionClesRSA.sauvegardeClePrivee(paireCles.getPrivate(), args[0]);

        // Sauvegarde de la clé publique
        GestionClesRSA.sauvegardeClePublique(paireCles.getPublic(), args[1]);

        System.out.println("Cles sauvegardees.");
    }

}