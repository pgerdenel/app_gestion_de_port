package B_back_office;

import B_back_office.objects.MyJSONApi;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

/**
 *  Permet de faire suivre la requête de transfert d'un bateau d'un port à un autre
 *  reçoit le nom_port, le nom_bateau, le num_place, le nom_portdest, la longueur
 *  Envoie une requête à chacun des ports concernés (nom_port et nom_portdest)
 *  Chaque requête contiendra un MessageGestPort avec une valeur dans l'objet JSON qui informera le port de se mettre en écoute ou d'envoyer les données
 */

public class Ajax_Transfer_BoatAtPort_Handler implements HttpHandler {

    public void handle(HttpExchange t) {

        // Utilisation d'un flux pour lire les donnees du message Http
        String reponse = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8));

            // Variables de traitement
            String query = br.readLine(); // on récupère l'objet User au format String Json
            JSONObject jo_recu = MyJSONApi.castinJSONObject(query); // on crée un JSONObject depuis les données reçues

            Integer port_number_src = BackOffice_Srv.getMap_port().get(MyJSONApi.getString(jo_recu, "nom_port")); // Récupération de l'IP et du port_number du nom_port_src
            Integer port_number_dest = BackOffice_Srv.getMap_port().get(MyJSONApi.getString(jo_recu, "nom_portdest")); // Récupération de l'IP et du port_number du nom_port_dest

            System.out.println("\nAjax_Transfer_BoatAtPort_Handler: donnée recue du portail= " + MyJSONApi.jsonToString(jo_recu, 1));

            System.out.println("\t> Le port source "+MyJSONApi.getString(jo_recu, "nom_port")+" est joignable sur le port_number "+port_number_src);
            System.out.println("\t> Le port de destination "+MyJSONApi.getString(jo_recu, "nom_portdest")+" est joignable sur le port_number "+port_number_dest);

            /* UTILISATION des sockets UDP pour communication avec le gestionnaire de port
             * -Récupération de l'IP et du port du port correspondant au nom port
             * -Creation d'un Message_GestPort pour envoie de l'id_traitements et des données(le nomport, le nombateau, l'emplacement(num de place)
             * -Socket cliente pour envoie du message par datagrampacket
             * -Socket serveur pour réception de la réponse
             */

            // ETAPE D'ENVOI
            // Creation de 2 Message_GestPort pour envoie de l'id_traitements et des données à chacun des ports

            // Message_GestPort pour le port source (qui envoie le bateau)
            // on crée un json object avec les informations qu'il a besoin
            JSONObject jo_port_src = new JSONObject();
            MyJSONApi.ajouterValeur(jo_port_src, "nom_port", MyJSONApi.getString(jo_recu, "nom_port")); // nécessaire pour que l'id traitement puisse savoir sur quel port lancer le traitement
            MyJSONApi.ajouterValeur(jo_port_src, "nom_port_dest", MyJSONApi.getString(jo_recu, "nom_portdest")); // nécessaire pour que l'id traitement puisse savoir sur quel port et port_number faire sa socket connecte et envoyer le nom_bateau
            MyJSONApi.ajouterValeur(jo_port_src, "nom_bateau", MyJSONApi.getString(jo_recu, "nom_bateau"));
            MyJSONApi.ajouterValeur(jo_port_src, "longueur", MyJSONApi.getString(jo_recu, "longueur"));
            MyJSONApi.ajouterValeur(jo_port_src, "num_place", MyJSONApi.getString(jo_recu, "num_place"));
            MyJSONApi.ajouterValeur(jo_port_src, "state", "src");
            JSONObject jo_message_port_src = new JSONObject();
            MyJSONApi.ajouterValeur(jo_message_port_src, "id", "7");
            MyJSONApi.ajouterValeur(jo_message_port_src, "data", jo_port_src);

            // Message_GestPort pour le port source (qui reçoit le bateau)
            // on crée un json object avec les informations qu'il a besoin
            JSONObject jo_port_dest = new JSONObject();
            MyJSONApi.ajouterValeur(jo_port_dest, "nom_port", MyJSONApi.getString(jo_recu, "nom_portdest")); // nécessaire pour que l'id traitement puisse savoir sur quel port lancer le traitement
            MyJSONApi.ajouterValeur(jo_port_dest, "nom_port_dest", MyJSONApi.getString(jo_recu, "nom_port"));// nécessaire pour que l'id traitement puisse savoir sur quel port et port_number faire sa socket connecte et recevoir le nom_bateau
            MyJSONApi.ajouterValeur(jo_port_dest, "nom_bateau", MyJSONApi.getString(jo_recu, "nom_bateau"));
            MyJSONApi.ajouterValeur(jo_port_dest, "longueur", MyJSONApi.getString(jo_recu, "longueur"));
            MyJSONApi.ajouterValeur(jo_port_dest, "num_place", MyJSONApi.getString(jo_recu, "num_place"));
            MyJSONApi.ajouterValeur(jo_port_dest, "state", "dest");
            JSONObject jo_message_port_dest = new JSONObject();
            MyJSONApi.ajouterValeur(jo_message_port_dest, "id", "7");
            MyJSONApi.ajouterValeur(jo_message_port_dest, "data", jo_port_dest);

            // Socket cliente pour envoie du message par datagrampacket
            // Création de la socket
            DatagramSocket socket = null;
            boolean isRequestEnded = false;  // quand l'échange est finit
            boolean isRequests_sended = false; // quand les 2 requêtes ont été envoyés au port src et au port dest
            boolean isResponses_received  =false; // quand les 2 réponses des 2 port sont reçues
            boolean isResponse_src_received = false; // quand réponse du port src reçue
            boolean isResponse_dest_received = false; // quand réponse du port dest reçue
            JSONObject reponse_all = new JSONObject();
            Boolean result_src = null;
            Boolean result_dest = null;

            while (!isRequestEnded) {

                while (!isRequests_sended) {

                    System.out.println("\n> Envoie du message au gestionnaire de port "+MyJSONApi.getString(jo_recu, "nom_port")+" : " + jo_message_port_src.toString());
                    System.out.println("\n> Envoie du message au gestionnaire de port "+MyJSONApi.getString(jo_recu, "nom_portdest")+" : " + jo_message_port_dest.toString());

                    /* INIT UDP_SOCKET_CLIENT ******************************************************************/
                    try {
                        socket = new DatagramSocket();
                        System.out.println("Socket Client created");

                    } catch (SocketException e) {
                        System.err.println("Erreur lors de la creation de la socket : " + e);
                        System.exit(-1);
                    }

                    // Transformation du message_src en tableau d'octets
                    ByteArrayOutputStream baos_src = new ByteArrayOutputStream();
                    try {
                        ObjectOutputStream oos_src = new ObjectOutputStream(baos_src);
                        oos_src.writeObject(jo_message_port_src.toString());
                    } catch (IOException e) {
                        System.err.println("Erreur lors de la sérialisation : " + e);
                        System.exit(-1);
                    }
                    // Transformation du message_dest en tableau d'octets
                    ByteArrayOutputStream baos_dest = new ByteArrayOutputStream();
                    try {
                        ObjectOutputStream oos_dest = new ObjectOutputStream(baos_dest);
                        oos_dest.writeObject(jo_message_port_dest.toString());
                    } catch (IOException e) {
                        System.err.println("Erreur lors de la sérialisation : " + e);
                        System.exit(-1);
                    }

                    // Création et envoi du segment UDP
                    try {
                        byte[] donnees_src = baos_src.toByteArray();
                        byte[] donnees_dest = baos_dest.toByteArray();

                        InetAddress adresse = InetAddress.getByName("localhost");
                        DatagramPacket msg_src = new DatagramPacket(donnees_src, donnees_src.length, adresse, port_number_src);
                        DatagramPacket msg_dest = new DatagramPacket(donnees_dest, donnees_dest.length, adresse, port_number_dest);

                        socket.send(msg_src);
                        System.out.println("message src sended");
                        socket.send(msg_dest);
                        System.out.println("message dest sended");

                        isRequests_sended = true; // les 2 requêtes ont été envoyées

                        socket.close();

                    } catch (UnknownHostException e) {
                        System.err.println("Erreur lors de la création de l'adresse : " + e);
                        System.exit(-1);
                    } catch (IOException e) {
                        System.err.println("Erreur lors de l'envoi du message : " + e);
                        System.exit(-1);
                    } finally {
                        System.out.println("socket closed");
                        socket.close();
                    }
                }
                // Socket serveur pour réception de la réponse
                while (!isResponses_received) {

                    System.out.println("\n> En attente de la reponse des gestionnaires de port ....");

                    /* INIT UDP_SOCKET_SERVEUR ******************************************************************/
                    try {
                        socket = new DatagramSocket(2087);
                        System.out.println("Socket listen on " + "localhost" + ":" + 2087);

                    }
                    catch (SocketException e) {
                        System.err.println("Erreur lors de la creation de la socket : " + e);
                        System.exit(-1);
                    }

                    // Lecture du message du serveur
                    DatagramPacket msgRecu = null;
                    try {
                        byte[] tampon = new byte[4096];
                        msgRecu = new DatagramPacket(tampon, tampon.length);
                        socket.receive(msgRecu);
                    }
                    catch (IOException e) {
                        System.err.println("Erreur lors de la reception du message : " + e);
                        System.exit(-1);
                    }

                    /* Récupération de la réponse des gestionnaires de port
                     * Objet JSON SRC { "result_src":"true or false", "result_dest":""  }
                     * Objet JSON DEST { "result_src":"", "result_dest":"true or false"  }
                     */
                    try {
                        ByteArrayInputStream bais = new ByteArrayInputStream(msgRecu.getData());
                        ObjectInputStream ois = new ObjectInputStream(bais);

                        String json_response = (String) ois.readObject();
                        System.out.println("Response d'un gestionnaire de port: " + json_response);

                        // on regarde de quel gestionnaire il s'agit
                        // on construit un objet JSON depuis ce qu'on a reçu
                        JSONObject reponse1 = MyJSONApi.castinJSONObject(json_response);

                        /* DEBUT Traitement Réponse source *******************/
                        switch(MyJSONApi.getString(reponse1, "result_src")) {
                            case "true":
                                isResponse_src_received = true;
                                break;
                            case "false":
                                isResponse_src_received = false;
                                break;
                        }
                        System.out.println("isResponse_src_received = "+isResponse_src_received);
                        if(MyJSONApi.getString(reponse1, "result_dest").equals("")) {
                            result_src = Boolean.valueOf(MyJSONApi.getString(reponse1, "result_src"));
                        }
                        /* FIN Traitement Réponse source *******************/

                        /* DEBUT Traitement Réponse destination *******************/
                        switch(MyJSONApi.getString(reponse1, "result_dest")) {
                            case "true":
                                isResponse_dest_received = true;
                                break;
                            case "false":
                                isResponse_dest_received = false;
                                break;
                        }
                        System.out.println("isResponse_dest_received = "+isResponse_dest_received);
                        if(MyJSONApi.getString(reponse1, "result_src").equals("")) {
                            result_dest = Boolean.valueOf(MyJSONApi.getString(reponse1, "result_dest"));
                        }
                        /* FIN Traitement Réponse destination *******************/

                        if(isResponse_src_received && isResponse_dest_received) {

                            /*System.out.println("DEBUGGGGGGGGG");
                            System.out.println("result_src = "+result_src);
                            System.out.println("result_dest = "+result_dest);
                            System.out.println("DEBUGGGGGGGGG");*/

                            if(result_src && result_dest) {
                                MyJSONApi.ajouterValeur(reponse_all, "result", true);
                                System.out.println("Le transfert s'est bien passe: SUCCESS");
                            }
                            else {
                                MyJSONApi.ajouterValeur(reponse_all, "result", false);
                                System.out.println("Le transfert a rencontré une erreur: FAILURE");
                            }
                            isResponses_received = true; // les 2 réponses ont été reçues
                            reponse = reponse_all.toString();
                            isRequestEnded = true;
                        }
                        System.out.println("socket closed");
                        socket.close();

                    }
                    catch(ClassNotFoundException cnfe) {
                        System.err.println("Erreur lors du cast de l'objet avec class inconnue: " + cnfe);
                        System.exit(-1);
                    }
                    catch (IOException e) {
                        System.err.println("Erreur lors de la recuperation de l'objet : " + e);
                        System.exit(-1);
                    }
                }
            }

        }
        catch (IOException io) {
            System.err.println("Error IOException " + io);
            System.exit(-1);
        }
        finally {
            System.out.println();
        }

        //ENVOIE de la réponse à l'origine de la requête (içi le C1_portail)
        System.out.println("> Envoie de la reponse au portail...");
        // Envoi de l'en-tete Http
        try {
            // System.out.println("Envoi de l'en-tete Http");
            Headers h = t.getResponseHeaders();
            h.set("Content-Type", "text/html; charset=utf-8");
            t.sendResponseHeaders(200, reponse.getBytes().length);
        } catch (IOException e) {
            System.err.println("Erreur lors de l'envoi de l'en-tete : " + e);
            System.exit(-1);
        }
        // Envoi du corps (donnees HTML)
        try {
            // System.out.println("Envoi du corps (donnees HTML)");
            OutputStream os = t.getResponseBody();
            os.write(reponse.getBytes());
            os.close();
            System.out.println("> Response envoyee au portail");
        } catch (IOException e) {
            System.err.println("Erreur lors de l'envoi du corps : " + e);
        }
    }
}
