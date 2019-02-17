package C3_gestion_de_port;

import C3_gestion_de_port.objects.Config;
import C3_gestion_de_port.objects.MyAPI;
import C3_gestion_de_port.objects.cert.MyCertificat;
import C3_gestion_de_port.objects.port.Parking_Port;
import C3_gestion_de_port.objects.security.GestionClesRSA;
import C3_gestion_de_port.objects.security.MyChiffrement_Asymetric_RSA;
import C3_gestion_de_port.objects.security.MyChiffrement_Symetric_AES;
import C3_gestion_de_port.threads_service.*;
import C3_gestion_de_port.objects.MyJSONApi;
import C3_gestion_de_port.objects.security.GenerationClesRSA;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static C3_gestion_de_port.objects.Config.fichierExiste;

/*
 * Class Gestionnaire de port
 * - Gère un port particulier Port 1
 * - reçoit les requetes depuis le backoffice et demarre une socket pour la requete associe
 * - fait suivre les requetes reçues à un Thread_Port_Service
 * - PORT 2091 à 2094 pour les ports de 1 à 4
 * - 2092 pour le port 2
 */

public class GestionnaireDePort_Srv_Port2 {

    public final static String path = "src\\C3_gestion_de_port\\resources\\ports\\port2\\";
    private final static String nomPort = "port2";

    private static Parking_Port parking;

