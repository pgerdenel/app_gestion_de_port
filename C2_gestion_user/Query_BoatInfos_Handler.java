package C2_gestion_user;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import C2_gestion_user.objects.MyJSONApi;
import org.json.JSONObject;
import java.io.*;
import java.nio.charset.StandardCharsets;

/*
 * Gère la requête HTTP
 * -donne informations sur le bateaux
 *
 * Requête '/query_boat_infos.html'
 * Emetteur : BackOffice
 * paramètres reçues:    nom_user, nom_bateau
 *
 * Réponse 'Ajax_Query_BoatInPort_Handler'
 * Destinataire : BackOffice
 * paramètres renvoyés:  JSONObject { "result": "true or false" }
 *
 */

public class Query_BoatInfos_Handler implements HttpHandler {

    public void handle(HttpExchange t) {
        String reponse ="";

        // Utilisation d'un flux pour lire les donnees du message Http
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8));

            // Recuperation des donnees en POST
            String query = br.readLine(); // On récupère les données nom_user et nom_bateau
            JSONObject infos_bateau_retrieved = new JSONObject();

            System.out.println("\nQuery_BoatInfos_Handler: data recue du Backoffice= "+query);

            JSONObject data = MyJSONApi.castinJSONObject(query);

            // on se rend dans le dossier correspondant au nom_user
            String path_folder_user ="src\\C2_gestion_user\\resources\\proprio\\"+MyJSONApi.getString(data, "nom_user")+"\\";
            String path_folder_user_bateaux = path_folder_user+"bateaux\\"+MyJSONApi.getString(data, "nom_bateau")+"\\";

            File f = new File(path_folder_user_bateaux);

            // on vérifie si le dossier existe
            if (f.exists() && f.isDirectory()) {
                System.out.println("le dossier existe");
                // on vérifie si le fichier nom_bateau.json existe
                if(MyJSONApi.fichierExiste(path_folder_user_bateaux+"bateau.json")) {

                    System.out.println("le fichier existe");
                    // on récupère le contenu du fichier
                    infos_bateau_retrieved = MyJSONApi.retrieveJSONObjectFromFile(path_folder_user_bateaux+"bateau.json");
                    System.out.println("\t> informations du bateaux recuperees "+infos_bateau_retrieved);
                    // on ajoute le champ result_type avec "data" pour préciser que l'on renvoie des données d'un bateau
                    MyJSONApi.ajouterValeur(infos_bateau_retrieved, "result", "data");
                }
                else {
                    //System.out.println("le fichier n'existe pas");
                    // on ajoute le champ result_type avec "nodata" pour préciser que l'on ne renvoie pas de données pr le bateau
                    MyJSONApi.ajouterValeur(infos_bateau_retrieved, "result", "no_data");
                }
            }
            else {
                //System.out.println("le dossier n'existe pas");
                // on ajoute le champ result_type avec "nodata" pour préciser que l'on ne renvoie pas de données pr le bateau
                MyJSONApi.ajouterValeur(infos_bateau_retrieved, "result", "no_data");
            }

            // on charge les données json du fichier bateau.json dans un jsonObject (nom_bateau:données)
            reponse = infos_bateau_retrieved.toString();

            System.out.println("\t> response sended to BackOffice : "+infos_bateau_retrieved);

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

}
