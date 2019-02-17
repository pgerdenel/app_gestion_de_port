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

public class Thread_Remove_BoatAtPlace implements Runnable {

    public static void main(String args[]) {

    }

    private JSONObject data_parameter;

    // Constructor
    public Thread_Remove_BoatAtPlace(String data_parameter) {
        try {
            this.data_parameter = new JSONObject(data_parameter);
        }
        catch(JSONException je) {
            System.out.println("\tThread_Remove_BoatAtPlace() constructor error "+je);
        }
    }

    @Override
    public void run() {

        System.out.println("\t> Thread_Remove_BoatAtPlace RUN() called with data parameter = "+data_parameter);

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
            JSONObject jo = new JSONObject(); // objet json de réponse
            Boolean b = null;

            try {
                // On vérifie que le bateau est présent dans le port
                System.out.println("on supprime avec la réflexivité !!!");
                // on récupère la class
                Class<?> port = Class.forName("C3_gestion_de_port.GestionnaireDePort_Srv_Port" + nomport.charAt(nomport.length() - 1));
                Class<?> parking = Class.forName("C3_gestion_de_port.objects.port.Parking_Port");
                // on récupère les methodes : getParking, setParking, writeOnlyPortInnJsonFile
                Method method_getParking = port.getDeclaredMethod("getParking");
                Method method_setParking = port.getDeclaredMethod("setParking", Parking_Port.class);
                // on récupère une nouvelle instance de la classe
                Object i_port = port.newInstance();
                // on appelle la méthode
                Parking_Port pp_tmp1 = (Parking_Port) method_getParking.invoke(i_port);

                // on met le nom du bateau qui va être supprimé dans l'objet json
                MyJSONApi.ajouterValeur(jo, "nom_bateau", pp_tmp1.retrieveNomBateau(MyJSONApi.getInt(data_parameter, "longueur"),MyJSONApi.getInt(data_parameter, "num_place")));
                // on supprime le nom_bateau à la list<longueur, num_place> de la map nom_port
                b = pp_tmp1.removeBateauAtPlace(MyJSONApi.getInt(data_parameter, "longueur"),MyJSONApi.getInt(data_parameter, "num_place"));
                // on sauvegarde le port dans le fichier JSON suite aux changements
                pp_tmp1.writeOnlyPortInJSONFile();

                System.out.println("\n\tnew map_parking = "+pp_tmp1.getMap_place().get(-6).toString());

                /*method_setParking.invoke(i_port, pp_tmp1);
                // on appelle la méthode
                Parking_Port pp_tmp2 = (Parking_Port) method_getParking.invoke(i_port);
                System.out.println("\n\tnew map_parking = "+pp_tmp2.getMap_place().get(-6).toString());*/

            }
            catch(ClassNotFoundException | InstantiationException | NoSuchMethodException | IllegalAccessException e) {
                System.out.println("error "+e);
            } catch(InvocationTargetException e) {
                System.out.println("error "+e.getTargetException());
            }


            // on met le résultat de la suppresion dans la réponse et on l'envoie
            MyJSONApi.ajouterValeur(jo, "result", b);
            oos.writeObject(jo.toString());

            final byte[] utf8Bytes = jo.toString().getBytes(StandardCharsets.UTF_8);
            System.out.println("\t> Result -> treated & sended\n\tmessage="+jo.toString()+"\n\tsize = "+utf8Bytes.length);
        }
        catch (IOException e) {
            System.err.println("\tErreur lors de la sérialisation : " + e);
            System.exit(-1);
        }

        // Création et envoi du segment UDP
        try {
            byte[] donnees = baos.toByteArray();
            InetAddress adresse = InetAddress.getByName("localhost");
            DatagramPacket msg = new DatagramPacket(donnees, donnees.length, adresse, 2086);
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
