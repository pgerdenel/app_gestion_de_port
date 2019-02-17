package C3_gestion_de_port.threads_service;

import C3_gestion_de_port.objects.MyJSONApi;
import C3_gestion_de_port.objects.port.Parking_Port;
import C3_gestion_de_port.objects.port.Place;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class Thread_Transfer_BoatToPort implements Runnable {

    public static void main(String args[]) {

    }

    private JSONObject data_parameter;

    // Constructor
    public Thread_Transfer_BoatToPort(String data_parameter) {
        try {
            this.data_parameter = new JSONObject(data_parameter);
        }
        catch(JSONException je) {
            System.out.println("\tThread_Transfer_BoatToPort() constructor error "+je);
        }
    }

    @Override
    public void run() {

        System.out.println("\t> Thread_Transfer_BoatToPort RUN() called with data parameter = "+data_parameter);

        DatagramSocket socket2 = null;

        /* INIT UDP_SOCKET_CLIENT ****************************************************************************/
        try {
            socket2 = new DatagramSocket();
            //System.out.println("\tSocket Client created");

        } catch(SocketException e) {
            System.err.println("\tErreur lors de la creation de la socket : " + e);
            System.exit(-1);
        }

        // Transformation en tableau d'octets
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            String nom_port = MyJSONApi.getString(data_parameter, "nom_port"); // port pour qui est destiné ce message
            String nom_port_dest = MyJSONApi.getString(data_parameter, "nom_port_dest"); // port de destination
            String state = MyJSONApi.getString(data_parameter, "state");
            String nom_bateau = MyJSONApi.getString(data_parameter, "nom_bateau");
            int longueur = MyJSONApi.getInt(data_parameter, "longueur");
            int num_place  = MyJSONApi.getInt(data_parameter, "num_place");


            System.out.println("src= "+nom_port);
            JSONObject jo_src = new JSONObject();
            JSONObject jo_dest = new JSONObject();

            switch (state) {
                case "src": { // port1 se connectera à nomport_dest et enverra
                    System.out.println(nom_port + " demarre un socket et se connectera a " + nom_port_dest + " pour envoyer le bateau");
                /*Method method_getParking = null;
                Method method_setParking = null;
                Object i_port = null;*/
                    Parking_Port pp_tmp = null;
                    try {
                        // On vérifie que le bateau est présent dans le port
                        System.out.println("on supprime avec la réflexivité !!!");
                        // on récupère la class
                        Class<?> port = Class.forName("C3_gestion_de_port.GestionnaireDePort_Srv_Port" + nom_port.charAt(nom_port.length() - 1));
                        Class<?> parking = Class.forName("C3_gestion_de_port.objects.port.Parking_Port");
                        // on récupère les methodes : getParking, setParking, writeOnlyPortInnJsonFile
                        Method method_getParking = port.getDeclaredMethod("getParking");
                        Method method_setParking = port.getDeclaredMethod("setParking", Parking_Port.class);
                        // on récupère une nouvelle instance de la classe
                        Object i_port = port.newInstance();
                        // on appelle la méthode
                        pp_tmp = (Parking_Port) method_getParking.invoke(i_port);
                    } catch (ClassNotFoundException | InstantiationException | NoSuchMethodException | IllegalAccessException e) {
                        System.out.println("error " + e);
                    } catch (InvocationTargetException e) {
                        System.out.println("error " + e.getTargetException());
                    }

                    /* CREATION DE LA SOCKET CONNECTE : ENVOIE DU NOM DE BATEAU ***********************/
                    final int portEcoute = 3000;
                    // Création de la socket
                    Socket socket = null;
                    try {
                        socket = new Socket("localhost", portEcoute);
                    } catch (UnknownHostException e) {
                        System.err.println("Erreur sur l'hôte : " + e);
                        System.exit(-1);
                    } catch (IOException e) {
                        System.err.println("Création de la socket impossible : " + e);
                        System.exit(-1);
                    }

                    // Association d'un flux d'entrée et de sortie
                    BufferedReader input = null;
                    PrintWriter output = null;
                    try {
                        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                    } catch (IOException e) {
                        System.err.println("Association des flux impossible : " + e);
                        System.exit(-1);
                    }

                    // Envoi des informations du bateau
                    JSONObject json_bateau_data = new JSONObject();
                    MyJSONApi.ajouterValeur(json_bateau_data, "nom_bateau", nom_bateau);
                    MyJSONApi.ajouterValeur(json_bateau_data, "longueur", longueur);
                    MyJSONApi.ajouterValeur(json_bateau_data, "num_place", num_place);
                    System.out.println("Envoi: " + json_bateau_data);
                    output.println(json_bateau_data);

                    // Lecture de l'état de l'ajout du bateau
                    String message = "";
                    try {
                        message = input.readLine();
                        System.out.println("Etat de l'ajout du bateau : " + message);
                        if (message.equals("true")) {
                            // on supprime le bateau de longueur et à l'emplacement dans ce port
                            pp_tmp.removeBateauAtPlace(longueur, num_place);
                            MyJSONApi.ajouterValeur(jo_src, "result_src", "true"); // Boolean b normalement pas true
                            MyJSONApi.ajouterValeur(jo_src, "result_dest", ""); // pr palier errror JSON
                            System.out.println("transfert from source SUCCESS");
                        } else {
                            MyJSONApi.ajouterValeur(jo_src, "result_src", "false"); // Boolean b normalement pas true
                            MyJSONApi.ajouterValeur(jo_src, "result_dest", ""); // pr palier errror JSON
                            System.out.println("transfert from source FAILURE");
                        }

                    } catch (IOException e) {
                        System.err.println("Erreur lors de la lecture : " + e);
                        System.exit(-1);
                    }

                    // Envoi de 'end' pour finir l'échange
                    message = "end";
                    System.out.println("Envoi: " + message);
                    output.println(message);

                    // Lecture de 'end' pour finir l'échange
                    try {
                        message = input.readLine();
                    } catch (IOException e) {
                        System.err.println("Erreur lors de la lecture : " + e);
                        System.exit(-1);
                    }
                    System.out.println("Fin echange : " + message);

                    // Fermeture des flux et de la socket
                    try {
                        System.out.println(pp_tmp.getMap_place().get(-6).toString());
                        input.close();
                        output.close();
                        socket.close();
                    } catch (IOException e) {
                        System.err.println("Erreur lors de la fermeture des flux et de la socket : " + e);
                        System.exit(-1);
                    }

                    /* FIN DE LA SOCKET CONNECTE ******************************************************/
                    // envoie de la réponse
                    oos.writeObject(jo_src.toString()); // envoie de la réponse du port source


                    final byte[] utf8Bytes_src = jo_src.toString().getBytes(StandardCharsets.UTF_8);
                    System.out.println("\tResult src -> treated & sended | size = " + utf8Bytes_src.length);
                    break;
                }
                case "dest": { // port1 se mettra en écoute des connexions de nomport_src et recevra
                    System.out.println(nom_port + " se mettra en ecoute des connexions de " + nom_port_dest + " et recevra le bateau");

                /*Method method_getParking = null;
                Method method_setParking = null;
                Object i_port = null;*/
                    Parking_Port pp_tmp = null;
                    try {
                        // On vérifie que le bateau est présent dans le port
                        System.out.println("on supprime avec la réflexivité !!!");
                        // on récupère la class
                        Class<?> port = Class.forName("C3_gestion_de_port.GestionnaireDePort_Srv_Port" + nom_port.charAt(nom_port.length() - 1));
                        Class<?> parking = Class.forName("C3_gestion_de_port.objects.port.Parking_Port");
                        // on récupère les methodes : getParking, setParking, writeOnlyPortInnJsonFile
                        Method method_getParking = port.getDeclaredMethod("getParking");
                        Method method_setParking = port.getDeclaredMethod("setParking", Parking_Port.class);
                        // on récupère une nouvelle instance de la classe
                        Object i_port = port.newInstance();
                        // on appelle la méthode
                        pp_tmp = (Parking_Port) method_getParking.invoke(i_port);
                    } catch (ClassNotFoundException | InstantiationException | NoSuchMethodException | IllegalAccessException e) {
                        System.out.println("error " + e);
                    } catch (InvocationTargetException e) {
                        System.out.println("error " + e.getTargetException());
                    }

                    /* CREATION DE LA SOCKET CONNECTE : RECEPTION DU BATEAU ***************************/
                    final int portEcoute = 3000;

                    // Création de la socket serveur
                    ServerSocket socketServeur = null;
                    try {
                        socketServeur = new ServerSocket(portEcoute);
                    } catch (IOException e) {
                        System.err.println("Création de la socket impossible : " + e);
                        System.exit(-1);
                    }

                    // Attente d'une connexion d'un client
                    Socket socketClient = null;
                    try {
                        socketClient = socketServeur.accept();
                    } catch (IOException e) {
                        System.err.println("Erreur lors de l'attente d'une connexion : " + e);
                        System.exit(-1);
                    }

                    // Association d'un flux d'entrée et de sortie
                    BufferedReader input = null;
                    PrintWriter output = null;
                    try {
                        input = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
                        output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream())), true);
                    } catch (IOException e) {
                        System.err.println("Association des flux impossible : " + e);
                        System.exit(-1);
                    }

                    // Lecture de l'objet JSON et ajout du bateau
                    String message = "";
                    String state_ajout = "";
                    JSONObject json_bateau_data;
                    try {
                        //json_bateau_data = new JSONObject(input.readLine());
                        json_bateau_data = MyJSONApi.castinJSONObject(input.readLine());
                        System.out.println("data_bateau Lu: " + json_bateau_data);
                        // on place le bateau dans le ParkingPort
                        // on récupère la place par default précédente
                        Place p = new Place(pp_tmp.getMap_place().get(MyJSONApi.getInt(json_bateau_data, "longueur")).get((MyJSONApi.getInt(json_bateau_data, "num_place") - 1)));
                        // si la place est libre
                        if (p.isEmpty()) {
                            // on assigne la place comme place occupée
                            p.setNom_bateau(MyJSONApi.getString(json_bateau_data, "nom_bateau"));
                            p.setEmpty(false);
                            // on met le nouveau bateau dedans
                            pp_tmp.getMap_place().get(MyJSONApi.getInt(json_bateau_data, "longueur")).set((MyJSONApi.getInt(json_bateau_data, "num_place") - 1), new Place(p));
                            state_ajout = "true";
                            MyJSONApi.ajouterValeur(jo_dest, "result_dest", "true");
                            MyJSONApi.ajouterValeur(jo_dest, "result_src", ""); // pr palier errror JSON
                            System.out.println("transfert from destination SUCCESS");
                        } else {
                            // le bateau ne peut pas être mis car la place est occupée
                            state_ajout = "false";
                            MyJSONApi.ajouterValeur(jo_dest, "result_dest", "false");
                            MyJSONApi.ajouterValeur(jo_dest, "result_src", ""); // pr palier errror JSON
                            System.out.println("transfert from destination FAILURE");
                        }
                    } catch (IOException e) {
                        System.err.println("Erreur lors de la lecture : " + e);
                        System.exit(-1);
                    }

                    // Envoi du résultat de l'ajout "state_ajout"
                    message = state_ajout;
                    System.out.println("Envoi: " + message);
                    output.println(message);

                    // Lecture de 'end' pour finir l'échange
                    try {
                        message = input.readLine();
                    } catch (IOException e) {
                        System.err.println("Erreur lors de la lecture : " + e);
                        System.exit(-1);
                    }
                    System.out.println("Fin echange Lu: " + message);

                    // Envoi de 'end' pour finir l'échange
                    message = "end";
                    System.out.println("Envoi: " + message);
                    output.println(message);

                    // Fermeture des flux et des sockets
                    try {
                        System.out.println(pp_tmp.getMap_place().get(-6).toString());
                        input.close();
                        output.close();
                        socketClient.close();
                        socketServeur.close();
                    } catch (IOException e) {
                        System.err.println("Erreur lors de la fermeture des flux et des sockets : " + e);
                        System.exit(-1);
                    }

                    /* FIN DE LA SOCKET CONNECTE ******************************************************/

                    // envoie de la réponse
                    // Temporisation d'envoie car le Handler du back Office redémarre le while et remet la socket en écoute
                    try {
                        TimeUnit.SECONDS.sleep(4);
                    } catch (InterruptedException ie) {
                        System.out.println("erreur delay " + ie);
                    }
                    oos.writeObject(jo_dest.toString()); // envoie de la réponse du port source


                    final byte[] utf8Bytes_dest = jo_dest.toString().getBytes(StandardCharsets.UTF_8);
                    System.out.println("\tResult dest -> treated & sended | size = " + utf8Bytes_dest.length);
                    break;
                }
                default:
                    System.err.println("erreur au niveau de l'etat que doit prendre le port pour transferer le bateau ==> !src et !dest ??");
                    break;
            }
        }
        catch (IOException e) {
            System.err.println("\tErreur lors de la sérialisation : " + e);
            System.exit(-1);
        }

        // Création et envoi du segment UDP
        try {
            byte[] donnees = baos.toByteArray();
            InetAddress adresse = InetAddress.getByName("localhost");
            DatagramPacket msg = new DatagramPacket(donnees, donnees.length, adresse, 2087);
            socket2.send(msg);

        }
        catch (UnknownHostException e) {
            System.err.println("\tErreur lors de la création de l'adresse : " + e);
            System.exit(-1);
        }
        catch (IOException e) {
            System.err.println("\tErreur lors de l'envoi du message : " + e);
            System.exit(-1);
        }
        finally {
            socket2.close();
            //System.out.println("\tsocket childThread closed");
            System.out.println("\n> En attente de requete cliente ....\n");
        }
    }
}
