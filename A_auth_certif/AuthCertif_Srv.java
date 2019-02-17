package A_auth_certif;

import A_auth_certif.objects.Config;
import A_auth_certif.objects.security.GenerationClesRSA;

import java.io.*;
import java.net.*;
import java.util.HashMap;

import static A_auth_certif.objects.Config.fichierExiste;

/**
 * AuthCertif_Srv UDP permettant de tester l'envoi d'objets dans un segment UDP.
 * Le client commence par envoyer sa clé publique au serveur et celui-ci répond en envoyant la sienne.
 * Le client lance ensuite un challenge au serveur : le client envoie une chaîne de caractères chiffrée au serveur qu'il doit déchiffrer.
 * Puis le serveur retourne la chaîne chiffrée que le client peut déchiffrer et comparer à la chaîne originale
 */

public class AuthCertif_Srv implements Runnable {

    public final static String path = "src\\A_auth_certif\\resources\\";
    private static HashMap<String, Boolean> map_entite;
    private static HashMap<String, Boolean> map_entite_port;

    public static void main(String[] args) {
        AuthCertif_Srv authCertif_srv = new AuthCertif_Srv();
        authCertif_srv.run();
    }

    @Override
    public void run() {

        /* CONFIG *****************************************************************************************************/
        System.out.println("# CONFIGURATION");
        // Création/récupération de l'objet config
        Config c;
        if (fichierExiste(path + "config.json")) {
            c = new Config(path + "config.json");
            System.out.println("config exist -> config loaded from \"" + path + "config.json" + "\"");
        } else {
            c = new Config(path + "config.json", true);
            c.ajouterValeur("adresse", "127.0.0.1");
            c.ajouterValeur("port", 2025);
            c.sauvegarder();
            c.sauvegarder();
            System.out.println("config does not exist -> config created at \"" + path + "config.json" + "\"");
        }
        /*INIT CERTIFICAT *********************************************************************************************/
        System.out.println();
        System.out.println("# INIT CERTIFICAT");
        // Création/récupération des clés publiques & privés
        if (fichierExiste(path + "private_key.bin") && fichierExiste(path + "public_key.bin")) { // on les charge
            System.out.println("keys exist -> key loaded");
        } else { // On les crée
            System.out.println("keys does not exist -> keys created");
            GenerationClesRSA.main(new String[]{path + "private_key.bin", path + "public_key.bin"});
        }
        /* INIT DATA SERVEUR ******************************************************************************************/
        System.out.println("# INIT DATA");
        map_entite = new HashMap<>();
        map_entite_port = new HashMap<>();


        /* INIT UDP SOCKET SERVEUR ************************************************************************************/
        System.out.println();
        System.out.println("# INIT UDP_SOCKET_SERVEUR");

        /* ENVOIE ET ECHANGE DES CLES *********************************************************************************/
        // On répète ce traitement indéfiniment
        while(true) {
            try {
                DatagramSocket socket = new DatagramSocket(c.getInt("port")); // 2025
                System.out.println("\nAuthCertif Started " + socket.getLocalAddress() + ":" + socket.getLocalPort() + "\n");
                System.out.println("> En attente de requete demande de certicat de la part des entites ....\n");
                // Lecture du message du client
                // On reçoit une requete d'une entite avec un JSONObject

                byte[] tampon = new byte[1024];
                DatagramPacket msgRecu = new DatagramPacket(tampon, tampon.length);
                socket.receive(msgRecu);

                new Thread(new Thread_Responder(msgRecu)).start();
                socket.close();
            } catch (IOException e) {
                System.err.println("Erreur lors de la reception du message : " + e);
                System.exit(-1);
            }
            catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            } finally {
                System.out.println("Base MapEntite \n" + map_entite.toString() + "\n");
                System.out.println("Base MapEntite_Port \n" + map_entite_port.toString() + "\n");
            }
            System.out.println();
        }

    }

    /* Getters & Setters */
    public static String getPath() {
        return path;
    }
    public static HashMap<String, Boolean> getMap_entite() {
        return map_entite;
    }
    public static void setMap_entite(HashMap<String, Boolean> map_entite) {
        AuthCertif_Srv.map_entite = map_entite;
    }
    public static HashMap<String, Boolean> getMap_entite_port() {
        return map_entite_port;
    }
    public static void setMap_entite_port(HashMap<String, Boolean> map_entite_port) {
        AuthCertif_Srv.map_entite_port = map_entite_port;
    }
}