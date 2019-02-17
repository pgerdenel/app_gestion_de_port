package C2_gestion_user;

import C2_gestion_user.objects.Config;
import C2_gestion_user.objects.GestionnaireUtilisateurs;
import C2_gestion_user.objects.MyAPI;
import C2_gestion_user.objects.MyJSONApi;
import C2_gestion_user.objects.cert.MyCertificat;
import C2_gestion_user.objects.security.GenerationClesRSA;
import C2_gestion_user.objects.security.GestionClesRSA;
import C2_gestion_user.objects.security.MyChiffrement_Asymetric_RSA;
import C2_gestion_user.objects.security.MyChiffrement_Symetric_AES;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static C2_gestion_user.objects.Config.fichierExiste;

/*
 * Serveur HTTP 9090 du gestionnaire utilisateur
 *
 * Permet d'assurer la liaison avec le backoffice afin de répondre à plusieurs traitements :
 *
 * - login de l'utilisateur
 * - récupération de la liste des utilisateurs propriétaire
 * - récupération des infos d'un bateau d'un utilisateur
 * - creer un bateau pour un utilisateur (enregistrer le bateau au format.json)
 * - supprimer le bateau d'un utilisateur
 * - gérer les fichiers utilisateurs(arborescence des bateaux de chaque utilisateur, leur couple login/pass)
 * - envoie sa clé publique et reçoit la clé publique serveur et son certificat sur le port 2026
 */

public class GestionUser_Srv {

    private static final String path = "src\\C2_gestion_user\\resources\\";
    private static final String path_all_users = "src\\C2_gestion_user\\resources\\all_users.json";
    private static final String path_all_users_base = "src\\C2_gestion_user\\resources\\all_users_base.json";

    public static void main(String[] args) {

        System.out.println("##############################################################################");

        /* CONFIG SERVEUR */
        System.out.println("# CONFIG SERVEUR");
        // Création/récupération de l'objet config
        Config c;
        if(fichierExiste(path+"config.json")) {
            c = new Config(path+"config.json");
            System.out.println("config exist -> config loaded from \""+path+"config.json"+"\""+"\n");
        }
        else {
            c = new Config(path+"config.json", true);
            c.ajouterValeur("adresse", "localhost");
            c.ajouterValeur("port", 9090);
            c.sauvegarder();
            c.sauvegarder();
            System.out.println("config does not exist -> config created at \""+path+"config.json"+"\""+"\n");
        }

        /* INIT CERTIFICAT */
        System.out.println("# INIT CERTIFICAT");
        // Création/récupération des clés publiques & privés
        if(fichierExiste(path+"private_key.bin") && fichierExiste(path+"public_key.bin")) { // on les charge
            System.out.println("keys exist -> key loaded"+"\n");
        }
        else { // On les crée
            System.out.println("keys does not exist -> keys created"+"\n");
            GenerationClesRSA.main(new String[]{path+"private_key.bin", path+"public_key.bin"});
        }
        // Récupération de la clé publiquer et création du certificat
        if(!MyAPI.checkIfCertEtcOk()) {
            while(MyAPI.checkSocketListen(2025)) { // tant que l'autorité de certification n'est pas joignable
                System.out.print("\nAutorite de certification non joignable, retry in "+ 5 +"sec ...");
            }
            ask_certificate(2025, 2026);
        }
        while(!MyAPI.pingHost("localhost", 8080)) {
            System.out.println("\nBackoffice non joignable, retry in "+ 5 +"sec ...");
        }
        exchangeCertificate();
        /* INIT EXECUTOR */
        System.out.println("# INIT EXECUTOR OBJECT");
        // SingleThreadExecutor : un pool qui ne contient qu'un seul thread. Toutes les tâches soumises sont executees de manière sequentielle
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        System.out.println("ExecutorService object created\n");

        /* INIT Gestionnaire */
        System.out.println("# INIT Users");
        GestionnaireUtilisateurs g = new GestionnaireUtilisateurs(GestionUser_Srv.getPath_all_users_base());
        g.init();
        System.out.println("All_users: \n" + g.toString());
        System.out.println("user created - > user reset\n");

        /* INIT HTTP SERVEUR */
        System.out.println("# INIT HTTP_SERVEUR");
        HttpServer serveur = null;
        try {
            serveur = HttpServer.create(new InetSocketAddress(c.getInt("port")), 0);
            System.out.println("HTTP-Server -> created");
        } catch(IOException e) {
            System.err.println("Erreur lors de la creation du serveur " + e);
            System.exit(-1);
        }
        System.out.println("##############################################################################\n");

        /* HANDLERs REQUEST */
        // Handler From Portail>Backoffice>GestionnaireUser
        serveur.createContext("/query_login.html", new Query_Login_Handler()); // verifie les identifiants de l'utilisateur et crée son arborescence
        serveur.createContext("/query_list_proprio.html", new Query_ListProprio_Handler()); // verifie les identifiants de l'utilisateur et crée son arborescence

        // Handler From Portail>Backoffice>GestionnaireDePort>Backoffice>GestionnaireUser
        serveur.createContext("/query_boat_infos.html", new Query_BoatInfos_Handler()); // Retrieve bateaux informations
        serveur.createContext("/create_boat.html", new Create_Boat_Handler()); // creer le bateau dans les dossiers de l'utilisateur spécifié
        serveur.createContext("/delete_boat.html", new Delete_Boat_Handler()); // supprime le bateau dans les dossiers de l'utilisateur spécifié

        serveur.setExecutor(executorService);
        serveur.start();

        System.out.println("Gestionnaire d'utilisateur started & listen at http://"+c.getString("adresse")+":"+c.getInt("port"));
    }

