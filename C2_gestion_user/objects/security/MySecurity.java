package C2_gestion_user.objects.security;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public interface MySecurity {

    // génère une chaine aléatoire de 16 bytes
    static String generate_passphrase() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 16;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }
    // génère une clé AES et renvoie un objet SecretKey
    static SecretKey/*String*/ generate_key() {
        SecretKey key = null;
        System.out.println("Generation d'une cle pour AES");
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            //keyGen.init(128);
            key = keyGen.generateKey();
            //System.out.println("cle=" + bytesToHex(key.getEncoded()));

        }
        catch(NoSuchAlgorithmException nsae) {
            System.out.println("error "+nsae);
            System.exit(-1);
        }
        //return bytesToHex(key.getEncoded()).substring(0,16);
        return key;
    }
    // génère une clé AES et renvoie un String
    static String/*String*/ generate_key_str() {
        SecretKey key = null;
        System.out.println("Generation d'une cle pour AES");
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            //keyGen.init(128);
            key = keyGen.generateKey();
            //System.out.println("cle=" + bytesToHex(key.getEncoded()));

        }
        catch(NoSuchAlgorithmException nsae) {
            System.out.println("error "+nsae);
            System.exit(-1);
        }
        return bytesToHex(key.getEncoded()).substring(0,16);
        //return key;
    }
    // convertit un tableau de bytes en hexadécimal
    static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
