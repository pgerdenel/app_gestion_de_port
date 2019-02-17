package B_back_office;

import B_back_office.objects.MyJSONApi;
import B_back_office.objects.security.MySecurity;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ExchangeAESKey_Handler implements HttpHandler {

    public void handle(HttpExchange t) {

        // Utilisation d'un flux pour lire les donnees du message Http
        BufferedReader br = new BufferedReader(new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8));
        String key = MySecurity.generate_key_str();
        System.out.println("key "+key);

        // on enregistre cette clé dans un fichier json
        JSONObject key_json = new JSONObject();
        MyJSONApi.ajouterValeur(key_json, "portail", key);
        MyJSONApi.writeToJSONFile(key_json, BackOffice_Srv.getPath()+"key_aes.json");

        //ENVOIE de la réponse à l'origine de la requête
        System.out.println("> Envoie de la cle à l'entite ...");
        // Envoi de l'en-tete Http
        try {
            // System.out.println("Envoi de l'en-tete Http");
            Headers h = t.getResponseHeaders();
            h.set("Content-Type", "text/html; charset=utf-8");
            t.sendResponseHeaders(200, key.getBytes().length);
        } catch (IOException e) {
            System.err.println("Erreur lors de l'envoi de l'en-tete : " + e);
            System.exit(-1);
        }
        // Envoi du corps (donnees HTML)
        try {
            // System.out.println("Envoi du corps (donnees HTML)");
            OutputStream os = t.getResponseBody();
            os.write(key.getBytes());
            os.close();
            System.out.println("> key envoyee à l'entite");
        } catch (IOException e) {
            System.err.println("Erreur lors de l'envoi du corps : " + e);
        }


    }
}
