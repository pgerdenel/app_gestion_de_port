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

public class Thread_Add_BoatAtPlace implements Runnable {

    public static void main(String args[]) {

    }

    private JSONObject data_parameter;

    // Constructor
    public Thread_Add_BoatAtPlace(String data_parameter) {
        try {
            this.data_parameter = new JSONObject(data_parameter);
        }
        catch(JSONException je) {
            System.out.println("\tThread_Add_BoatAtPlace() constructor error "+je);
        }
    }

    @Override
    public void run() {

        System.out.println("\t> Thread_Add_BoatAtPlace RUN() called with data parameter = "+data_parameter);

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

            // Obbjet de réponse et valeur du résultat
            JSONObject jo = new JSONObject();
            Boolean b = null;

            // Brute
            /*// On récupère le ParkingPort du port
            Parking_Port pp_tmp1 = GestionnaireDePort_Srv_Port1.getParking();
            // On ajoute le bateau au parkingport temporaire  et cela vérifie si la place est libre
            b = pp_tmp1.addBateauAtPlace(
                    MyJSONApi.getString(data_parameter, "nom_bateau"),
                    MyJSONApi.getInt(data_parameter, "longueur"),
                    MyJSONApi.getInt(data_parameter, "num_place"));
            // on assigne ce nouveau ParkingPort dans la map_parking
            GestionnaireDePort_Srv_Port1.setParking(pp_tmp1);
            // on sauvegarde le port dans le fichier JSON suite aux changements
            GestionnaireDePort_Srv_Port1.getParking().writeOnlyPortInJSONFile();
            System.out.println("\n\tnew map_parking = "+GestionnaireDePort_Srv_Port1.getParking().getMap_place().get(-6).toString());*/

            //Reflexive
            try {
                System.out.println("on ajoute avec la réflexivité !!!");
                // on récupère la class
                Class<?> port = Class.forName("C3_gestion_de_port.GestionnaireDePort_Srv_Port" + nomport.charAt(nomport.length() - 1));
                Class<?> parking = Class.forName("C3_gestion_de_port.objects.port.Parking_Port");
                // on récupère les methodes : getParking, setParking, writeOnlyPortInnJsonFile
                Method method_getParking = port.getDeclaredMethod("getParking");
                Method method_setParking = port.getDeclaredMethod("setParking", Parking_Port.class);
                Method method_write = parking.getDeclaredMethod("writeOnlyPortInJSONFile");
                // on récupère une nouvelle instance de la classe
                Object i_port = port.newInstance();
                Object i_parking = parking.newInstance();
                // on appelle la méthode
                Parking_Port pp_tmp1 = (Parking_Port) method_getParking.invoke(i_port);
                // On ajoute le bateau au parkingport temporaire  et cela vérifie si la place est libre
                b = pp_tmp1.addBateauAtPlace(
                        MyJSONApi.getString(data_parameter, "nom_bateau"),
                        MyJSONApi.getInt(data_parameter, "longueur"),
                        MyJSONApi.getInt(data_parameter, "num_place"));
                // on assigne ce nouveau ParkingPort dans la map_parking
                method_setParking.invoke(i_port, pp_tmp1);
                // on sauvegarde le port dans le fichier JSON suite aux changements
                Parking_Port pp_tmp2 = (Parking_Port) method_getParking.invoke(i_port);
                pp_tmp2.writeAllPortInJSONFile();
                Parking_Port pp_tmp3 = (Parking_Port) method_getParking.invoke(i_port);
                System.out.println("\n\tnew map_parking = "+pp_tmp3.getMap_place().get(-6).toString());

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
            MyJSONApi.ajouterValeur(jo, "result", String.valueOf(b));
            oos.writeObject(jo.toString());

            // on affiche la taille en Byte de l'objet
            final byte[] utf8Bytes = jo.toString().getBytes(StandardCharsets.UTF_8); // "UTF-8"
            System.out.println("\tResult -> treated & sended | size = "+utf8Bytes.length);
        }
        catch (IOException e) {
            System.err.println("\tErreur lors de la sérialisation : " + e);
            System.exit(-1);
        }

        // Création et envoi du segment UDP
        try {
            byte[] donnees = baos.toByteArray();
            InetAddress adresse = InetAddress.getByName("localhost");
            DatagramPacket msg = new DatagramPacket(donnees, donnees.length, adresse, 2085);
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
            //System.out.println("\tsocket childThread closed");
            socket2.close();
            System.out.println("\n> En attente de requete cliente ....\n");
        }
    }
}