    public static String getPath() {
        return path;
    }
    public static String getPath_all_users() {
        return path_all_users;
    }
    private static String getPath_all_users_base() {
        return path_all_users_base;
    }

    /**
     * Envoie une requête à l'authorité de certification
     * envoie sa clé publique
     * reçoit la clé publique du serveur
     * enregistre la clé publique du serveur
     * reçoit son certificat
     * enregistre son certificat
     */
    private static void ask_certificate(int port_number_auth_certif, int port_number_ask_cert) {

        boolean isMessageAskCert_sended = false;        // boolean état "Envoie du message askCert"
        boolean isPublic_Key_client_sended = false;     // boolean état "Envoie de sa clé publique"
        boolean isPublic_key_serveur_received = false;  // boolean état "En reception de la clé publique serveur"
        boolean isSymetric_key_received = false;        // boolean état "En reception de la clé symetrique"
        boolean isCertificat_received = false;          // boolean état "En attente de reception de son certificat
        boolean isRequestEnded = false;                 // boolean état "Etat des échanges terminés"

        while(!isMessageAskCert_sended) {

            // création d'un JSONObject ask_cert
            JSONObject message_ask_cert = new JSONObject();
            MyJSONApi.ajouterValeur(message_ask_cert, "nom_entite", "gest_user");
            MyJSONApi.ajouterValeur(message_ask_cert, "port_number", port_number_ask_cert);

            /* INIT UDP_SOCKET_CLIENT */
            // Création de la socket
            DatagramSocket socket = null;
            try {
                socket = new DatagramSocket();
                System.out.println("Socket Client created");

            } catch (SocketException e) {
                System.err.println("Erreur lors de la creation de la socket : " + e);
                System.exit(-1);
            }

            // Transformation en tableau d'octets
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(message_ask_cert.toString());
            } catch (IOException e) {
                System.err.println("Erreur lors de la sérialisation : " + e);
                System.exit(-1);
            }

            // Création et envoi du segment UDP
            try {
                byte[] donnees = baos.toByteArray();
                InetAddress adresse = InetAddress.getByName("localhost");
                DatagramPacket msg = new DatagramPacket(donnees, donnees.length, adresse, port_number_auth_certif);
                socket.send(msg);
                System.out.println("message_ask_cert sended");
                isMessageAskCert_sended = true;
                //i--;
                socket.close();

            } catch (UnknownHostException e) {
                System.err.println("Erreur lors de la création de l'adresse : " + e);
                System.exit(-1);
            } catch (IOException e) {
                System.err.println("Erreur lors de l'envoi du message_ask_cert : " + e);
                System.exit(-1);
            } finally {
                System.out.println("socket closed");
                socket.close();
            }

            // on attend 5 secondes entre chaque envoie
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException ie) {
                System.err.println("Erreur lors de l'attente imposé entre chaque envoie : " + ie);
                System.exit(-1);
            }
        }

        // Récupération de la clé publique
        PublicKey publicKey_client = GestionClesRSA.lectureClePublique(path+"public_key.bin");
        PrivateKey privateKey = GestionClesRSA.lectureClePrivee(path+"private_key.bin");
        int i=2;
        // Création de la socket
        DatagramSocket socket = null;

        /* (2) ENVOIE ET ECHANGE DES CLES *****************************************************************************/

        while(!isRequestEnded) { //tant que le traitement n'est pas correctement fait

            /* (2.1) ENVOIE DE LA CLE PUBLIQUE AU SERVEUR *************************************************************/

            while (!isPublic_Key_client_sended) { // tant que le client n'a pas envoyé sa clé et que le serveur ne l'a pas reçu, on l'envoie 3 fois toutes les 30secondes

                System.out.println("\n> Envoie de la cle publique au serveur");

                /* INIT UDP_SOCKET_CLIENT */
                try {
                    socket = new DatagramSocket();
                    System.out.println("Socket Client created");

                } catch(SocketException e) {
                    System.err.println("Erreur lors de la creation de la socket : " + e);
                    System.exit(-1);
                }

                // Transformation en tableau d'octets
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(publicKey_client);
                } catch (IOException e) {
                    System.err.println("Erreur lors de la sérialisation : " + e);
                    System.exit(-1);
                }

                // Création et envoi du segment UDP
                try {
                    byte[] donnees = baos.toByteArray();
                    InetAddress adresse = InetAddress.getByName("localhost");
                    DatagramPacket msg = new DatagramPacket(donnees, donnees.length, adresse, port_number_ask_cert);
                    socket.send(msg);
                    System.out.println("public key sended");
                    isPublic_Key_client_sended = true;
                    //i--;
                    socket.close();

                }
                catch (UnknownHostException e) {
                    System.err.println("Erreur lors de la création de l'adresse : " + e);
                    System.exit(-1);
                }
                catch (IOException e) {
                    System.err.println("Erreur lors de l'envoi du message : " + e);
                    System.exit(-1);
                }
                finally {
                    System.out.println("socket closed");
                    socket.close();
                }


                // si le nombre d'envoie est dépassé mais que la clé publique n'a pas été reçu
                /*if(!isPublic_Key_client_sended && i==0) {
                    isPublic_Key_client_sended = true;
                    // on ferme la socket
                    System.out.println("nombre envoie atteinte\nfermeture de la socket");
                    //socket.close();
                    System.exit(0);
                }*/


                // on attend 30 seconds entre chaque envoie
                try {
                    TimeUnit.SECONDS.sleep(5);
                }
                catch(InterruptedException ie) {
                    System.err.println("Erreur lors de l'attente imposé entre chaque envoie : " + ie);
                    System.exit(-1);
                }

            }
            /* (2.2) RECEPTION DE LA CLE PUBLIQUE SERVEUR ***************************************************************/
            while (!isPublic_key_serveur_received) {

                System.out.println("\n> En attente de la cle publique du serveur");

                /* INIT UDP_SOCKET_SERVEUR */
                try {
                    socket = new DatagramSocket(port_number_ask_cert);
                    System.out.println("Socket listen on "+"localhost:"+port_number_ask_cert);

                } catch(SocketException e) {
                    System.err.println("Erreur lors de la creation de la socket : " + e);
                    System.exit(-1);
                }

                // Lecture du message du serveur
                DatagramPacket msgRecu = null;
                try {
                    byte[] tampon = new byte[1024];
                    msgRecu = new DatagramPacket(tampon, tampon.length);
                    socket.receive(msgRecu);
                }
                catch (IOException e) {
                    System.err.println("Erreur lors de la réception du message : " + e);
                    System.exit(-1);
                }

                // Récupération de la clé publique serveur
                try {
                    ByteArrayInputStream bais = new ByteArrayInputStream(msgRecu.getData());
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    PublicKey publicKey_serveur = (PublicKey) ois.readObject();

                    // on sauvegarde la clé publique du serveur dans un fichier
                    GestionClesRSA.sauvegardeClePublique(publicKey_serveur, path+"public_key_authority.bin");

                    System.out.println("¨Public_key recu : " + publicKey_serveur);
                    isPublic_key_serveur_received = true;
                    socket.close();
                    System.out.println("socket closed");
                }
                catch (ClassNotFoundException e) {
                    System.err.println("Objet reçu non reconnu : " + e);
                    System.exit(-1);
                }
                catch (IOException e) {
                    System.err.println("Erreur lors de la récupération de l'objet : " + e);
                    System.exit(-1);
                }
            }

            /* (3) RECEPTION DE LA CLE SYMETRIQUE ET DECHIFFREMENT ****************************************************/
            String decrypted_symetric_key = null;
            while (!isSymetric_key_received) {

                System.out.println("\n> En attente de la symetric_key de la part du serveur ... ");

                /* INIT UDP_SOCKET_SERVEUR */
                try {
                    socket = new DatagramSocket(port_number_ask_cert);
                    System.out.println("Socket listen on localhost:"+port_number_ask_cert);

                } catch(SocketException e) {
                    System.err.println("Erreur lors de la creation de la socket : " + e);
                    System.exit(-1);
                }

                // Lecture du message du serveur
                DatagramPacket msgRecu = null;
                try {
                    byte[] tampon = new byte[283]; // 10
                    msgRecu = new DatagramPacket(tampon, tampon.length);
                    socket.receive(msgRecu);
                }
                catch (IOException e) {
                    System.err.println("Erreur lors de la réception de la symetric_key : " + e);
                    System.exit(-1);
                }

                // Récupération de la clé symétrique chiffrée par la clé publique de nous(ce client du code là)
                try {
                    ByteArrayInputStream bais = new ByteArrayInputStream(msgRecu.getData());
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    byte[] encrypted_symetric_key = (byte[]) ois.readObject();
                    System.out.println("Clé symétrique encrypted: " + new String(encrypted_symetric_key));

                    // on décrypte la clé symétrique avec la clé asymétrique privée RSA du client
                    decrypted_symetric_key = MyChiffrement_Asymetric_RSA.decrypt_message_str(privateKey, encrypted_symetric_key); // /*(SecretKey) MySecurity.deserialize(*/

                    isSymetric_key_received = true;
                    socket.close();
                }
                catch (Exception e) {
                    System.err.println("Erreur lors de la récupération de la symetric_key : " + e);
                    System.exit(-1);
                }
            }

            /* (4) RECEPTION DE L'ENCRYPTED CERTIFICAT ET DECHIFFREMENT ***********************************************/
            while (!isCertificat_received) {

                System.out.println("\n> En attente du certificat de la part du serveur .... ");

                /* INIT UDP_SOCKET_SERVEUR ****************************************************************************/
                try {
                    socket = new DatagramSocket(port_number_ask_cert);
                    System.out.println("Socket listen on localhost:"+port_number_ask_cert);

                } catch(SocketException e) {
                    System.err.println("Erreur lors de la creation de la socket : " + e);
                    System.exit(-1);
                }

                // Lecture du message du serveur
                DatagramPacket msgRecu = null;
                try {
                    byte[] tampon = new byte[1024]; // 779
                    msgRecu = new DatagramPacket(tampon, tampon.length);
                    socket.receive(msgRecu);
                }
                catch (IOException e) {
                    System.err.println("Erreur lors de la réception du certificat : " + e);
                    System.exit(-1);
                }

                // Récupération du certificat
                try {
                    ByteArrayInputStream bais = new ByteArrayInputStream(msgRecu.getData());
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    byte[] encrypted_certificat = (byte[]) ois.readObject();

                    // on décrypte le certificat avec la clé symetric
                    String decrypted_certificat = MyChiffrement_Symetric_AES.decrypt_message(decrypted_symetric_key, encrypted_certificat);

                    // on reconstruit l'objet json depuis le certificat String décrypté
                    JSONObject my_certificat_json = new JSONObject(decrypted_certificat);
                    // on reconstruit l'objet certificat depuis le JSONObject certiticat
                    MyCertificat myCertificat = new MyCertificat(my_certificat_json);
                    System.out.println("Recu : " + myCertificat.toString());

                    // on enregistre le certificat dans un fichier
                    myCertificat.writeJSONCert(path, "cert_gest_user.json");
                    isCertificat_received = true;
                    isRequestEnded = true;
                }
                catch (ClassNotFoundException e) {
                    System.err.println("certificat reçu non reconnu : " + e);
                    System.exit(-1);
                }
                catch (IOException e) {
                    System.err.println("Erreur lors de la récupération du certificat : " + e);
                    System.exit(-1);
                }
                catch(JSONException je) {
                    System.err.println("Erreur lors de la désérialisation de l'objet certificat : " + je);
                    System.exit(-1);
                }
                finally {
                    System.out.println("\n");
                }
            }

        }
    }
    /**
     * Permet au gest_user de contacter le back office (requête HTTP) pour lui envoyer son certificat et recevoir le sien
     * Enregistre le certificat reçue du back-office
     */
    private static void exchangeCertificate() {
        System.out.println("\n> Envoie d'une demande d'échange de certificat au back-office " );

        DatagramSocket socket = null;

        boolean isRequestEnd = false;

            // création d'une requête HTTP pour envoyer le certificat
            /*  Construction d'une requête HTTP
             *  Données envoyées : port_number_join
             *  Destinataire server : BackOffice
             *  Destinataire handler : ExchangeCertificate_Handler | exchange_certificate
             */

            // on récupère le certificat du gestionnaire_user
            String urlParameters = MyJSONApi.retrieveJSONObjectFromFile(GestionUser_Srv.getPath()+"cert_gest_user.json").toString();
            StringBuilder reponse;
            try {

                byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
                URL myurl = new URL("http://localhost:8080/exchange_certificate.html");

                HttpURLConnection con = (HttpURLConnection) myurl.openConnection();

                con.setDoOutput(true);
                con.setRequestMethod("POST");
                con.setRequestProperty("User-Agent", "Java client");
                con.setRequestProperty("Content-Type", "application/json");

                try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                    wr.write(postData);
                    System.out.println("\nexchangeCertificate: requete d'echange de certificat envoye au back-office= " + urlParameters);
                }

                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {

                    String line;
                    reponse = new StringBuilder();

                    while ((line = in.readLine()) != null) {
                        reponse.append(line);
                        reponse.append(System.lineSeparator());
                    }
                }
                System.out.println("\nexchangeCertificate: certificat reçu du back-office= " + reponse);

                // on reconstuit le jsonObject reçue du back-office
                JSONObject cert_backoffice = MyJSONApi.castinJSONObject(reponse.toString());

                // enregistrement du certificat du back-office
                MyJSONApi.writeToJSONFile(cert_backoffice, GestionUser_Srv.getPath()+"cert_back_office.json");
            } catch(IOException e) {
                System.out.println("Error |"+e+"| à l'envoie au serveur back-office at ");
                System.exit(-1);
            }
    }

}
