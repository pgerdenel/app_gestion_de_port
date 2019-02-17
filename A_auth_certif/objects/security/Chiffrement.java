package A_auth_certif.objects.security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

/**
 * Classe permettant de chiffrer un message à l'aide d'une clé publique.
 * Le message chiffré est placé dans un fichier.
 * @author Cyril Rabat
 * @version 23/10/2017
 */
public class Chiffrement {

    /**
     * Méthode principale.
     * @param args 0 nom du fichier dans lequel se trouve la clé publique
     *             1 message à chiffrer
     *             2 nom du fichier dans lequel sauvegarder le message chiffré
     */
    public static void main(String[] args) {
        // Vérification des arguments
        if(args.length != 3) {
            System.err.println("Utilisation :");
            System.err.println("  java Chiffrement clePublique message output");
            System.err.println("    où :");
            System.err.println("      - clePublique : nom du fichier qui contient la clé publique");
            System.err.println("      - message     : message à chiffrer");
            System.err.println("      - output      : fichier contenant le message chiffré");
            System.exit(-1);        
        }

        System.out.println("Message à chiffrer : " + args[1]);

        // Recuperation de la cle publique
        PublicKey clePublique = GestionClesRSA.lectureClePublique(args[0]);

        // Chiffrement du message
        byte[] bytes = null;
        try {
            Cipher chiffreur = Cipher.getInstance("RSA");
            chiffreur.init(Cipher.ENCRYPT_MODE, clePublique);
            bytes = chiffreur.doFinal(args[1].getBytes());
        } catch(NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchPaddingException e) {
            System.err.println("Erreur lors du chiffrement : " + e);
            System.exit(-1);
        }

        // Sauvegarde du message chiffré
        try {
            FileOutputStream fichier = new FileOutputStream(args[2]);
            fichier.write(bytes);
            fichier.close();    
        } catch(IOException e) {
            System.err.println("Erreur lors de la sauvegarde du message chiffré : " + e);
            System.exit(-1);
        }  
        System.out.println("Message code enregistré dans '" + args[2] + "'");
    }

}