package C2_gestion_user;

import C2_gestion_user.objects.GestionnaireUtilisateurs;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import C2_gestion_user.objects.bateau.Bateau;
import C2_gestion_user.objects.bateau.carnet_bord.Carnet_Bord;
import C2_gestion_user.objects.bateau.carnet_maintenance.Carnet_maintenance;
import C2_gestion_user.objects.MyJSONApi;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;

/*
 * Gère la requête HTTP
 * -création du bateau de l'utilisateur (objet)
 * -enregistrement de ses informations au format json
 *
 * Requête : '/create_boat.html'
 * Emetteur : BackOffice
 * paramètres reçues:    nom_user, le nom_bateau, la longueur, le modele, le nom_port
 *
 * Réponse : 'Ajax_Add_BoatAtPlace_Handler'
 * Destinataire : BackOffice
 * paramètres renvoyés:  JSONObject { "result": "true or false" }
 *
 */

public class Create_Boat_Handler implements HttpHandler {

    public void handle(HttpExchange t) {
        String reponse ="";

        // Utilisation d'un flux pour lire les donnees du message Http
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8));

            // Recuperation des donnees en POST
            String query = br.readLine(); // On récupère les données nom_user et nom_bateau
            JSONObject isWrited = new JSONObject();

            System.out.println("\nCreate_Boat_Handler: data recue du Backoffice= "+query);

            // on récupère les données de la requête
            JSONObject data = MyJSONApi.castinJSONObject(query);

            // on se rend dans le dossier correspondant au nom_user
            String path_folder_user ="src\\C2_gestion_user\\resources\\proprio\\"+MyJSONApi.getString(data, "nom_user")+"\\bateaux\\";
            String path_folder_user_bateaux = path_folder_user+MyJSONApi.getString(data, "nom_bateau")+"\\";

            // On crée le dossier portant le nom du bateaux
            new File(path_folder_user_bateaux).mkdirs();

            // on crée le bateau
            Bateau b = new Bateau(MyJSONApi.getString(data, "nom_bateau"), MyJSONApi.getString(data, "modele"), MyJSONApi.getString(data, "longueur"), MyJSONApi.getString(data, "nom_user"), new Carnet_maintenance(false, MyJSONApi.getString(data, "modele")), new Carnet_Bord(MyJSONApi.getString(data, "nom_port")));
            //System.out.println("bateau crée "+b.toString());

            // on crée le fichier nom_bateau.json
            MyJSONApi.writeToJSONFile(b.toJson(), path_folder_user_bateaux+"bateau.json");

            // on vérifie si l'option de gardiennage est a true
            // si oui on modifie le fichier.json all_users.json pour mettre gard à tru
            GestionnaireUtilisateurs g = new GestionnaireUtilisateurs(GestionUser_Srv.getPath_all_users());

            // si l'user a son gard à 'chainevide' ou qu'il a son gard a 'false' && que le gard a mettre est a true
            if(MyJSONApi.getString(MyJSONApi.castinJSONObject(MyJSONApi.getString(g.getAll_users(), MyJSONApi.getString(data, "nom_user"))), "gard").equals("") | (MyJSONApi.getString(MyJSONApi.castinJSONObject(MyJSONApi.getString(g.getAll_users(), MyJSONApi.getString(data, "nom_user"))), "gard").equals("false") && MyJSONApi.getString(data, "gard").equals("true"))) {
                // on assigne son gard a true (svg dans le fichier json egalement)
                g.setGard(MyJSONApi.getString(data, "nom_user"), MyJSONApi.getString(data, "gard"));
                System.out.println("on modifie le guard");
            }
            System.out.println("Guard MAJ: pr l'utilisateur "+MyJSONApi.getString(data, "nom_user")+"\n"+g.toString());

            // on renvoie true ou false si la création du bateau a été faite ou non
            MyJSONApi.ajouterValeur(isWrited, "result", "true");

            reponse = isWrited.toString();
            System.out.println("reponse "+reponse);
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
