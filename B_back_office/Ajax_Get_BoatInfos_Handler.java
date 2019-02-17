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
 *  reçoit le nom_port, nom_bateau, le nom_user et nom_emplacement
 *  fait suivre la requête au gestionnaire de port
 *  renvoie la réponse du gestionnaire de port au portail
 *  - envoie de la requête au gestionnaire de port sur le port 2091
 *  - en attente de réponse du gestionnaire de port sur le port 2083
 */
public class Ajax_Get_BoatInfos_Handler implements HttpHandler {

    public void handle(HttpExchange t) {

        // Utilisation d'un flux pour lire les donnees du message Http
        String reponse = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8));

            // Variables de traitement
            String query = br.readLine(); // on récupère l'objet User au format String Json
            JSONObject jo_recu = MyJSONApi.castinJSONObject(query); // on crée un JSONObject depuis les données reçues
            Integer port = BackOffice_Srv.getMap_port().get(MyJSONApi.getString(jo_recu, "nom_port")); // Récupération de l'IP et du port du port correspondant au nom port

            System.out.println("\t> Le port "+query+" est joignable sur le port_number "+port);

            System.out.println("\nAjax_Query_BoatInPort_Handler: donnée recue du portail= " + MyJSONApi.jsonToString(jo_recu, 1));

            /* UTILISATION des sockets UDP pour communication avec le gestionnaire de port
             * -Récupération de l'IP et du port du port correspondant au nom port
             * -Creation d'un Message_GestPort pour envoie de l'id_traitements et des données(le nomport, le nombateau, l'emplacement(num de place)
             * -Socket cliente pour envoie du message par datagrampacket
             * -Socket serveur pour réception de la réponse
             **/

            // Creation d'un JSONObject Message_GestPort pour envoie de l'id_traitements et des données(le port)
            JSONObject jo_message_gest_port = new JSONObject();
            MyJSONApi.ajouterValeur(jo_message_gest_port, "id", "3");
            MyJSONApi.ajouterValeur(jo_message_gest_port, "data", jo_recu);

            // Socket cliente pour envoie du message par datagrampacket
            // Création de la socket
            DatagramSocket socket = null;
            boolean isRequestEnded = false;
            boolean isRequest_sended = false;
            boolean isResponse_received = false;

            while (!isRequestEnded) {

                while (!isRequest_sended) {

                    System.out.println("\n> Envoie du message au gestionnaire de port " + jo_message_gest_port.toString());

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
                        DatagramPacket msg = new DatagramPacket(donnees, donnees.length, adresse, port); // port
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

                    /* INIT UDP_SOCKET_SERVEUR ************************************************************************/
                    try {
                        socket = new DatagramSocket(2083);
                        System.out.println("Socket listen on " + "localhost" + ":" + 2083);

                    }
                    catch (SocketException e) {
                        System.err.println("Erreur lors de la creation de la socket : " + e);
                        System.exit(-1);
                    }

                    System.out.println("\n> En attente de la reponse du gestionnaire de port ....");
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

                    /* Récupération de la réponse du gestionnaire de port
                     * Objet JSON { "result":"result_type" }
                     * result_type = "data" alors on fait une requête au gestionnaireutilisateur pour récupérer les informations.json du bateau
                     * result_type = "no_data" alors il n'existe aucun bateau à cette emplacement
                     */
                    try {
                        ByteArrayInputStream bais = new ByteArrayInputStream(msgRecu.getData());
                        ObjectInputStream ois = new ObjectInputStream(bais);

                        String json_response = (String) ois.readObject();
                        System.out.println("Response du gestionnaire de port: " + json_response);

                        // on la valeur de l'objet JSOn reçu pour savoir si on doit requêter les informations du bateau au gestionnaire utilisateur
                        // On construit un objet JSON depuis ce qu'on a reçu
                        JSONObject reponse1 = MyJSONApi.castinJSONObject(json_response);
                        if(MyJSONApi.getString(reponse1, "result").equals("data")) {
                            // On effectue une requête au Gestionnaire utilisateur pour récupérer les informations.json du bateau


                            /*REQUÊTE GESTIONNAIRE UTILISATEUR ********************************************************/

                            /*  Construction d'une requête HTTP
                             *  Données envoyées : nom_user, nom_bateau
                             *  Destinataire server : Gestionnaire utilisateur
                             *  Destinataire handler : Query_BoatInfos_Handler
                             */
                            JSONObject jo_query_boat_info = new JSONObject();
                            MyJSONApi.ajouterValeur(jo_query_boat_info, "nom_user", MyJSONApi.getString(jo_recu, "nom_user"));
                            MyJSONApi.ajouterValeur(jo_query_boat_info, "nom_bateau", MyJSONApi.getString(jo_recu, "nom_bateau"));

                            String urlParameters = jo_query_boat_info.toString();
                            StringBuilder reponse2;

                            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
                            URL myurl = new URL("http://localhost:9090/query_boat_infos.html");
                            try {

                                HttpURLConnection con = (HttpURLConnection) myurl.openConnection();

                                con.setDoOutput(true);
                                con.setRequestMethod("POST");
                                con.setRequestProperty("User-Agent", "Java client");
                                con.setRequestProperty("Content-Type", "application/json");

                                try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                                    wr.write(postData);
                                    System.out.println("\nAjax_Get_BoatInfos_Handler: requete envoye au gestionnaire user= " + urlParameters);
                                }

                                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {

                                    String line;
                                    reponse2 = new StringBuilder();

                                    while ((line = in.readLine()) != null) {
                                        reponse2.append(line);
                                        reponse2.append(System.lineSeparator());
                                    }
                                }
                                System.out.println("\nAjax_Get_BoatInfos_Handler: reponse recue du gestionnaire user= " + reponse2);

                                // System.out.println("content "+content.toString());
                                reponse = reponse2.toString();
                            }
                            catch(ConnectException ce) {
                                System.out.println("Error |"+ce+"| au serveur GestionUtilisateurs at "+myurl);
                                System.exit(-1);
                            }


                            /* FIN REQUÊTE GESTIONNAIRE UTILISATEUR ****************************************/

                        }
                        else {
                            // comme l'on a pas besoin de récupéré les infos.json du bateau, on fait simplement suivre le message avec result="no_data"
                            reponse = json_response;
                        }



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
