package C2_gestion_user.objects;

import C2_gestion_user.GestionUser_Srv;
import C3_gestion_de_port.objects.MyJSONApi;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.TimeUnit;

public interface MyAPI {

    /**
     * Vérifie si le backoffice est joignable
     * Rééssaie toutes les 5sec si celui ci ne l'est pas
     * @param host : localhost
     * @param port : port number
     * @return true or false
     */
    static boolean pingHost(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 5);
            return true;
        }
        catch (IOException e) {
            return false; // Either timeout or unreachable or failed DNS lookup.
        }
        finally {
            try {
                TimeUnit.SECONDS.sleep(5);
            }
            catch(InterruptedException iie) {
                System.out.println(""+iie);
            }
        }
    }
    /**
     * Permet de vérifier que la socke de l'autorité de certification est en écoute
     * @param port : port de la socket de l'autorité de certification
     * @return boolean : si oui ou non elle écoute
     */
    static boolean checkSocketListen(int port) {
        try (DatagramSocket s = new DatagramSocket(port)) {
            return true;
        } catch (IOException ex) {
            /* ignore */
        }
        finally {
            try {
                TimeUnit.SECONDS.sleep(5);
            }
            catch(InterruptedException iie) {
                //
            }
        }
        return false;
    }
    /**
     * Vérifie que les clés publiques et certificat sont crées
     * @return boolean
     */
    static boolean checkIfCertEtcOk() {
        return
                C3_gestion_de_port.objects.MyJSONApi.fichierExiste(GestionUser_Srv.getPath()+"cert_gest_user.json") &&
                        C3_gestion_de_port.objects.MyJSONApi.fichierExiste(GestionUser_Srv.getPath()+"private_key.bin") &&
                        C3_gestion_de_port.objects.MyJSONApi.fichierExiste(GestionUser_Srv.getPath()+"public_key.bin") &&
                        MyJSONApi.fichierExiste(GestionUser_Srv.getPath()+"public_key_authority.bin");
    }
}
