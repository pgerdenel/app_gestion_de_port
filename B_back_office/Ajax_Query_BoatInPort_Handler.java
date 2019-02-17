package B_back_office;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import B_back_office.objects.MyJSONApi;
import org.json.JSONObject;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

/**
 *  reçoit le nom_port, nom_bateau, et nom_emplacement
 *  fait suivre la requête au gestionnaire de port
 *  renvoie la réponse du gestionnaire de port au portail
 *  - envoie de la requête au gestionnaire de port sur le port 2091
 *  - en attente de réponse du gestionnaire de port sur le port 2084
 */

public class Ajax_Query_BoatInPort_Handler implements HttpHandler {

    public void handle(HttpExchange t) {

        // Utilisation d'un flux pour lire les donnees du message Http
        String reponse = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8));

            // Variables de traitement
            String query = br.readLine(); // on récupère les données au format String Json
            JSONObject jo_recu = MyJSONApi.castinJSONObject(query); // on crée un JSONObject depuis les données reçues
            Integer port = BackOffice_Srv.getMap_port().get(MyJSONApi.getString(jo_recu, "nom_port")); // Récupération de l'IP et du port du port correspondant au nom port


            System.out.println("\nAjax_Query_BoatInPort_Handler: donnée recue du portail= " + MyJSONApi.jsonToString(jo_recu, 1));
            System.out.println("\t> Le port "+query+" est joignable sur le port_number "+port);

            // Creation d'un JSONObject Message_GestPort pour envoie de l'id_traitements et des données(le port)
            JSONObject jo_message_gest_port = new JSONObject();
            MyJSONApi.ajouterValeur(jo_message_gest_port, "id", "4");
            MyJSONApi.ajouterValeur(jo_message_gest_port, "data", jo_recu);

            /* UTILISATION des sockets UDP pour communication avec le gestionnaire de port
             * -Récupération de l'IP et du port du port correspondant au nom port
             * -Creation d'un Message_GestPort pour envoie de l'id_traitements et des données(le nomport, le nombateau, l'emplacement(num de place)
             * -Socket cliente pour envoie du message par datagrampacket
             * -Socket serveur pour réception de la réponse
             */

            // Socket cliente pour envoie du message par datagrampacket
            // Création de la socket
            DatagramSocket socket = null;
            boolean isRequestEnded = false;
            boolean isRequest_sended = false;
            boolean isResponse_received = false;

            while (!isRequestEnded) {

                while (!isRequest_sended) {

                    System.out.println("\n> Envoie du message au gestionnaire de port " + jo_message_gest_port.toString());

                    /* INIT UDP_SOCKET_CLIENT ******************************************************************/
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
                        oos.writeObject(jo_message_gest_port.toString());
                    } catch (IOException e) {
                        System.err.println("Erreur lors de la sérialisation : " + e);
                        System.exit(-1);
                    }

                    // Création et envoi du segment UDP
                    try {
                        byte[] donnees = baos.toByteArray();
                        InetAddress adresse = InetAddress.getByName("localhost");
                        DatagramPacket msg = new DatagramPacket(donnees, donnees.length, adresse, port); // 2091
                        socket.send(msg);
                        System.out.println("message sended");
                        isRequest_sended = true;
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
                while (!isResponse_received) {

                    System.out.println("\t> En attente de la reponse du gestionnaire de port ....");

                    /* INIT UDP_SOCKET_SERVEUR ******************************************************************/
                    try {
                        socket = new DatagramSocket(2084);
                        System.out.println("Socket listen on " + "localhost" + ":" + 2084);

                    }
                    catch (SocketException e) {
                        System.err.println("Erreur lors de la creation de la socket : " + e);
                        System.exit(-1);
                    }

                    // Lecture du message du serveur
                    DatagramPacket msgRecu = null;
                    try {
                        byte[] tampon = new byte[32];
                        msgRecu = new DatagramPacket(tampon, tampon.length);
                        socket.receive(msgRecu);
                    }
                    catch (IOException e) {
                        System.err.println("Erreur lors de la reception du message : " + e);
                        System.exit(-1);
                    }

                    // Récupération de la réponse du gestionnaire de port
                    try {
                        ByteArrayInputStream bais = new ByteArrayInputStream(msgRecu.getData());
                        ObjectInputStream ois = new ObjectInputStream(bais);

                        // on récupère la réponse
                        reponse = (String) ois.readObject();
                        System.out.println("Recu : " + reponse);

                        isResponse_received = true;
                        isRequestEnded = true;
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
