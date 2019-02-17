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
 *  reçoit le nom du port concernant la requête
 *  fait suivre la requête au gestionnaire de port
 *  renvoie la réponse du gestionnaire de port au portail
 *  - envoie de la requête au gestionnaire de port sur le port 2091
 *  - en attente de réponse du gestionnaire de port sur le port 2080
 */

public class Ajax_Get_PlacePrice_Handler implements HttpHandler {

    public void handle(HttpExchange t) {

        // Utilisation d'un flux pour lire les donnees du message Http
        String response = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(t.getRequestBody(),StandardCharsets.UTF_8));

            // Variables de traitement
            String query = br.readLine(); // on récupère l'objet User au format String Json
            Integer port = BackOffice_Srv.getMap_port().get(query); // Récupération de l'IP et du port du port correspondant au nom port

            System.out.println("\t> Le port "+query+" est joignable sur le port_number "+port);
            System.out.println("\nAjax_Get_PlacePrice_Handler: nom de port recue du portail= "+query);

            /* UTILISATION des sockets UDP pour communication avec le gestionnaire de port
             * -Récupération de l'IP et du port du port correspondant au nom port
             * -Creation d'un Message_GestPort pour envoie de l'id_traitements et des données(le port)
             * -Socket cliente pour envoie du message par datagrampacket
             * -Socket serveur pour réception de la réponse
             */

            // Creation d'un Message_GestPort pour envoie de l'id_traitements et des données(le port)
            JSONObject jo_message_gest_port = new JSONObject();
            JSONObject jo_recu = new JSONObject();
            MyJSONApi.ajouterValeur(jo_recu, "nom_port", query);
            MyJSONApi.ajouterValeur(jo_message_gest_port, "id", "1");
            MyJSONApi.ajouterValeur(jo_message_gest_port, "data", jo_recu);

            // System.out.println("jo_message_gest_port "+jo_message_gest_port.toString(1));

            // Socket cliente pour envoie du message par datagrampacket
            // Création de la socket
            DatagramSocket socket = null;
            boolean isRequestEnded = false;
            boolean isRequest_sended  = false;
            boolean isResponse_received = false;

            while(!isRequestEnded) {

                while (!isRequest_sended) {

                    System.out.println("\n> Envoie du message au gestionnaire de port "+jo_message_gest_port.toString());

                    /* INIT UDP_SOCKET_CLIENT *************************************************************************/
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
                        DatagramPacket msg = new DatagramPacket(donnees, donnees.length, adresse, port);
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

                    /*// on attend 30 seconds entre chaque envoie
                    try {
                        TimeUnit.SECONDS.sleep(5);
                    } catch (InterruptedException ie) {
                        System.err.println("Erreur lors de l'attente imposé entre chaque envoie : " + ie);
                        System.exit(-1);
                    }*/
                }
                // Socket serveur pour réception de la réponse
                while (!isResponse_received) {

                    System.out.println("\t> En attente de la reponse du gestionnaire de port ....");

                    /* INIT UDP_SOCKET_SERVEUR ***************************************************************************/
                    try {
                        socket = new DatagramSocket(2081);
                        System.out.println("Socket listen on " + "localhost" + ":" + 2081);

                    } catch (SocketException e) {
                        System.err.println("Erreur lors de la creation de la socket : " + e);
                        System.exit(-1);
                    }

                    // Lecture du message du serveur
                    DatagramPacket msgRecu = null;
                    try {
                        byte[] tampon = new byte[1024];
                        msgRecu = new DatagramPacket(tampon, tampon.length);
                        socket.receive(msgRecu);
                    } catch (IOException e) {
                        System.err.println("Erreur lors de la réception du message : " + e);
                        System.exit(-1);
                    }

                    // Récupération de la réponse du gestionnaire de port
                    try {
                        ByteArrayInputStream bais = new ByteArrayInputStream(msgRecu.getData());
                        ObjectInputStream ois = new ObjectInputStream(bais);
                        String json_response= (String) ois.readObject();
                        System.out.println("Recu : " + json_response);
                        response = json_response;
                        isResponse_received = true;
                        isRequestEnded = true;
                        socket.close();
                    } catch (ClassNotFoundException e) {
                        System.err.println("Objet reçu non reconnu : " + e);
                        System.exit(-1);
                    } catch (IOException e) {
                        System.err.println("Erreur lors de la récupération de l'objet : " + e);
                        System.exit(-1);
                    }
                }
            }

        }
        catch(IOException io) {

            System.err.println("Error IOException "+io);
            System.exit(-1);
        }
        finally {
            System.out.println();
        }

        /* ENVOIE de la réponse à l'origine de la requête (içi le C1_portail) */
        System.out.println("> Envoie de la reponse au portail...");
        // Envoi de l'en-tete Http
        try {
            // System.out.println("Envoi de l'en-tete Http");
            Headers h = t.getResponseHeaders();
            h.set("Content-Type", "text/html; charset=utf-8");
            t.sendResponseHeaders(200, response.getBytes().length);
        } catch(IOException e) {
            System.err.println("Erreur lors de l'envoi de l'en-tete : " + e);
            System.exit(-1);
        }
        // Envoi du corps (donnees HTML)
        try {
            // System.out.println("Envoi du corps (donnees HTML)");
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
            System.out.println("> Response envoyee au portail");
        } catch(IOException e) {
            System.err.println("Erreur lors de l'envoi du corps : " + e);
        }
    }
}