    public static void main(String args[]) {

        /* CONFIG SERVEUR **********************************************************************************/
        System.out.println("# CONFIG SERVEUR");
        // Creation/recuperation de l'objet config
        Config c;
        if (fichierExiste(path + "config.json")) {
            c = new Config(path + "config.json");
            System.out.println("config exist -> config loaded from \"" + path + "config.json" + "\"");
        }
        else {
            c = new Config(path + "config.json", true);
            c.ajouterValeur("adresse", "localhost");
            c.ajouterValeur("port", 2092);
            c.sauvegarder();
            c.sauvegarder();
            System.out.println("config does not exist -> config created at \"" + path + "config.json" + "\"");
        }

        /* INIT CERTIFICAT ***********************************************************************************/
        System.out.println("\n# INIT CERTIFICAT");
        // Creation/recuperation des cles publiques & prives
        if (fichierExiste(path + "private_key.bin") && fichierExiste(path + "public_key.bin")) { // on les charge
            System.out.println("keys exist -> key loaded");
        }
        else { // On les cree
            System.out.println("keys does not exist -> keys created");
            GenerationClesRSA.main(new String[]{path + "private_key.bin", path + "public_key.bin"});
        }
        // Envoie et récuperation clé publique serveur et certificat
        if(!MyAPI.checkIfCertEtcOk("2")) {
            while(MyAPI.checkSocketListen(2025)) { // tant que l'autorité de certification n'est pas joignable
                System.out.print("\nAutorite de certification non joignable, retry in "+ 5 +"sec ...");
            }
            ask_certificate(2025, 2032);
        }
        // on attend que le backoffice soit joignable avant de d'echanger nos certificats
        while(!C2_gestion_user.objects.MyAPI.pingHost("localhost", 8080)) {
            System.out.println("\nBackoffice non joignable, retry in "+ 5 +"sec ...");
        }
        exchangeCertificate();

        /* INITIALISATION DES DONNEES JSON *******************************************************************/


        /* INITIALISATION DES PARKING PORT ET ENREGISTREMENT AU BACKOFFICE ***********************************/
        System.out.println("\n# INIT PARKINGPORT AND RECORD AT BACKOFFICE");
        // On vérifie que le BackOffice est joignable avant d'enregistrer les ports
        while(!pingHost("localhost", 8080)) {
            System.out.println("Backoffice non joignable, retry in 5sec ...");
        }
        parking = new Parking_Port(nomPort, "informations_port.json", "prix_place.json");
        transmitRecordPort();

        /* INITIALISATION DES DONNEES ************************************************************************/
        // SingleThreadExecutor : un pool qui ne contient qu'un seul thread. Toutes les tâches soumises sont executees de manière sequentielle
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        /* SOCKET SERVER UDP **************************************************************/
        DatagramSocket socket = null;
        DatagramPacket msgRecu = null;
        
        int serverPort = 2092;

        try {
            // Setup socket
            socket = new DatagramSocket(serverPort);
            System.out.println("\nGestionnaireDePort_Srv Started "+socket.getLocalAddress()+":"+socket.getLocalPort()+"\n");
        }
        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        finally {
            System.out.println("Base MapPort -6 \n"+GestionnaireDePort_Srv_Port2.getParking().getMap_place().get(-6).toString()+"\n");
        }

        System.out.println("> En attente de requete cliente ....\n");


        while(true) {

            // Lecture du message du client
            try {
                //System.out.println("> Lecture de la requete cliente ....");
                // On reçoit une requete d'un client avec des donnees JSON
                byte[] tampon = new byte[1024];
                msgRecu = new DatagramPacket(tampon, tampon.length);
                Objects.requireNonNull(socket).receive(msgRecu);
            }
            catch (IOException e) {
                System.err.println("Erreur lors de la reception du message : " + e);
                System.exit(-1);
            }
            System.out.println();

            // Recuperation du message du client
            try {
                //System.out.println("> Recuperation des donnees de la requete cliente ....");
                ByteArrayInputStream bais = new ByteArrayInputStream(msgRecu.getData());
                ObjectInputStream ois = new ObjectInputStream(bais);

                JSONObject message_gestPort = MyJSONApi.castinJSONObject((String)ois.readObject());

                System.out.println(message_gestPort);
                System.out.println("> New Treatments called : id_treatment = " + MyJSONApi.getString(message_gestPort, "id") + " data_json = "+MyJSONApi.getString(message_gestPort, "data"));

                /* Utiliser un future permet :
                 * d'obtenir la valeur de retour (ou null s'il n'y en a pas)
                 * d'obtenir l'exception levee par la tâche au cas où celle-ci en a levee une
                 * de demander l'annulation de l'execution de la tâche (si celle-ci prend en charge cette fonctionnalite)
                 */
                // On cree un nouveau Thread_Service pour la requete et on lui passe les donnees JSON de la requete

                switch (MyJSONApi.getString(message_gestPort, "id")) {
                    case "1":
                        System.out.println("\t> New Thread_Get_PlacePrice started for treatment");
                        Future future1 = executorService.submit(new Thread_Get_PlacePrice(MyJSONApi.getString(message_gestPort, "data")));
                        break;
                    case "2":
                        System.out.println("\t> New Thread_Get_InfosFreePlace started for treatment");
                        Future future2 = executorService.submit(new Thread_Get_InfosFreePlace(MyJSONApi.getString(message_gestPort, "data")));
                        break;
                    case "3":
                        System.out.println("\t> New Thread_Get_BoatInfos started for treatment");
                        Future future3 = executorService.submit(new Thread_Get_BoatInfos(MyJSONApi.getString(message_gestPort, "data")));
                        break;
                    case "4":
                        System.out.println("\t> New Thread_Query_BoatInPort started for treatment");
                        Future future4 = executorService.submit(new Thread_Query_BoatInPort(MyJSONApi.getString(message_gestPort, "data")));
                        break;
                    case "5":
                        System.out.println("\t> New Thread_Add_BoatAtPlace started for treatment");
                        Future future5 = executorService.submit(new Thread_Add_BoatAtPlace(MyJSONApi.getString(message_gestPort, "data")));
                        break;
                    case "6":
                        System.out.println("\t> New Thread_Remove_BoatAtPlace started for treatment");
                        Future future6 = executorService.submit(new Thread_Remove_BoatAtPlace(MyJSONApi.getString(message_gestPort, "data")));
                        break;
                    case "7":
                        System.out.println("\t> New Thread_Transfer_BoatToPort started for treatment");
                        Future future7 = executorService.submit(new Thread_Transfer_BoatToPort(MyJSONApi.getString(message_gestPort, "data")));
                        break;
                }

            }
            catch (ClassNotFoundException e) {
                System.err.println("Objet reçu non reconnu : " + e);
                System.exit(-1);
            }
            catch (IOException e) {
                System.err.println("Erreur lors de la recuperation de l'objet : " + e);
                System.exit(-1);
            }
            finally {
                // on ne ferme pas la socket pour toujours recevoir les requete sur les differents ports
                System.out.println("\n> Socket always listenning ");
                System.out.println("> En attente de requete cliente ....\n");
            }
        }
    }

