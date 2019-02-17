package C3_gestion_de_port;

/*
 * Class Gestionnaire de port
 * - Gère un port particulier Port 1
 * - reçoit les requetes depuis le backoffice et demarre une socket pour la requete associe
 * - fait suivre les requetes reçues à un Thread_Port_Service
 * - PORT 2091 à 2094 pour les ports de 1 à 4
 * - 2094 pour le port 4
 */

import C3_gestion_de_port.objects.Config;
import C3_gestion_de_port.objects.port.Parking_Port;
import C3_gestion_de_port.objects.security.GenerationClesRSA;
import C3_gestion_de_port.threads_service.*;
import C3_gestion_de_port.objects.MyJSONApi;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static C3_gestion_de_port.objects.Config.fichierExiste;

/* Permet de caractériser un gestionnaire de port */


public class GestionnaireDePort_Srv_Port4 {

    public final static String path = "src\\C3_gestion_de_port\\resources\\ports\\port4\\";
    private final static String nomPort = "port4";

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
            c.ajouterValeur("port", 2094);
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
        // Recuperation de la cle publiquer et creation du certificat

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
        
        int serverPort = 2094;
        String data_parameter = "data"; // data de la requete

        try {
            // Setup socket
            socket = new DatagramSocket(serverPort);
            System.out.println("\nGestionnaireDePort_Srv Started "+socket.getLocalAddress()+":"+socket.getLocalPort()+"\n");
        }
        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
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

        StringBuilder reponse = new StringBuilder();
        HttpURLConnection con;
        StringBuilder state = null;
        URL myurl = null;

        JSONObject jo = new JSONObject();
        MyJSONApi.ajouterValeur(jo, "nom_port", nomPort);
        MyJSONApi.ajouterValeur(jo, "adresse", "localhost");
        MyJSONApi.ajouterValeur(jo, "port", 2094);
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
        GestionnaireDePort_Srv_Port4.parking = new Parking_Port(parking);
    }


}