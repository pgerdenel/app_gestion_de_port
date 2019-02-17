package A_auth_certif.objects.security;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
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

    // sauvegarde une clé public PEM de PHP dans un fichier
    static void backupPublicKeyInFile(String path, String filename, String key) {
        // Création du fichier de sortie
        FileWriter fs = null;
        try {
            fs = new FileWriter(path+filename);
        } catch (IOException e) {
            System.err.println("Erreur lors de l'ouverture du fichier '" + path+filename + "'."+e);
            System.exit(-1);
        }

        // Sauvegarde dans le fichier
        try {
            fs.write(key);
            System.out.println("\t> file \""+path+filename+"\"");
            fs.flush();
            fs.close();   // Fermeture du fichier
        } catch (IOException e) {
            System.err.println("Erreur lors de l'écriture dans le fichier."+e);
            System.exit(-1);
        }
    }

    // permet de convertir une PublicKey en clé pem String
    static String convertPublicKeyToPem(PublicKey publicKey) {

        final String PUBLICKEY_PREFIX    = "-----BEGIN PUBLIC KEY-----";
        final String PUBLICKEY_POSTFIX   = "-----END PUBLIC KEY-----";

        return PUBLICKEY_PREFIX + "\n" + DatatypeConverter.printBase64Binary(publicKey.getEncoded()).replaceAll("(.{64})", "$1\n") + "\n" + PUBLICKEY_POSTFIX;
    }
}
