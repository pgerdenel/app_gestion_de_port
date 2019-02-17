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
 *  - en attente de réponse du gestionnaire de port sur le port 2082
 */

public class Ajax_Get_InfosFreePlace_Handler implements HttpHandler {

    public void handle(HttpExchange t) {

        // Utilisation d'un flux pour lire les donnees du message Http
        String response = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(t.getRequestBody(),StandardCharsets.UTF_8));

            // Variables de traitement
            String query = br.readLine(); // on récupère l'objet User au format String Json
            Integer port = BackOffice_Srv.getMap_port().get(query); // Récupération de l'IP et du port du port correspondant au nom port

            System.out.println("> Ajax_Get_InfosFreePlace_Handler: nom de port recue du portail= "+query);
            System.out.println("\t> Le port "+query+" est joignable sur le port_number "+port);

            /* UTILISATION des sockets UDP pour communication avec le gestionnaire de port
             * -Récupération de l'IP et du port du port correspondant au nom port
             * -Creation d'un Message_GestPort pour envoie de l'id_traitements et des données(le port)
             * -Socket cliente pour envoie du message par datagrampacket
             * -Socket serveur pour réception de la réponse
             */

            // Creation d'un JSONObject Message_GestPort pour envoie de l'id_traitements et des données(le port)
            JSONObject jo_message_gest_port = new JSONObject();
            JSONObject jo_data = new JSONObject();
            MyJSONApi.ajouterValeur(jo_data, "nom_port", query);
            MyJSONApi.ajouterValeur(jo_message_gest_port, "id", "2");
            MyJSONApi.ajouterValeur(jo_message_gest_port, "data", jo_data);


            System.out.println("jo_message_gest_port "+jo_message_gest_port);

            // Socket cliente pour envoie du message par datagrampacket
            // Création de la socket
            DatagramSocket socket = null;
            boolean isRequestEnded = false;
            boolean isRequest_sended  = false;
            boolean isResponse_received = false;

            while(!isRequestEnded) {

                while (!isRequest_sended) {

                    System.out.println("\t> Envoie du message au gestionnaire de port "+jo_message_gest_port.toString());

                    /* INIT UDP_SOCKET_CLIENT *************************************************************************/
                    try {
                        socket = new DatagramSocket();
                        System.out.println("\t> Socket Client created");

                    } catch (SocketException e) {
                        System.err.println("\t> Erreur lors de la creation de la socket : " + e);
                        System.exit(-1);
                    }

                    // Transformation en tableau d'octets
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    try {
                        ObjectOutputStream oos = new ObjectOutputStream(baos);
                        oos.writeObject(jo_message_gest_port.toString());
                    } catch (IOException e) {
                        System.err.println("\t> Erreur lors de la sérialisation : " + e);
                        System.exit(-1);
                    }

                    // Création et envoi du segment UDP
                    try {
                        byte[] donnees = baos.toByteArray();
                        InetAddress adresse = InetAddress.getByName("localhost");
                        DatagramPacket msg = new DatagramPacket(donnees, donnees.length, adresse, port);
                        socket.send(msg);
                        System.out.println("\t> message sended");
                        isRequest_sended = true;
                        socket.close();

                    } catch (UnknownHostException e) {
                        System.err.println("\t> Erreur lors de la création de l'adresse : " + e);
                        System.exit(-1);
                    } catch (IOException e) {
                        System.err.println("\t> Erreur lors de l'envoi du message : " + e);
                        System.exit(-1);
                    } finally {
                        System.out.println("\t> socket closed");
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
                        socket = new DatagramSocket(2082);
                        System.out.println("\t> Socket listen on " + "localhost" + ":" + 2082);

                    } catch (SocketException e) {
                        System.err.println("\t> Erreur lors de la creation de la socket : " + e);
                        System.exit(-1);
                    }

                    // Lecture du message du serveur
                    DatagramPacket msgRecu = null;
                    try {
                        byte[] tampon = new byte[16384];
                        msgRecu = new DatagramPacket(tampon, tampon.length);
                        socket.receive(msgRecu);
                        System.out.println("\t> Reponse recu du gestionnaire de port (thread treatment)");
                    } catch (IOException e) {
                        System.err.println("\t> Erreur lors de la réception du message : " + e);
                        System.exit(-1);
                    }

                    // Récupération de la réponse du gestionnaire de port
                    try {
                        ByteArrayInputStream bais = new ByteArrayInputStream(msgRecu.getData());
                        ObjectInputStream ois = new ObjectInputStream(bais);

                        // On récupère l' objet
                        response = (String) ois.readObject();

                        isResponse_received = true;
                        isRequestEnded = true;

                        ois.close();
                        bais.close();
                        socket.close();

                    } catch (ClassNotFoundException e) {
                        System.err.println("\t> Objet reçu non reconnu : " + e);
                        System.exit(-1);
                    } catch (IOException e) {
                        System.err.println("\t> Erreur lors de la recuperation de l'objet : " + e);
                        System.exit(-1);
                    }
                }
            }

        }
        catch(IOException io) {

            System.err.println("\t> Error IOException "+io);
            System.exit(-1);
        }

        /* ENVOIE de la réponse à l'origine de la requête (içi le C1_portail) */
        System.out.println("\t> Envoie de la reponse au portail...");
        // Envoi de l'en-tete Http
        try {
            // System.out.println("Envoi de l'en-tete Http");
            Headers h = t.getResponseHeaders();
            h.set("Content-Type", "text/html; charset=utf-8");
            t.sendResponseHeaders(200, response.getBytes().length);
        } catch(IOException e) {
            System.err.println("\t> Erreur lors de l'envoi de l'en-tete : " + e);
            System.exit(-1);
        }
        // Envoi du corps (donnees HTML)
        try {
            // System.out.println("Envoi du corps (donnees HTML)");
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
            System.out.println("\t> Response envoyee au portail\n");
        } catch(IOException e) {
            System.err.println("\t> Erreur lors de l'envoi du corps : " + e+"\n");
        }
    }
}
