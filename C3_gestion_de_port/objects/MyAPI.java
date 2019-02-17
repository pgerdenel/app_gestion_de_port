package C3_gestion_de_port.objects;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
                System.out.println("\nBackoffice non joignable, retry in "+ 5 +"sec ...");
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
            s.close();
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
    static boolean checkIfCertEtcOk(String id_port) {
        boolean isExist = false;
        try {
            Class<?> port = Class.forName("C3_gestion_de_port.GestionnaireDePort_Srv_Port" + id_port);
            Method method_getPath = port.getDeclaredMethod("getPath");
            // on récupère une nouvelle instance de la classe
            Object i_port = port.newInstance();

            isExist =  MyJSONApi.fichierExiste(method_getPath.invoke(i_port)+"cert_gest_port"+id_port+".json") &&
                    MyJSONApi.fichierExiste(method_getPath.invoke(i_port)+"private_key.bin") &&
                    MyJSONApi.fichierExiste(method_getPath.invoke(i_port)+"public_key.bin") &&
                    MyJSONApi.fichierExiste(method_getPath.invoke(i_port)+"public_key_authority.bin");
        }
        catch(ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException e) {
            System.out.println("error "+e);
        } catch(InvocationTargetException e) {
            System.out.println("error "+e.getTargetException());
        }


        return isExist;
    }
}
