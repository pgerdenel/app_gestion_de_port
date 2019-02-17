package B_back_office;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import B_back_office.objects.MyJSONApi;
import org.json.JSONObject;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 *  reçoit une requête RecordPort du Gestionnaire de port à son démarrage pour s'enregistrer
 *  Enregistre le nom du port dans la liste des port du backoffice
 */

public class Record_PortHandler implements HttpHandler{

    private HashMap<String, Integer> map_port;

    public Record_PortHandler(HashMap<String, Integer> map_portp) {
        // there is an implied super() here
        this.map_port = new HashMap<>(map_portp);
    }

    public void handle(HttpExchange t) {

        // Utilisation d'un flux pour lire les donnees du message Http
        StringBuilder reponse = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8));

            // Variables de traitement
            String query = br.readLine(); // on récupère le JSONObjet contenant le nom du port
            reponse = new StringBuilder();
            JSONObject data_reponse = new JSONObject();
            JSONObject jo = MyJSONApi.castinJSONObject(query);

            System.out.println("> Record_PortHandler called: \""+ MyJSONApi.getString(jo, "nom_port")+"\" request to register : ");

            /* Traitement de la requête */
            // On vérifie que le port n'a pas déjà été enregistré au backoffice
            if(!map_port.containsKey(MyJSONApi.getString(jo, "nom_port"))) { // si port non enregistré

                // on ajoute le nom de port a la map_port attribute
                map_port.put(MyJSONApi.getString(jo, "nom_port"), MyJSONApi.getInt(jo, "port"));
                System.out.println("\t> "+MyJSONApi.getString(jo, "nom_port")+ " -> added to map_port");

                // on remplace la ConcurrentHashMap map_port du Backoffice par celle ci
                BackOffice_Srv.setNewPort(MyJSONApi.getString(jo, "nom_port"), MyJSONApi.getInt(jo, "port"));
                // on remplace la map port
                System.out.println("\t> Recorded Port Map Main = "+ BackOffice_Srv.getMap_port().toString()+"\n");

                /* Création de la réponse */
                // On crée le JSONObject contenant l'état d'enregistrement du port
                MyJSONApi.ajouterValeur(data_reponse, "record_state", "true");
            }
            else {
                System.out.println("\t> port already recorded at back office");

                /* Création de la réponse */
                // On crée le JSONObject contenant l'état d'enregistrement du port
                MyJSONApi.ajouterValeur(data_reponse, "record_state", "true");
            }
            // On met cette objet JSON dans la réponse de la précédente requête
            reponse.append(data_reponse);
        }
        catch(IOException io) {
            System.out.println("Error IOException "+io);
            System.exit(-1);
        }

        /* ENVOIE de la réponse à l'origine de la requête (içi le gestionnaire de port) */

        // Envoi de l'en-tete Http
        try {
            //System.out.println("Envoi de l'en-tete Http");
            Headers h = t.getResponseHeaders();
            h.set("Content-Type", "text/html; charset=utf-8");
            t.sendResponseHeaders(200, reponse.toString().getBytes().length);
        } catch(IOException e) {
            System.err.println("Erreur lors de l'envoi de l'en-tete : " + e);
            System.exit(-1);
        }
        // Envoi du corps (donnees HTML)
        try {
            //System.out.println("Envoi du corps (donnees HTML)");
            OutputStream os = t.getResponseBody();
            os.write(reponse.toString().getBytes());
            os.close();
        } catch(IOException e) {
            System.err.println("Erreur lors de l'envoi du corps : " + e);
        }
    }


}
