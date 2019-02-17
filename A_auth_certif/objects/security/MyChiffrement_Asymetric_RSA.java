package A_auth_certif.objects.security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class MyChiffrement_Asymetric_RSA {

    static final String public_key_file_name = "public_key.bin";
    static final String private_key_file_name = "private_key.bin";

    public static void main(String[] args) {
    }

    public static byte[] encrypte_message(String public_key_path_folder, String message, boolean backup) {
        // Recuperation de la cle publique
        PublicKey clePublique = GestionClesRSA.lectureClePublique(public_key_path_folder+public_key_file_name);
        // Chiffrement du message
        byte[] bytes = null;
        try {
            Cipher chiffreur = Cipher.getInstance("RSA");
            chiffreur.init(Cipher.ENCRYPT_MODE, clePublique);
            bytes = chiffreur.doFinal(message.getBytes());
        } catch(NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchPaddingException e) {
            System.err.println("Erreur lors du chiffrement : " + e);
            System.exit(-1);
        }

        if(backup) {
            try {
                FileOutputStream fichier = new FileOutputStream(public_key_file_name+"_encrypted");
                fichier.write(bytes);
                fichier.close();
            } catch(IOException e) {
                System.err.println("Erreur lors de la sauvegarde du message chiffré : " + e);
                System.exit(-1);
            }
        }

        return bytes;
    }
    public static byte[] encrypte_message(PublicKey publicKey, String message, boolean backup) {
        // Chiffrement du message
        byte[] bytes = null;
        try {
            Cipher chiffreur = Cipher.getInstance("RSA");
            chiffreur.init(Cipher.ENCRYPT_MODE, publicKey);
            bytes = chiffreur.doFinal(message.getBytes());
        } catch(NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchPaddingException e) {
            System.err.println("Erreur lors du chiffrement : " + e);
            System.exit(-1);
        }

        if(backup) {
            try {
                FileOutputStream fichier = new FileOutputStream(public_key_file_name+"_encrypted");
                fichier.write(bytes);
                fichier.close();
            } catch(IOException e) {
                System.err.println("Erreur lors de la sauvegarde du message chiffré : " + e);
                System.exit(-1);
            }
        }

        return bytes;
    }

    public static String decrypt_message(String private_key_path_folder, byte[] encrypted_message) {
        // Récupération de la clé privée
        PrivateKey clePrivee = GestionClesRSA.lectureClePrivee(private_key_path_folder+private_key_file_name);

        // Chargement du message chiffré si enregistré dans un fichier
        /*byte[] messageCode = null;
        try {
            FileInputStream fichier = new FileInputStream(args[1]);
            messageCode = new byte[fichier.available()];
            fichier.read(messageCode);
            fichier.close();
        } catch(IOException e) {
            System.err.println("Erreur lors de la lecture du message : " + e);
            System.exit(-1);
        }*/

        // Déchiffrement du message
        byte[] bytes = null;
        try {
            Cipher dechiffreur = Cipher.getInstance("RSA");
            dechiffreur.init(Cipher.DECRYPT_MODE, clePrivee);
            bytes = dechiffreur.doFinal(encrypted_message);
        } catch(NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchPaddingException e) {
            System.err.println("Erreur lors du déchiffrement : " + e);
            System.exit(-1);
        }

        return new String(bytes);
    }
    public static byte[] decrypt_message(PrivateKey privateKey, byte[] encrypted_message) {

        // Chargement du message chiffré si enregistré dans un fichier
        /*byte[] messageCode = null;
        try {
            FileInputStream fichier = new FileInputStream(args[1]);
            messageCode = new byte[fichier.available()];
            fichier.read(messageCode);
            fichier.close();
        } catch(IOException e) {
            System.err.println("Erreur lors de la lecture du message : " + e);
            System.exit(-1);
        }*/
        //System.out.println("size encrypted_message "+encrypted_message.length);
        // Déchiffrement du message
        byte[] bytes = null;
        try {
            Cipher dechiffreur = Cipher.getInstance("RSA");
            dechiffreur.init(Cipher.DECRYPT_MODE, privateKey);
            bytes = dechiffreur.doFinal(encrypted_message);
        } catch(NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchPaddingException e) {
            System.err.println("Erreur lors du déchiffrement : " + e);
            System.exit(-1);
        }

        return bytes;
    }
    public static String decrypt_message_str(PrivateKey privateKey, byte[] encrypted_message) {

        // Chargement du message chiffré si enregistré dans un fichier
        /*byte[] messageCode = null;
        try {
            FileInputStream fichier = new FileInputStream(args[1]);
            messageCode = new byte[fichier.available()];
            fichier.read(messageCode);
            fichier.close();
        } catch(IOException e) {
            System.err.println("Erreur lors de la lecture du message : " + e);
            System.exit(-1);
        }*/
        //System.out.println("size encrypted_message "+encrypted_message.length);
        // Déchiffrement du message
        byte[] bytes = null;
        try {
            Cipher dechiffreur = Cipher.getInstance("RSA");
            dechiffreur.init(Cipher.DECRYPT_MODE, privateKey);
            bytes = dechiffreur.doFinal(encrypted_message);
        } catch(NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchPaddingException e) {
            System.err.println("Erreur lors du déchiffrement : " + e);
            System.exit(-1);
        }

        return new String(bytes);
    }
}
