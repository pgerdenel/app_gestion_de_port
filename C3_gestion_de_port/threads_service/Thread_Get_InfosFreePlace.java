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

public class Thread_Get_InfosFreePlace implements Runnable {

    public static void main(String args[]) {

    }

    private JSONObject data_parameter;

    // Constructor
    public Thread_Get_InfosFreePlace(String data_parameter) {
        try {
            this.data_parameter = new JSONObject(data_parameter);
        }
        catch(JSONException je) {
            System.out.println("\tThread_Get_InfosFreePlace() constructor error "+je);
        }
    }

    @Override
    public void run() {

        System.out.println("\t> Thread_Get_InfosFreePlace RUN() called with data parameter = "+data_parameter);

        DatagramSocket socket2 = null;
        ObjectOutputStream oos = null;

        /* INIT UDP_SOCKET_CLIENT ****************************************************************************/
        try {
            socket2 = new DatagramSocket();
            System.out.println("\tSocket Client created");

        } catch(SocketException e) {
            System.err.println("\tErreur lors de la creation de la socket : " + e);
            System.exit(-1);
        }

        // Transformation en tableau d'octets
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            oos = new ObjectOutputStream(baos);

            String nomport = MyJSONApi.getString(data_parameter, "nom_port");

            JSONObject jo_free_place = new JSONObject();

            try {
                // On récupère les places libres du port nom_port
                System.out.println("on récupère place libre avec la réflexivité !!!");
                // on récupère la class
                Class<?> port = Class.forName("C3_gestion_de_port.GestionnaireDePort_Srv_Port" + nomport.charAt(nomport.length() - 1));
                Class parking = Class.forName("C3_gestion_de_port.objects.port.Parking_Port");
                // on récupère les methodes : getParking, setParking, writeOnlyPortInnJsonFile
                Method method_getParking = port.getDeclaredMethod("getParking");
                // on récupère une nouvelle instance de la classe
                Object i_port = port.newInstance();
                // on appelle la méthode
                Parking_Port pp_tmp1 = (Parking_Port) method_getParking.invoke(i_port);
                jo_free_place = pp_tmp1.getFreePlace();
            }
            catch(ClassNotFoundException | InstantiationException | NoSuchMethodException | IllegalAccessException e) {
                System.out.println("error "+e);
            } catch(InvocationTargetException e) {
                System.out.println("error "+e.getTargetException());
            }

            oos.writeObject(jo_free_place.toString());

            // on affiche la taille en Byte de l'objet
            final byte[] utf8Bytes = jo_free_place.toString().getBytes(StandardCharsets.UTF_8); // false:20 et true:17
            System.out.println("\tFreePlaceList(size="+utf8Bytes.length+") -> sended"); // 11

        } catch (IOException e) {
            System.err.println("\tErreur lors de la sérialisation : " + e);
            System.exit(-1);
        }

        // Création et envoi du segment UDP
        try {
            byte[] donnees = baos.toByteArray();
            InetAddress adresse = InetAddress.getByName("localhost");
            DatagramPacket msg = new DatagramPacket(donnees, donnees.length, adresse, 2082);
            socket2.send(msg);
            oos.close();
            baos.close();
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
