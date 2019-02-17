package A_auth_certif.objects.security;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class MyChiffrement_Symetric_AES {

    private final static String algo = "AES"; // "AES/CTR/NoPadding" "AES/CBC/NoPadding"

    public static void main(String[] args) {

    }

    /**
     * V1
     * @param passphrase clé AES de chiffrement
     * @param message message à chiffrer
     * @return byte[] message chiffré
     */
    public static byte[] encrypte_message(String passphrase, String message ) {
        // Chiffrement du message
        SecretKeySpec specification = new SecretKeySpec(passphrase.getBytes(), algo);
        byte[] bytes = null;
        try {
            Cipher chiffreur = Cipher.getInstance(algo);
            chiffreur.init(Cipher.ENCRYPT_MODE, specification);
            bytes = chiffreur.doFinal(message.getBytes());

        } catch(NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchPaddingException e) {
            System.err.println("Erreur lors du chiffrement : " + e);
            System.exit(-1);
        }
        return bytes;
    }
    /**
     * V1
     * @param passphrase clé AES de chiffrement
     * @param encrypted_message message à déchiffrer
     * @return String message déchiffré
     */
    public static String decrypt_message(String passphrase, byte[] encrypted_message) {
        SecretKeySpec specification = new SecretKeySpec(passphrase.getBytes(), algo);
        byte[] decrypted_message = null;
        try {
            Cipher dechiffreur = Cipher.getInstance(algo);
            dechiffreur.init(Cipher.DECRYPT_MODE, specification);
            decrypted_message = dechiffreur.doFinal(encrypted_message);
        } catch(NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchPaddingException e) {
            System.err.println("Erreur lors du dechiffrement : " + e);
            System.exit(-1);
        }
        return new String(decrypted_message);
    }
    /**
     * V2
     * @param secretKey clé de chiffrement AES
     * @param message à chiffré
     * @return byte[] message chiffré
     */
    public static byte[] encrypte_message(SecretKey secretKey, String message ) {
        // Chiffrement du message
        SecretKeySpec specification = new SecretKeySpec(MySecurity.bytesToHex(secretKey.getEncoded()).substring(0,16).getBytes(), algo);
        byte[] bytes = null;
        try {
            Cipher chiffreur = Cipher.getInstance(algo); // "AES/ECB/NoPadding"
            chiffreur.init(Cipher.ENCRYPT_MODE, specification);
            bytes = chiffreur.doFinal(message.getBytes());
        } catch(NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchPaddingException e) {
            System.err.println("Erreur lors du chiffrement : " + e);
            System.exit(-1);
        }
        return bytes;
    }
    /**
     * V2
     * @param secretKey clé de chiffrement AES
     * @param encrypted_message à déchiffré
     * @return String message déchiffré
     */
    public static String decrypt_message(SecretKey secretKey, byte[] encrypted_message) {
        SecretKeySpec specification = new SecretKeySpec(MySecurity.bytesToHex(secretKey.getEncoded()).substring(0,16).getBytes(), algo);
        byte[] decrypted_message = null;
        try {
            Cipher dechiffreur = Cipher.getInstance(algo);
            dechiffreur.init(Cipher.DECRYPT_MODE, specification);
            decrypted_message = dechiffreur.doFinal(encrypted_message);
        } catch(NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchPaddingException e) {
            System.err.println("Erreur lors du dechiffrement : " + e);
            System.exit(-1);
        }
        return new String(decrypted_message);
    }
}
