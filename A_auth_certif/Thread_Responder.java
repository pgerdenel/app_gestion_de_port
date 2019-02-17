package A_auth_certif;

import A_auth_certif.objects.MyJSONApi;
import A_auth_certif.threads_entities.Thread_Cert_Back_Office;
import A_auth_certif.threads_entities.Thread_Cert_Gest_Port;
import A_auth_certif.threads_entities.Thread_Cert_Gest_User;
import A_auth_certif.threads_entities.Thread_Cert_Portail;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.net.DatagramPacket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Permet de lire le message de demande de certificat envoyé par un client
 * et de déléguer les traitements aux threads associés par l'intermédiaire
 * d'un Executor Service
 */
public class Thread_Responder implements Runnable {

    private DatagramPacket msgRecu;

    public Thread_Responder(DatagramPacket msgRecu) {
        this.msgRecu = msgRecu;
    }

    @Override
    public void run() {
        // Recuperation du message du client
        try {
            /* INITIALISATION DES DONNEES ************************************************************************/
            // SingleThreadExecutor : un pool qui ne contient qu'un seul thread. Toutes les tâches soumises sont executees de manière sequentielle
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            JSONObject message_ask_cert = new JSONObject();
            //System.out.println("> Recuperation des donnees de la requete cliente ....");

            try {
                ByteArrayInputStream bais = new ByteArrayInputStream(msgRecu.getData());
                ObjectInputStream ois = new ObjectInputStream(bais);
                message_ask_cert = MyJSONApi.castinJSONObject((String) ois.readObject());
            }
            catch(StreamCorruptedException sce) {
                System.out.println("on recoit une erreur de StreamCorrupted car c'est le portail");
                //System.out.println("données reçues de PHP"+msgRecu.getData());
                ByteArrayInputStream bais = new ByteArrayInputStream(msgRecu.getData());
                java.util.Scanner s = new java.util.Scanner(bais).useDelimiter("\\A");
                StringBuilder sb = new StringBuilder();
                while(s.hasNext()) {
                        String tchar = s.next();
                        if(!tchar.equals("}")) {
                            sb.append(tchar);
                        }
                }
                //System.out.println("data received from php "+sb.toString());
                message_ask_cert = MyJSONApi.castinJSONObject(sb.toString());
                //System.out.println("data php built in JSONObject "+sb.toString());
                System.out.println("data php built in JSONObject port_number "+MyJSONApi.getString(message_ask_cert, "port_number"));
                System.out.println("data php built in JSONObject nom_entite "+MyJSONApi.getString(message_ask_cert, "nom_entite"));
            }

            System.out.println("> Nouvelle demande de certificat called : " + message_ask_cert);

            /* Utiliser un future permet :
             * d'obtenir la valeur de retour (ou null s'il n'y en a pas)
             * d'obtenir l'exception levee par la tâche au cas où celle-ci en a levee une
             * de demander l'annulation de l'execution de la tâche (si celle-ci prend en charge cette fonctionnalite)
             */
            // On cree un nouveau Thread_Service pour la requete et on lui passe les donnees JSON de la requete

            switch (MyJSONApi.getString(message_ask_cert, "nom_entite")) {
                case "gest_user":
                    //map_entite.put("gest_user", false);
                    System.out.println("\t> New Thread_Cert_Gest_User started for certificate");
                    //Future future1 = executorService.submit();
                    new Thread(new Thread_Cert_Gest_User(MyJSONApi.getString(message_ask_cert, "port_number"))).start();
                    break;
                case "back_office":
                    System.out.println("\t> New Thread_Cert_Back_Office started for certificate");
                    //Future future2 = executorService.submit();
                    new Thread(new Thread_Cert_Back_Office(MyJSONApi.getString(message_ask_cert, "port_number"))).start();
                    break;
                case "gest_port":
                    System.out.println("\t> New Thread_Cert_Gest_Port started for certificate");
                    //Future future3 = executorService.submit();
                    new Thread(new Thread_Cert_Gest_Port(MyJSONApi.getString(message_ask_cert, "port_number"), MyJSONApi.getInt(message_ask_cert, "id_port"))).start();
                    break;
                case "portail":
                    System.out.println("\t> New Thread_Cert_Portail started for certificate");
                    //Future future4 = executorService.submit();
                    new Thread(new Thread_Cert_Portail(MyJSONApi.getString(message_ask_cert, "port_number"))).start();
                    break;
            }

        } catch (ClassNotFoundException e) {
            System.err.println("Objet reçu non reconnu : " + e);
            System.exit(-1);
        } catch (IOException e) {
            System.err.println("Erreur lors de la recuperation de l'objet : " + e);
            System.exit(-1);
        }
        /*finally {
            on ne ferme pas la socket pour toujours recevoir les requete de demande de certificat
        }*/
    }
}
