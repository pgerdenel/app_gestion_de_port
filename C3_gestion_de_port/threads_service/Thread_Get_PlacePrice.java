package C3_gestion_de_port.threads_service;
/*
 * Class permettant de gerer le traitement "envoie du prix des places"
 * Ce traitement se contente de renvoyer un objet JSON de l'enum E_Prix

 *
 */
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

public class Thread_Get_PlacePrice implements Runnable {

        public static void main(String args[]) {

        }

        private JSONObject data_parameter;

        public Thread_Get_PlacePrice(String data_parameter) {
            try {
                this.data_parameter = new JSONObject(data_parameter);
            }
            catch(JSONException je) {
                System.out.println("\tThread_Get_PlacePrice() constructor error "+je);
            }
        }

        @Override
        public void run() {

            System.out.println("\t> Thread_Get_PlacePrice RUN() called with data parameter = "+data_parameter);

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

                // On renvoie l'attribut prix_place au format String d'un JSONArray
                try {
                    // On récupère les places libres du port nom_port
                    System.out.println("on récupère prix place avec la réflexivité !!!");
                    // on récupère la class
                    Class<?> port = Class.forName("C3_gestion_de_port.GestionnaireDePort_Srv_Port" + nomport.charAt(nomport.length() - 1));
                    Class<?> parking = Class.forName("C3_gestion_de_port.objects.port.Parking_Port");
                    // on récupère les methodes : getParking, setParking, writeOnlyPortInnJsonFile
                    Method method_getParking = port.getDeclaredMethod("getParking");
                    // on récupère une nouvelle instance de la classe
                    Object i_port = port.newInstance();
                    // on appelle la méthode
                    Parking_Port pp_tmp1 = (Parking_Port) method_getParking.invoke(i_port);
                    oos.writeObject(pp_tmp1.prixToJsonArray().toString());
                }
                catch(ClassNotFoundException | InstantiationException | NoSuchMethodException | IllegalAccessException e) {
                    System.out.println("error "+e);
                } catch(InvocationTargetException e) {
                    System.out.println("error "+e.getTargetException());
                }

                System.out.println("\tPlacePriceList -> treated & sended");

            } catch (IOException e) {
                System.err.println("\tErreur lors de la sérialisation : " + e);
                System.exit(-1);
            }

            // Création et envoi du segment UDP
            try {
                byte[] donnees = baos.toByteArray();
                InetAddress adresse = InetAddress.getByName("localhost");
                DatagramPacket msg = new DatagramPacket(donnees, donnees.length, adresse, 2081);
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
