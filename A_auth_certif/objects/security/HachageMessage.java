package A_auth_certif.objects.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Classe permettant de tester le hachage avec SHA-256.
 * @author Cyril Rabat
 * @version 23/10/2017
 */
public class HachageMessage {

    /**
     * Méthode principale.
     * @param args 0 message dont on veut l'empreinte SHA-256
     */
    public static void main(String[] args) {
        // Vérification des arguments
        if(args.length != 1) {
            System.err.println("Utilisation :");
            System.err.println("  java HachageMessage message");
            System.err.println("    où :");
            System.err.println("      - message : message dont on veut produire l'empreinte");
            System.exit(-1);
        }

        // Préparation de l'empreinte
        MessageDigest empreinte = null;
        try {
            empreinte = MessageDigest.getInstance("SHA-256");
        } catch(NoSuchAlgorithmException e) {
            System.err.println("Problème avec SHA : " + e);
            System.exit(-1);
        }

        // Calcul de l'empreinte
        byte[] bytes = empreinte.digest(args[0].getBytes());
        String resultat = new String(bytes);

        System.out.println("Message   : " + args[0]);
        System.out.println("Empreinte : " + resultat);
    }

}
