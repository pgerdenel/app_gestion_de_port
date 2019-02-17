package C3_gestion_de_port.threads_service;

import C3_gestion_de_port.objects.MyJSONApi;
import C3_gestion_de_port.objects.port.Parking_Port;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Thread_Get_BoatInfos implements Runnable {

    public static void main(String args[]) {

    }

    private JSONObject data_parameter;

    // Constructor
    public Thread_Get_BoatInfos(String data_parameter) {
        try {
            this.data_parameter = new JSONObject(data_parameter);
        }
        catch(JSONException je) {
            System.out.println("\tThread_Get_BoatInfos() constructor error "+je);
        }
    }

    @Override
    public void run() {

        System.out.println("\t> Thread_Get_BoatInfos RUN() called with data parameter = "+data_parameter);

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

            String nomport = MyJSONApi.getString(data_parameter, "nom_port");

            String result_type = null; // variable pour indiquer si la réponse contient l'état "data" avec les informations d'un bateau ou l'état "no_data" car le bateau n'exite pas à cette emplacementu
            JSONObject jo = new JSONObject(); // on crée un objet json pour envoyer la réponse


            // on vérifie qu'il existe bien un bateau à cette emplacement
            try {
                System.out.println("on récupère les informations avec la réflexivité !!!");
                // on récupère la class
                Class<?> port = Class.forName("C3_gestion_de_port.GestionnaireDePort_Srv_Port" + nomport.charAt(nomport.length() - 1));
                Class parking = Class.forName("C3_gestion_de_port.objects.port.Parking_Port");
                // on récupère les methodes : getParking, setParking, writeOnlyPortInnJsonFile
                Method method_getParking = port.getDeclaredMethod("getParking");
                // on récupère une nouvelle instance de la classe
                Object i_port = port.newInstance();
                Object i_parking = parking.newInstance();
                // on appelle la méthode
                Parking_Port pp_tmp1 = (Parking_Port) method_getParking.invoke(i_port);

                System.out.println("\n\tnew map_parking = "+ pp_tmp1.getMap_place().get(-6).toString());
                if(pp_tmp1.checkBateauInWithNumPlace(MyJSONApi.getString(data_parameter, "nom_bateau"), MyJSONApi.getInt(data_parameter, "num_place"))) {
                    // on dinque dans la variable result_type qu'il est nécessaire de récupérer les informations d'un bateau car il existe bien
                    result_type = "data";
                    System.out.println("\t> Ce bateau est gare a l'emplacement specifie du port specifie");
                }
                else {
                    // on indique dans la variable result_type qu'il n'est pas nécessaire de récupérer les informations d'un bateau qui n'existe pas
                    result_type = "no_data";
                    System.out.println("\t> Ce bateau n'est pas gare a l'emplacement specifie du port specifie");
                }

            }
            catch(ClassNotFoundException | InstantiationException | NoSuchMethodException | IllegalAccessException e) {
                System.out.println("error "+e);
            }
            /*catch(NoSuchFieldException e) {
                System.out.println("error "+e);
            }*/ catch(InvocationTargetException e) {
                System.out.println("error "+e.getTargetException());
            }

            // on met le résultat dans l'objet de réponse et on l'envoie
            MyJSONApi.ajouterValeur(jo, "result", result_type);
            oos.writeObject(jo.toString());

            // on affiche la taille en Byte de l'objet
            final byte[] utf8Bytes = jo.toString().getBytes(StandardCharsets.UTF_8); // false:20 et true:17
            System.out.println("\t> Result(size="+utf8Bytes.length+") -> treated & sended");
        }
        catch (IOException e) {
            System.err.println("\tErreur lors de la sérialisation : " + e);
            System.exit(-1);
        }

        // Création et envoi du segment UDP
        try {
            byte[] donnees = baos.toByteArray();
            InetAddress adresse = InetAddress.getByName("localhost");
            DatagramPacket msg = new DatagramPacket(donnees, donnees.length, adresse, 2083);
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