    private static void transmitRecordPort() {

        StringBuilder reponse;
        HttpURLConnection con;
        StringBuilder state = null;
        URL myurl = null;

        JSONObject jo = new JSONObject();
        MyJSONApi.ajouterValeur(jo, "nom_port", nomPort);
        MyJSONApi.ajouterValeur(jo, "adresse", "localhost");
        MyJSONApi.ajouterValeur(jo, "port", 2092);
        System.out.println("\n> Envoie de la requete d'enregistrement du port = "+MyJSONApi.getString(jo, "nom_port"));

        byte[] postData = jo.toString().getBytes(StandardCharsets.UTF_8);

        try {
            myurl = new URL("http://localhost:8080/record_port.html");
            con = (HttpURLConnection) myurl.openConnection();

            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Java client");
            con.setRequestProperty("Content-Type", "application/json");

            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(postData);
                System.out.println("\t> record request -> sended");
            }

            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String line;
                reponse = new StringBuilder();

                while ((line = in.readLine()) != null) {
                    reponse.append(line);
                    reponse.append(System.lineSeparator());
                }

            }
            JSONObject backoffice_response_json = new JSONObject(reponse.toString());

            // On traite la réponse reçue du back office
            if(MyJSONApi.getString(backoffice_response_json, "record_state").equals("true")) {
                // le port à bien été enregistré au backoffice
                System.out.print("\t> Backoffice response = "+reponse.toString().trim()+" -> port enregistre");
            }
            else {
                // le port n'a pas été enregistré au backoffice
                System.out.print("> Backoffice response = "+reponse.toString().trim()+" -> port non enregistre");
            }

        }
        catch(JSONException je) {
            System.out.println("erreur de création du JSONObject à partir de reponse "+je);
            System.exit(-1);
        }
        catch(MalformedURLException me) {
            System.out.println("transmitRecordPort(), Error MalformedURLException "+me);
            System.exit(-1);
        }
        catch(ProtocolException pe) {
            System.out.println("transmitRecordPort(), Error ProtocolException "+pe);
            System.exit(-1);
        }
        catch(ConnectException ce) {
            System.out.println("transmitRecordPort(), Error |"+ce+"| au serveur BackOffice at "+myurl);
            System.exit(-1);
        }
        catch(IOException io) {
            System.out.println("transmitRecordPort(), Error IOException "+io);
            System.exit(-1);
        }
        finally {
            System.out.print("\n");
        }
    }
    private static boolean pingHost(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 5);
            return true;
        }
        catch (IOException e) {
            return false; // Either timeout or unreachable or failed DNS lookup.
        }
        finally {
            try {
                TimeUnit.SECONDS.sleep(5);
            }
            catch(InterruptedException iie) {
                System.out.println("\nBackoffice non joignable, retry in 5sec ...");
            }
        }
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
            MyJSONApi.ajouterValeur(message_ask_cert, "nom_entite", "gest_port");
            MyJSONApi.ajouterValeur(message_ask_cert, "port_number", port_number_ask_cert);
            MyJSONApi.ajouterValeur(message_ask_cert, "id_port", 2);

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
                    System.out.println("Clé symétrique decrypted: " + decrypted_symetric_key);

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
                    myCertificat.writeJSONCert(path, "cert_gest_"+nomPort+".json");
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
     * Permet au gest_port de contacter le back office (requête HTTP) pour lui envoyer son certificat et recevoir le sien
     * Enregistre le certificat reçue du back-office
     */
    private static void exchangeCertificate() {
        System.out.println("\n> Envoie d'une demande d'échange de certificat au back-office " );

        DatagramSocket socket = null;

        boolean isMessageSended =false;
        boolean isRequestEnd = false;

        // création d'une requête HTTP pour envoyer le certificat
        /*  Construction d'une requête HTTP
         *  Données envoyées : port_number_join
         *  Destinataire server : BackOffice
         *  Destinataire handler : ExchangeCertificate_Handler | exchange_certificate
         */

        // on récupère le certificat du gestionnaire_user
        String urlParameters = MyJSONApi.retrieveJSONObjectFromFile(GestionnaireDePort_Srv_Port2.getPath()+"cert_gest_port2.json").toString();
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
            MyJSONApi.writeToJSONFile(cert_backoffice, GestionnaireDePort_Srv_Port2.getPath()+"cert_back_office.json");

        } catch(IOException e) {
            System.out.println("Error |"+e+"| à l'envoie au serveur back-office at ");
            System.exit(-1);
        }
    }
    // Getters & Setters
    public static String getPath() {
        return path;
    }
    public static String getNomPort() {
        return nomPort;
    }
    public static Parking_Port getParking() {
        return new Parking_Port(parking);
    }
    public static void setParking(Parking_Port parking) {
        GestionnaireDePort_Srv_Port2.parking = new Parking_Port(parking);
    }


}