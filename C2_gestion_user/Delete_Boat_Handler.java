package C2_gestion_user;

import C2_gestion_user.objects.MyJSONApi;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;

/*
 * Gère la requête HTTP
 * -suppression du bateau de l'utilisateur (fichiers json)
 *
 * Requête '/delete_boat.html'
 * Emetteur : BackOffice
 * paramètres reçues:    nom_user, nom_bateau
 *
 * Réponse 'Ajax_Remove_BoatAtPlace_Handler
 * Destinataire : BackOffice
 * paramètres renvoyés:  JSONObject { "result": "true or false" }
 *
 */

public class Delete_Boat_Handler implements HttpHandler {

    public void handle(HttpExchange t) {
        String reponse ="";

        // Utilisation d'un flux pour lire les donnees du message Http
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8));

            // Recuperation des donnees en POST
            String query = br.readLine(); // On récupère les données nom_user et nom_bateau
            JSONObject isRemoved = new JSONObject();

            System.out.println("\nDelete_Boat_Handler: data recue du Backoffice= "+query);
            JSONObject data = MyJSONApi.castinJSONObject(query);

            // on se rend dans le dossier correspondant au nom_user
            String path_folder_user ="src\\C2_gestion_user\\resources\\proprio\\"+MyJSONApi.getString(data, "nom_user")+"\\bateaux\\";
            String path_folder_user_bateaux = path_folder_user+MyJSONApi.getString(data, "nom_bateau");

            // on renvoie true ou false si la création du bateau a été faite ou non
            MyJSONApi.ajouterValeur(isRemoved, "result", deleteDirectory(new File(path_folder_user_bateaux)));

            reponse = isRemoved.toString();

        } catch(IOException e) {
            System.err.println("Erreur lors de la lecture d'une ligne " + e);
            System.exit(-1);
        }

        // Envoi de l'en-tete Http
        try {
            //System.out.println("Envoi de l'en-tete Http");
            Headers h = t.getResponseHeaders();
            h.set("Content-Type", "text/html; charset=utf-8");
            t.sendResponseHeaders(200, reponse.getBytes().length);
        } catch(IOException e) {
            System.err.println("Erreur lors de l'envoi de l'en-tete : " + e);
            System.exit(-1);
        }

        // Envoi du corps (donnees HTML)
        try {
            //System.out.println("Envoi du corps (donnees HTML)");
            OutputStream os = t.getResponseBody();
            os.write(reponse.getBytes());
            os.close();
        } catch(IOException e) {
            System.err.println("Erreur lors de l'envoi du corps : " + e);
        }
    }

    private static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

}
