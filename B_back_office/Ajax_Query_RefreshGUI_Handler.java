package B_back_office;

import B_back_office.objects.E_Model_to_Nom;
import B_back_office.objects.MyJSONApi;
import C3_gestion_de_port.objects.port.E_Longueur;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.*;

/*
 * Renvoie les données de l'interface à rafraichir
 * list des ports
 * liste des proprios
 * liste des longueurs
 * liste des modeles
 */

public class Ajax_Query_RefreshGUI_Handler implements HttpHandler {

    @Override
    public void handle(HttpExchange t) {

        // Traitement du flux
        System.out.println("\nAjax_Query_RefreshGUI_Handler called()");

        // Variables de traitement
        JSONObject json_refresh = new JSONObject(); // json objet contenant l'ensemble des données

        // On récupère le JSONArray des ports
        MyJSONApi.ajouterValeur(json_refresh, "list_port", BackOffice_Srv.getListPortJson());
        System.out.println("ListPort added "+MyJSONApi.jsonToString(BackOffice_Srv.getListPortJson(), 1));

        // On récupère le JSONArray de la liste des proprio
        JSONArray jsonArray_proprio = getListProprio();
        MyJSONApi.ajouterValeur(json_refresh, "list_proprio", getListProprio());
        System.out.println("ListProprio added"+ MyJSONApi.jsonToString(jsonArray_proprio, 1));

        // On récupère le JSONArray de liste des modèles
        MyJSONApi.ajouterValeur(json_refresh, "list_modele", E_Model_to_Nom.toJsonArray());
        System.out.println("ListModele added"+ MyJSONApi.jsonToString(E_Model_to_Nom.toJsonArray(), 1));

        // On récupère le JSONArray de la liste des longueurs
        MyJSONApi.ajouterValeur(json_refresh, "list_longueur", E_Longueur.toJsonArray());
        System.out.println("ListLongueur added"+ MyJSONApi.jsonToString(E_Longueur.toJsonArray(), 1));

        String reponse = json_refresh.toString();

        // ENVOIE de la réponse à l'origine de la requête (içi le C1_portail)
        System.out.println("> Envoie de la reponse au portail..." +reponse);

        // Envoi de l'en-tete Http
        try {
            // System.out.println("Envoi de l'en-tete Http");
            Headers h = t.getResponseHeaders();
            h.set("Content-Type", "text/html; charset=utf-8");
            t.sendResponseHeaders(200, reponse.getBytes().length);
        } catch (IOException e) {
            System.err.println("Erreur lors de l'envoi de l'en-tete : " + e);
            System.exit(-1);
        }
        // Envoi du corps (donnees HTML)
        try {
            // System.out.println("Envoi du corps (donnees HTML)");
            OutputStream os = t.getResponseBody();
            os.write(reponse.getBytes());
            os.close();
            System.out.println("> Response envoyee au portail");
        } catch (IOException e) {
            System.err.println("Erreur lors de l'envoi du corps : " + e);
        }
    }

    private JSONArray getListProprio() {
        /*  Construction d'une requête HTTP
         *  Données envoyées : aucune
         *  Destinataire server : Gestionnaire utilisateur
         *  Destinataire handler : Query_ListProprio_Handler
         */
        JSONArray jsonArray = new JSONArray();
        StringBuilder reponse = new StringBuilder();
        URL my_url = null;

        try {
            my_url = new URL("http://localhost:9090/query_list_proprio.html");

            HttpURLConnection con = (HttpURLConnection) my_url.openConnection();

            con.setDoOutput(true);
            //con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Java client");
            con.setRequestProperty("Content-Type", "application/json");

            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {

                String line;

                while ((line = in.readLine()) != null) {
                    reponse.append(line);
                    reponse.append(System.lineSeparator());
                }
            }

            System.out.println("\ngetListProprio(): reponse recue du gestionnaire user= " + reponse);
            jsonArray = MyJSONApi.castinJsonArray(reponse.toString());

        } catch (ConnectException ce) {
            System.out.println("Error |" + ce + "| au serveur GestionUtilisateurs at " + my_url);
            System.exit(-1);
        } catch (MalformedURLException me) {
            System.out.println("Error MalformedURLException " + me);
            System.exit(-1);
        } catch (ProtocolException pe) {
            System.out.println("Error ProtocolException " + pe);
            System.exit(-1);
        } catch (IOException io) {
            System.out.println("Error IOException " + io);
            System.exit(-1);
        }

        return jsonArray;
    }
}
