package C2_gestion_user;

import C2_gestion_user.objects.GestionnaireUtilisateurs;
import C2_gestion_user.objects.MyJSONApi;
import C2_gestion_user.objects.User;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;

/*
 * Gère la requête HTTP
 * -vérifie les identifiants de l'utilisateur
 *
 * Requête '/query_login.html'
 * Emetteur : BackOffice
 * paramètres reçues: login et pass
 *
 * Réponse 'Query_LoginHandler'
 * Destinataire : BackOffice
 * paramètres renvoyés:  String_JSONObject "{\"result\":\"success\", \"type\":\""+type+"\"}"
 *
 */

public class Query_Login_Handler implements HttpHandler {

    public void handle(HttpExchange t) {
        String reponse ="";

        // Utilisation d'un flux pour lire les donnees du message Http
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8));

            String query = br.readLine(); // On récupère l'objet User au format String Json
            System.out.println("\nQuery_Login_Handler: user_json_string recue du Backoffice= "+query);

            // On reconstruit l'objet java depuis le String Json
            User user = new User(MyJSONApi.castinJSONObject(query));

            // on verifie si le login et le pass sont enregistre dans le fichier all_users.json
            GestionnaireUtilisateurs g = new GestionnaireUtilisateurs(GestionUser_Srv.getPath_all_users());

            // On vérifie que le gestionnaire ne renvoie pas une chaine vide lors du getLogin()
            if(!g.getString(user.getLogin()).equals("")) {
                // On vérifie que le mot de passe pour l'user est correct
                try {
                    String pass = new JSONObject(g.getString(user.getLogin())).getString("pass");
                    String type = new JSONObject(g.getString(user.getLogin())).getString("type");
                    if (user.getPass().equals(pass)) {
                        System.out.println("\n\t> identifiants correctes");
                        reponse = "{\"result\":\"success\", \"type\":\""+type+"\"}";
                        user.setType(type);
                        System.out.println("\n\t> user_java= "+user.toString());
                        if(type.equals("p")) {
                            // l'utilisateur est vérifie, on crée son arborescence pour stocker ses bateaux
                            createUserEnv(user.getLogin());
                        }
                    }
                    else {
                        System.out.println("\nPass incorrect");
                        reponse += "{\"result\":\"fail\"}";
                    }
                }
                catch(JSONException je) {
                    System.out.println("Erreur de vérification du mot de passe "+je);
                }
            }
            else {
                System.out.println("\nLogin incorrect");
                reponse += "{\"result\":\"fail\"}";
            }
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

    private void createUserEnv(String nom_user) {
        String path_folder_user ="src\\C2_gestion_user\\resources\\proprio\\"+nom_user+"\\";
        String path_folder_user_bateaux = path_folder_user+"bateaux";
        String path_folder_user_stats = path_folder_user+"stats";

        File f = new File(path_folder_user_bateaux);
        File f2 = new File(path_folder_user_stats);

        // on vérifie si le dossier existe
        if (f.exists() && f.isDirectory() && f2.exists() && f2.isDirectory()) {
            System.out.println("\n\t> User folders & files existing");
        }
        else {
            new File(path_folder_user_bateaux).mkdirs();
            new File(path_folder_user_stats).mkdirs();
            System.out.println("\n\t> User folders & files created");
        }


    }

}
